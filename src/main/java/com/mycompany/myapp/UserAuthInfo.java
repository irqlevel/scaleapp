package com.mycompany.myapp;

public class UserAuthInfo {
	public String username;
	public String password;

	public static UserAuthInfo parseString(String json) {
		UserAuthInfo result = new JsonTransformer().parse(json, UserAuthInfo.class);
		return result;
	}

	public String toString() {
		return new JsonTransformer().render(this);
	}
}
