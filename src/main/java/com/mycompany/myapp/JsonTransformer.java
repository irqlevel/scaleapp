package com.mycompany.myapp;

import com.google.gson.Gson;


public class JsonTransformer {
	private Gson gson = new Gson();

	public <T> T parse(String json, Class<T> cls) {
		return gson.fromJson(json, cls);
	}
	
	public String render(Object model) {
       		return gson.toJson(model);
    }

}

