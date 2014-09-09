package com.mycompany.myapp;

import com.cserver.shared.Utils;

public class AppSession {
	public String value = null;
	public long uid = -1;
	public long expires = -1;

	public static AppSession parseString(String json) {
		AppSession result = new JsonTransformer().parse(json, AppSession.class);
		return result;
	}

	public String toString() {
		return new JsonTransformer().render(this);
	}
	
	public static AppSession generate(long uid, long expiresDelta) {
		AppSession session = new AppSession();
		session.value = Utils.getRndString(16);
		session.expires = System.currentTimeMillis() + expiresDelta;
		session.uid = uid;
		return session;
	}
}
