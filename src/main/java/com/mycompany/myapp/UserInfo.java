package com.mycompany.myapp;

public class UserInfo {
	public String username;
	public long uid = -1;

	public static UserInfo parseString(String json) {
		UserInfo result = new JsonTransformer().parse(json, UserInfo.class);
		return result;
	}

	public String toString() {
		return new JsonTransformer().render(this);
	}
}
