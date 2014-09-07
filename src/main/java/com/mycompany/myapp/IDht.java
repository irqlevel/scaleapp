package com.mycompany.myapp;

public interface IDht {
	public DhtResult put(String tableName, String key, String value) throws Exception ;
	public DhtResult get(String tableName, String key) throws Exception;
	public DhtResult remove(String tableName, String key) throws Exception;
}
