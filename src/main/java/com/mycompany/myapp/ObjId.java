package com.mycompany.myapp;

public class ObjId {
	public static long our_epoch = 1407922045080L;
	
	static public int getVsid(long id) {
		return (int)((id >> 10) & 0x1FFF);
	}
	
	static public long getTimeMillis(long id) {
		return (id >> 23) + our_epoch;
	}
}
