package com.mycompany.myapp;

public interface IDht {
	public DhtResult put(String key, String value) throws Exception ;
	public DhtResult get(String key) throws Exception;
}
