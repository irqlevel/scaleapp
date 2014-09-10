package com.mycompany.myapp;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.rythmengine.RythmEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.JsonHelper;
import com.cserver.shared.NsHttpCookie;
import com.cserver.shared.NsHttpRequest;
import com.cserver.shared.NsHttpResponse;

public class AppHandlers {
    private static final Logger log = LoggerFactory.getLogger(AppHandlers.class);

    public static final String staticPath = "static";
    public static final String staticUri = "static";
    public static final String templatesPath = "templates";

    public static final String sessionCookieName = "AUTH";
    private static volatile RythmEngine engine = null;
    
	public static NsHttpResponse logout(NsHttpRequest request) throws UnsupportedEncodingException {
		NsHttpResponse response = new NsHttpResponse();


		Map<String, String> result = new HashMap<String, String>();
		result.put("uri", request.getUri());
		result.put("host", request.getHeaders("host"));
		result.put("user-agent", request.getHeaders("User-Agent"));

		response.setStatus(NsHttpResponse.OK);
		response.setJson(JsonHelper.mapToString(result));

		return response;
	}

	public static NsHttpResponse profile(NsHttpRequest request) throws UnsupportedEncodingException {
		User user = userAuth(request);
		if (user == null)
			return renderNotFound(request);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user", user.toUserInfo());
		
		return renderTemplate("profile.html", map);
	}

	public static NsHttpResponse shards(NsHttpRequest request, int vsid) throws Exception {
		NsHttpResponse response = new NsHttpResponse();

		switch (request.getMethod()) {
			case NsHttpRequest.PUT:
				ShardConf conf = ShardConf.loadFromString(new String(request.getContentBytes(), "UTF-8"));
				log.info("loading conf=" + conf.toString());

				if (conf.vsid != vsid) {
					throw new Exception("conf.vsid=" + conf.vsid + " vs. vsid=" + vsid);
				}

				if (ShardConfCache.getInstance().update(conf)) {
					response.setStatus(NsHttpResponse.OK);
				} else {
					log.error("not found shard with vsid=" + vsid);
					response.setStatus(NsHttpResponse.INTERNAL_SERVER_ERROR);
				}

				break;
			case NsHttpRequest.DELETE:
				if (ShardConfCache.getInstance().delete(vsid)) {
					response.setStatus(NsHttpResponse.OK);
				} else {
					log.error("not found shard with vsid=" + vsid);
					response.setStatus(NsHttpResponse.NOT_FOUND);
				}
				break;
			default:
				throw new Exception("unsupported method=" + request.getMethod());
		}

		return response;
	}

	public static NsHttpResponse userJoin(NsHttpRequest request) throws Exception {
		NsHttpResponse response = new NsHttpResponse();
		switch (request.getMethod()) {
			case NsHttpRequest.PUT:
				AppResult result = new AppResult();
				UserAuthInfo inf = UserAuthInfo.parseString(new String(request.getContentBytes(), "UTF-8"));
				log.info("adding user=" + inf.toString());
				User user = new User();
				user.setUserName(inf.username);
				user.setPassword(inf.password);
				long uid = User.put(ShardConfCache.getInstance().getRandomShard(), user);
				if (uid == -1) {
					log.error("cant put user");
					result.setError(AppError.ACCOUNT_ALREADY_REGISTRED);
					response.setJson(result.toString());
					response.setStatus(NsHttpResponse.OK);
					return response;
				}
				log.info("saving username");
				DhtResult rs = Dht.getInstance().put("USERNAME", inf.username, Long.toString(uid));
				if (rs.error != 0) {
					log.error("cant put username");
					if (uid != -1)
						User.delete(uid);
					result.setError(AppError.ACCOUNT_ALREADY_REGISTRED);
					response.setJson(result.toString());
					response.setStatus(NsHttpResponse.OK);
					return response;
				}

				log.info("user=" + user.getUserName() + " was added, id=" + uid);
				result.setError(AppError.SUCCESS);
				result.setUid(uid);
				response.setJson(result.toString());
				response.setStatus(NsHttpResponse.OK);
				break;
			default:
				throw new Exception("unsupported method=" + request.getMethod());
		}

		return response;
	}

