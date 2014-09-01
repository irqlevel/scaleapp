package com.mycompany.myapp;

public class UserAdd {
	public String username;

	public static UserAdd parseString(String json) {
		UserAdd result = new JsonTransformer().parse(json, UserAdd.class);
		return result;
	}
	
	public String toString() {
		return new JsonTransformer().render(this);
	}
}
