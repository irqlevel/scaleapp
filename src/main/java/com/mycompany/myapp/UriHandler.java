package com.mycompany.myapp;


import java.util.regex.Matcher;

import com.cserver.shared.NsHttpRequest;
import com.cserver.shared.NsHttpResponse;

public interface UriHandler {
	public NsHttpResponse handle(Matcher match, NsHttpRequest request) throws Exception;
}
