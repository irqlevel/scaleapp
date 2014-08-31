package com.mycompany.myapp;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rythmengine.RythmEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.INsHttpServerHandler;
import com.cserver.shared.JsonHelper;
import com.cserver.shared.NsHttpCookie;
import com.cserver.shared.NsHttpRequest;
import com.cserver.shared.NsHttpResponse;

public class UriMatcher implements INsHttpServerHandler {
    private static final Logger log = LoggerFactory.getLogger(UriMatcher.class);
    private static volatile UriMatcher instance = null;
    private static volatile RythmEngine rythm = null;
    
    private static final String staticPath = "static";
    private static final String staticUri = "static";
    private static final String templatesPath = "templates";
    
    private Map<Pattern, UriHandler> handlers = new HashMap<Pattern, UriHandler>();
    
	private UriMatcher() {
		
	}
		
	private NsHttpResponse logout(NsHttpRequest request) throws UnsupportedEncodingException {
		NsHttpResponse response = new NsHttpResponse();
		
		
		Map<String, String> result = new HashMap<String, String>();
		result.put("uri", request.getUri());
		result.put("host", request.getHeaders("host"));
		result.put("user-agent", request.getHeaders("User-Agent"));
		
		response.setStatus(NsHttpResponse.OK);
		response.setJson(JsonHelper.mapToString(result));
		
		return response;
	}
	
	private NsHttpResponse profile(NsHttpRequest request, long id) throws UnsupportedEncodingException {
		NsHttpResponse response = new NsHttpResponse();
		
		Map<String, String> result = new HashMap<String, String>();
		result.put("uri", request.getUri());
		result.put("host", request.getHeaders("host"));
		result.put("user-agent", request.getHeaders("User-Agent"));
		result.put("id", Long.toString(id));
		result.put("cookies", JsonHelper.setToJson(request.getCookieKeys()));
		
		try {
			String petrValue = request.getSignedCookie("petr");
			result.put("petrValue", petrValue);
		} catch (Exception e) {
			log.error("cant get signed cookie", e);
		}
		
		response.setStatus(NsHttpResponse.OK);
		response.setJson(JsonHelper.mapToString(result));
		response.addSignedCookie(new NsHttpCookie("petr", "33"));

			
		return response;
	}
	
	
	private NsHttpResponse shards(NsHttpRequest request, int vsid) throws Exception {
		NsHttpResponse response = new NsHttpResponse();
		
		switch (request.getMethod()) {
			case NsHttpRequest.PUT:
				ShardConf conf = ShardConf.loadFromString(new String(request.getContentBytes(), "UTF-8"));
				if (conf.vsid != vsid) {
					throw new Exception("conf.vsid=" + conf.vsid + " vs. vsid=" + vsid);			
				}
				
				if (ShardConfCache.getInstance().put(conf)) {
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
	
	private NsHttpResponse renderTemplate(String name, Map<String, Object> params) throws UnsupportedEncodingException {
		NsHttpResponse response = new NsHttpResponse();
		String content = null;
		File file = new File(templatesPath, name);
		
		if (params != null) {
			content = rythm.render(file, params);
		} else {
			content = rythm.render(file);
		}
		
		response.setStatus(NsHttpResponse.OK);
		response.setContent(content, NsHttpResponse.mimeTypeOfFile(file));
				
		return response;
	}
	
	private NsHttpResponse renderTemplateSimple(String name) throws UnsupportedEncodingException {
		return renderTemplate(name, null);
	}
	
	private NsHttpResponse renderNotFound(NsHttpRequest request) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("uri", request.getUri());
			return renderTemplate("404.html", params);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			log.error("exception=", e);
			return notFound(request);
		}
	}
	
	private NsHttpResponse renderInternalServerError(NsHttpRequest request) {
		try {
			return renderTemplateSimple("500.html");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			log.error("exception=", e);
			return internalServerError(request);
		}
	}
	
	private NsHttpResponse root(NsHttpRequest request) throws UnsupportedEncodingException {
		return renderTemplateSimple("root.html");
	}
	
	private NsHttpResponse contact(NsHttpRequest request) throws UnsupportedEncodingException {
		return renderTemplateSimple("contact.html");
	}

	private NsHttpResponse join(NsHttpRequest request) throws UnsupportedEncodingException {
		return renderTemplateSimple("join.html");
	}
	
	private NsHttpResponse login(NsHttpRequest request) throws UnsupportedEncodingException {
		return renderTemplateSimple("login.html");
	}
	
	private NsHttpResponse staticFiles(NsHttpRequest request, String path) {
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
	
	private void setup() {
		
		rythm = new RythmEngine();
		Map<String, Object> conf = new HashMap<String, Object>();
		conf.put("rythm.engine.mode", "dev");
		conf.put("rythm.home.template.dir", new File(templatesPath).getAbsolutePath());
		rythm = new RythmEngine(conf);
		
		handlers.put(Pattern.compile("^/$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				
				return root(request);
			}});
		
		handlers.put(Pattern.compile("^/contact$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				
				return contact(request);
			}});
		
		handlers.put(Pattern.compile("^/login$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				
				return login(request);
			}});
		
		handlers.put(Pattern.compile("^/logout$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				return logout(request);
			}});
		
		handlers.put(Pattern.compile("^/join$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				return join(request);
			}});
		
		
		handlers.put(Pattern.compile("^/profile/(\\d+)$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws NumberFormatException, UnsupportedEncodingException {
				// TODO Auto-generated method stub
				return profile(request, Long.parseLong(match.group(1)));
			}});

		handlers.put(Pattern.compile("^/" + staticUri + "/(.+)$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				
				return staticFiles(request, match.group(1));
			}});
		
		handlers.put(Pattern.compile("^/shards/(\\d+)$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws Exception {
				// TODO Auto-generated method stub
				return shards(request, Integer.parseInt(match.group(1)));
			}});
	}
	
	public NsHttpResponse handle(NsHttpRequest request) {
		
		try {
			for (Pattern p : handlers.keySet()) {
				Matcher m = p.matcher(request.getUri());
				if (m.matches()) {
					return handlers.get(p).handle(m, request);
				}
			}
		} catch (Exception e) { 
			log.error("exception=", e);
			return renderInternalServerError(request);
		}
		
		return renderNotFound(request);
	}
	
	private NsHttpResponse notFound(NsHttpRequest request) {
		log.info("not found=" + request.getUri());
		NsHttpResponse response = new NsHttpResponse();
		response.setStatus(NsHttpResponse.NOT_FOUND);
		return response;
	}
	
	private NsHttpResponse forbidden(NsHttpRequest request) {
		log.info("forbidden=" + request.getUri());
		NsHttpResponse response = new NsHttpResponse();
		response.setStatus(NsHttpResponse.FORBIDDEN);
		return response;
	}
	
	private NsHttpResponse internalServerError(NsHttpRequest request) {
		log.info("internal server error=" + request.getUri());
		NsHttpResponse response = new NsHttpResponse();
		response.setStatus(NsHttpResponse.INTERNAL_SERVER_ERROR);
		return response;
	}
	
    static public UriMatcher getInstance() {
    	if (instance != null)
    		return instance;
    	synchronized(UriMatcher.class) {
    		if (instance == null) {
    			UriMatcher uriMatcher = new UriMatcher();
   				uriMatcher.setup();
    			instance = uriMatcher;
    		}
    	}
    	return instance;
    }
}
