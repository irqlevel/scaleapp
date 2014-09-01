package com.cserver.shared;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpHeaders.Names;

public class NsHttpRequest {
	private HttpRequest request = null;
	private List<byte[]> chunks = new ArrayList<byte[]>();
	
	private int method = -1;
	
	public static final int GET = 1;
	public static final int POST = 2;
	public static final int PUT = 3;
	public static final int HEAD = 4;
	public static final int DELETE = 5;
	private static final String TAG = "NsHttpRequest";
	private String contentType = null;
	
	private Map<String, String> cookies = new HashMap<String, String>();
	
	private static Map<HttpMethod, Integer> methodMap = new HashMap<HttpMethod, Integer>();
	
	private static KeyPair signKp = null;
	
	static {
		methodMap.put(HttpMethod.GET, GET);
		methodMap.put(HttpMethod.POST, POST);
		methodMap.put(HttpMethod.HEAD, HEAD);
		methodMap.put(HttpMethod.PUT, PUT);
		methodMap.put(HttpMethod.DELETE, DELETE);
	}
		
	static public void setup(KeyPair signKp) {
		NsHttpRequest.signKp = signKp;
	}
	
	public byte[] getContentBytes() {
		int size = (int) countContentLength();
		byte[] total = new byte[size];
		int pos = 0;
		for (byte[] chunk : chunks) {
			for (int i = 0; i < chunk.length; i++) {
				total[pos++] = chunk[i];
			}
		}
		//SLog.i(TAG, "contentBytes.length=" + total.length);
		
		return total;
	}
	
	public void appendContent(HttpContent chunk) {
		int chunkSize = chunk.content().readableBytes();
		byte[] chunkBytes = new byte[chunkSize];
		ByteBuf bb = Unpooled.wrappedBuffer(chunkBytes);
		bb.setIndex(0, 0);
		chunk.content().readBytes(bb);
		chunks.add(chunkBytes);
		bb.release();
	}
	
	public long countContentLength() {
		long length = 0;
		for (byte[] bytes : chunks) {
			length+= bytes.length;
		}
		return length;
	}
	
	public long getContentLength() {
		return countContentLength();
	}
	
	public String getContentType() {
		if (this.contentType != null) {
			return this.contentType;
		}
		this.contentType = this.request.headers().get(Names.CONTENT_TYPE);
		return this.contentType;
	}

	public NsHttpRequest(HttpRequest request) {
		this.request = request;
		
		getUri();
		getMethod();
		getContentLength();
		getContentType();
		getContentBytes();
		parseCookies();
	}
	
	public HttpRequest getRequest() {
		return this.request;
	}
	
	public String getUri() {
		return this.request.getUri();
	}
	
	public int getMethod() {
		if (method != -1)
			return method;
		
		method = methodMap.get(this.request.getMethod());
		return method;
	}
	
	public String getHeaders(String key) {
		return this.request.headers().get(key);
	}
	
	public void parseCookies() {
		String cookieS = this.request.headers().get("Cookie");
		if (cookieS == null)
			return;
		
		cookieS = cookieS.replaceAll("\\s+","");
		String []kvs = cookieS.split(";");
		for (String kv : kvs) {
			int sep = kv.indexOf("=");
			if (sep == -1)
				continue;
			
			cookies.put(kv.substring(0, sep), kv.substring(sep+1, kv.length()));
		}
	}
	
	public Set<String> getCookieKeys() {
		return cookies.keySet();
	}
	
	public String getCookie(String key) {
		return cookies.get(key);
	}
	
	public 	String getSignedCookie(String key) throws Exception {
		String value = cookies.get(key);
		if (value == null) {
			throw new Exception("No cookie with name=" + key);
		}
		
		NsHttpCookie cookie = new NsHttpCookie(key, value);
		cookie = cookie.signedToPlain(signKp);
		
		return cookie.value;
	}
}
