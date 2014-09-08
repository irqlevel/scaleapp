package com.cserver.shared;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.HttpHeaders.Names;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;

public class NsHttpResponse {
	public static final int CONTINUE = 100;
	public static final int OK = 200;
	public static final int MOVED_PERMANENTLY = 301;
	public static final int BAD_REQUEST = 400;
	public static final int NOT_FOUND = 404;
	public static final int FORBIDDEN = 403;
	public static final int NOT_IMPLEMENTED = 501;
	public static final int INTERNAL_SERVER_ERROR = 500;
	public static final int SERVICE_UNAVAILABLE = 503;
	
	private String contentType = "text/plain";
	private long contentLength = 0;
	private byte[] content = null;
	
	private int status = NOT_IMPLEMENTED;
		
	private Map<String, String> headers = new HashMap<String, String>();
	private List<NsHttpCookie> cookies = new ArrayList<NsHttpCookie>();
	private static KeyPair signKp = null;
	
	public NsHttpResponse() {
		
	}
	static public void setup(KeyPair signKp) {
		NsHttpResponse.signKp = signKp;
	}
	
	public static String mimeTypeOfFile(File file) {
		if (file.getPath().endsWith(".css"))
			return "text/css";

		if (file.getPath().endsWith(".js"))
			return "application/javascript";
		
		if (file.getPath().endsWith(".html"))
			return "text/html";
		
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		return mimeTypesMap.getContentType(file.getPath());
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public void setContent(String content, String contentType) throws UnsupportedEncodingException {
		setContentBytes(content.getBytes("UTF-8"), contentType);
	}
	
	public void setContentBytes(byte[] content, String contentType) throws UnsupportedEncodingException {
		this.content = content;
		this.contentType = contentType;
	}
	
	public void setFile(File file) {
		this.content = FileOps.readFileBinary(file);
		this.contentType = mimeTypeOfFile(file);
		this.contentLength = this.content.length;
	}
	
	public void setJson(String json) throws UnsupportedEncodingException {
		setContent(json, "application/json");
	}
	
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public void setHeaders(String key, String value) {
		this.headers.put(key, value);
	}
	
	public void addCookie(NsHttpCookie cookie) {
		this.cookies.add(cookie);
	}
	
	public void addSignedCookie(NsHttpCookie cookie) throws UnsupportedEncodingException {		
		cookies.add(cookie.plainToSigned(signKp));
	}
	
	private HttpResponseStatus genStatus()
	{
		switch(status) {
		case CONTINUE:
			return HttpResponseStatus.CONTINUE;
		case OK:
			return HttpResponseStatus.OK;
		case MOVED_PERMANENTLY:
			return HttpResponseStatus.MOVED_PERMANENTLY;
		case BAD_REQUEST:
			return HttpResponseStatus.BAD_REQUEST;
		case NOT_FOUND:
			return HttpResponseStatus.NOT_FOUND;
		case FORBIDDEN:
			return HttpResponseStatus.FORBIDDEN;
		case NOT_IMPLEMENTED:
			return HttpResponseStatus.NOT_IMPLEMENTED;
		case INTERNAL_SERVER_ERROR:
			return HttpResponseStatus.INTERNAL_SERVER_ERROR;
		case SERVICE_UNAVAILABLE:
			return HttpResponseStatus.SERVICE_UNAVAILABLE;
		
		default:
			return HttpResponseStatus.INTERNAL_SERVER_ERROR;
		}
	}
	
	public HttpResponse getResponse() {
		
		FullHttpResponse response = null;
		
		if (content != null) {
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, 
					HttpResponseStatus.OK, Unpooled.wrappedBuffer(content));
			contentLength = response.content().readableBytes();
		} else {
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, 
					HttpResponseStatus.OK);

			contentLength = 0;
		}
		
		for (String key : headers.keySet()) {
			response.headers().set(key, headers.get(key));
		}
		
		for (NsHttpCookie cookie : cookies) {
			response.headers().add("Set-Cookie", cookie.toString());
		}
		
		response.setStatus(genStatus());
		response.headers().set(Names.CONTENT_TYPE, contentType);
		response.headers().set(Names.CONTENT_LENGTH, contentLength);
		
		return response;
	}
}
