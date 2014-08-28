package com.cserver.shared;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;

public class NsHttpCookie {
	public String key = null;
	public String value = null;
	public String domain = null;
	public String path = null;
	public String expires = null;
	
	public NsHttpCookie(String key, String value) {
		set(key, value);
	}
	
	public void set(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String toString() {
		String out = " " + key + "=" + value + ";";
		if (expires != null)
			out+= " expires=" + expires + ";";
		if (path != null)
			out+= " path=" + path + ";";
		if (domain != null)
			out+= " domain=" + domain + ";";
		return out;
	}
	
	
	public NsHttpCookie plainToSigned(KeyPair kp) throws UnsupportedEncodingException
	{
		byte[] rawData = this.value.getBytes("UTF-8");
		String data = Base64.encode(rawData);
		String dataSign = Base64.encode(CryptLib.getInstance().getSign(rawData, kp.getPrivate()));
		
		NsHttpCookie signedCookie = new NsHttpCookie(this.key, data + "<>" + dataSign);
		signedCookie.path = this.path;
		signedCookie.domain = this.domain;
		signedCookie.expires = this.expires;
		
		return signedCookie;
	}
	
	public NsHttpCookie signedToPlain(KeyPair kp) throws Exception {
		String [] words = this.value.split("<>");
		String data = words[0];
		String dataSign = words[1];
		byte [] rdata = Base64.decode(data);
		byte [] rdataSign = Base64.decode(dataSign);
		if (!CryptLib.getInstance().checkSign(rdata, rdataSign, kp.getPublic())) {
			throw new Exception("cant decrypt cookie");
		}
		
		String dataS = new String(rdata, "UTF-8");
		NsHttpCookie cookie = new NsHttpCookie(this.key, dataS);
		cookie.path = this.path;
		cookie.domain = this.domain;
		cookie.expires = this.expires;
		
		return cookie;
	}
}
