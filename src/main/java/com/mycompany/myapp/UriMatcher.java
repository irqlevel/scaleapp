package com.mycompany.myapp;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.INsHttpServerHandler;
import com.cserver.shared.NsHttpRequest;
import com.cserver.shared.NsHttpResponse;

public class UriMatcher implements INsHttpServerHandler {
    private static final Logger log = LoggerFactory.getLogger(UriMatcher.class);
    private static volatile UriMatcher instance = null;

    private Map<Pattern, UriHandler> handlers = new HashMap<Pattern, UriHandler>();

	private UriMatcher() {

	}

	private void setup() {

		handlers.put(Pattern.compile("^/$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub

				return AppHandlers.root(request);
			}});

		handlers.put(Pattern.compile("^/contact$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				
				return AppHandlers.contact(request);
			}});
		
		handlers.put(Pattern.compile("^/login$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				
				return AppHandlers.login(request);
			}});
		
		handlers.put(Pattern.compile("^/logout$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				return AppHandlers.logout(request);
			}});
		
		handlers.put(Pattern.compile("^/join$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				return AppHandlers.join(request);
			}});
		
		handlers.put(Pattern.compile("^/profile$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws NumberFormatException, UnsupportedEncodingException {
				// TODO Auto-generated method stub
				return AppHandlers.profile(request);
			}});

		handlers.put(Pattern.compile("^/" + AppHandlers.staticUri + "/(.+)$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				
				return AppHandlers.staticFiles(request, match.group(1));
			}});
		
		handlers.put(Pattern.compile("^/shards/(\\d+)$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws Exception {
				// TODO Auto-generated method stub
				return AppHandlers.shards(request, Integer.parseInt(match.group(1)));
			}});
		
		handlers.put(Pattern.compile("^/user/join$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws Exception {
				// TODO Auto-generated method stub
				return AppHandlers.userJoin(request);
			}});
		
		handlers.put(Pattern.compile("^/user/login$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws Exception {
				// TODO Auto-generated method stub
				return AppHandlers.userLogin(request);
			}});
		
		handlers.put(Pattern.compile("^/user/current$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws Exception {
				// TODO Auto-generated method stub
				return AppHandlers.userCurrent(request);
			}});
		
		handlers.put(Pattern.compile("^/user/logout$"), new UriHandler() {
			@Override
			public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws Exception {
				// TODO Auto-generated method stub
				return AppHandlers.userLogout(request);
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
			return AppHandlers.internalServerError(request);
		}
		
		return AppHandlers.notFound(request);
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