	public static RythmEngine getTemplateRender() {
		synchronized(AppHandlers.class) {
			if (engine == null) {
				engine = new RythmEngine();
				Map<String, Object> conf = new HashMap<String, Object>();
				conf.put("rythm.engine.mode", "dev");
				conf.put("rythm.home.template.dir", new File(templatesPath).getAbsolutePath());
				engine = new RythmEngine(conf);
			}
		}
		
		return engine;
	}

	public static NsHttpResponse renderTemplate(String name, Map<String, Object> params) throws UnsupportedEncodingException {
		NsHttpResponse response = new NsHttpResponse();
		String content = null;
		File file = new File(templatesPath, name);

		if (params != null) {
			content = getTemplateRender().render(file, params);
		} else {
			content = getTemplateRender().render(file);
		}

		response.setStatus(NsHttpResponse.OK);
		response.setContent(content, NsHttpResponse.mimeTypeOfFile(file));

		return response;
	}

	public static NsHttpResponse renderTemplateSimple(String name) throws UnsupportedEncodingException {
		return renderTemplate(name, null);
	}

	public static NsHttpResponse renderNotFound(NsHttpRequest request) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("uri", request.getUri());
			NsHttpResponse response = renderTemplate("404.html", params);
			response.setStatus(NsHttpResponse.NOT_FOUND);
			return response;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			log.error("exception=", e);
			return notFound(request);
		}
	}

	public static NsHttpResponse renderInternalServerError(NsHttpRequest request) {
		try {
			NsHttpResponse response = renderTemplateSimple("500.html");
			response.setStatus(NsHttpResponse.INTERNAL_SERVER_ERROR);
			return response;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			log.error("exception=", e);
			return internalServerError(request);
		}
	}

	public static NsHttpResponse root(NsHttpRequest request) throws UnsupportedEncodingException {
		return renderTemplateSimple("root.html");
	}

	public static NsHttpResponse contact(NsHttpRequest request) throws UnsupportedEncodingException {
		return renderTemplateSimple("contact.html");
	}

	public static NsHttpResponse join(NsHttpRequest request) throws UnsupportedEncodingException {
		return renderTemplateSimple("join.html");
	}

	public static NsHttpResponse login(NsHttpRequest request) throws UnsupportedEncodingException {
		return renderTemplateSimple("login.html");
	}

	public static NsHttpResponse staticFiles(NsHttpRequest request, String path) {
		File file = new File(staticPath, path);
		if (!file.exists())
			return notFound(request);

		if (!file.isFile())
			return forbidden(request);

		NsHttpResponse response = new NsHttpResponse();
		response.setFile(file);
		response.setStatus(NsHttpResponse.OK);

	    return response;
	}

	public static NsHttpResponse notFound(NsHttpRequest request) {
		log.info("not found=" + request.getUri());
		NsHttpResponse response = new NsHttpResponse();
		response.setStatus(NsHttpResponse.NOT_FOUND);
		return response;
	}

	public static NsHttpResponse forbidden(NsHttpRequest request) {
		log.info("forbidden=" + request.getUri());
		NsHttpResponse response = new NsHttpResponse();
		response.setStatus(NsHttpResponse.FORBIDDEN);
		return response;
	}

	public static NsHttpResponse internalServerError(NsHttpRequest request) {
		log.info("internal server error=" + request.getUri());
		NsHttpResponse response = new NsHttpResponse();
		response.setStatus(NsHttpResponse.INTERNAL_SERVER_ERROR);
		return response;
	}

	public static User userAuth(NsHttpRequest request) {
		String sessionValue = request.getHeaders("X-SESSION-TOKEN");
		if (sessionValue == null)
			return null;
		
		DhtResult rs = Dht.getInstance().get("SESSION", sessionValue);
		if (rs.error != AppError.SUCCESS)
			return null;
		
		AppSession session = AppSession.parseString(rs.value);
		User user = User.get(session.uid);
		if (user == null)
			return null;
		
		user.setSession(session);
		return user;
	}
	
	public static NsHttpResponse userLogin(NsHttpRequest request) throws Exception {
		NsHttpResponse response = new NsHttpResponse();
		switch (request.getMethod()) {
			case NsHttpRequest.POST:
				AppResult result = new AppResult();
				
				UserAuthInfo inf = UserAuthInfo.parseString(new String(request.getContentBytes(), "UTF-8"));
				log.info("loging user=" + inf.toString());
				DhtResult rs = Dht.getInstance().get("USERNAME", inf.username);
				if (rs.error != AppError.SUCCESS) {
					result.setError(AppError.ACCOUNT_NOT_FOUND);
					response.setJson(result.toString());
					response.setStatus(NsHttpResponse.OK);
					return response;
				}
				
				long uid = Long.parseLong(rs.value);
				User user = User.get(uid);
				if (user == null) {
					result.setError(AppError.ACCOUNT_NOT_FOUND);
					response.setJson(result.toString());
					response.setStatus(NsHttpResponse.OK);
					return response;
				}
				
				if (!user.checkPassword(inf.password)) {
					result.setError(AppError.ACCOUNT_LOGIN_OR_PASSWORD_INVALID);
					response.setJson(result.toString());
					response.setStatus(NsHttpResponse.OK);
					return response;
				}
				
				AppSession session = AppSession.generate(user.getId(), 24*60*60*1000); //1 day
				rs = Dht.getInstance().put("SESSION", session.value, session.toString());
				if (rs.error != AppError.SUCCESS) {
					result.setError(AppError.INTERNAL_SERVER_ERROR);
					response.setJson(result.toString());
					response.setStatus(NsHttpResponse.OK);
					return response;
				}
					
				log.info("user " + user.getUserName() + " logged session=" + session.value);
				
				result.setError(AppError.SUCCESS);
				result.setUid(uid);
				result.setToken(session.value);
				response.setJson(result.toString());
				response.setStatus(NsHttpResponse.OK);
				break;
			default:
				throw new Exception("unsupported method=" + request.getMethod());
		}

		return response;
	}

	
	public static NsHttpResponse userCurrent(NsHttpRequest request) throws Exception {
		NsHttpResponse response = new NsHttpResponse();
		switch (request.getMethod()) {
			case NsHttpRequest.GET:
				AppResult result = new AppResult();
				
				User user = userAuth(request);
				if (user == null) {
					result.setError(AppError.ACCOUNT_NOT_FOUND);
					response.setJson(result.toString());
					response.setStatus(NsHttpResponse.OK);
					return response;
				}
				
				result.setError(AppError.SUCCESS);
				result.setUser(user.toUserInfo());
				result.setUid(user.getId());
				response.setJson(result.toString());
				response.setStatus(NsHttpResponse.OK);
				break;
			default:
				throw new Exception("unsupported method=" + request.getMethod());
		}

		return response;
	}
	
	public static NsHttpResponse userLogout(NsHttpRequest request) throws Exception {
		NsHttpResponse response = new NsHttpResponse();
		switch (request.getMethod()) {
			case NsHttpRequest.POST:
				AppResult result = new AppResult();
				
				User user = userAuth(request);
				if (user == null) {
					result.setError(AppError.ACCOUNT_NOT_FOUND);
					response.setJson(result.toString());
					response.setStatus(NsHttpResponse.OK);
					return response;
				}

				Dht.getInstance().remove("SESSION", user.getSession().value);
				result.setError(AppError.SUCCESS);
				response.setJson(result.toString());
				response.setStatus(NsHttpResponse.OK);
				break;
			default:
				throw new Exception("unsupported method=" + request.getMethod());
		}

		return response;
	}
}
