package com.mycompany.myapp;

public class UserJoin {
	public String username;
	public String password;

	public static UserJoin parseString(String json) {
		UserJoin result = new JsonTransformer().parse(json, UserJoin.class);
		return result;
	}

	public String toString() {
		return new JsonTransformer().render(this);
	}
}
