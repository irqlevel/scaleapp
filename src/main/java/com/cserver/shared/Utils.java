package com.cserver.shared;


import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Utils {

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static long currentTime() {
		return System.currentTimeMillis();
	}
	
	public static String currentTimeS() {
		// TODO Auto-generated method stub
		
	    DateTime dateTime = new DateTime(System.currentTimeMillis(),DateTimeZone.forTimeZone(TimeZone.getDefault()));
	    DateTimeFormatter timeFormater = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss,SSS");
	    
		return timeFormater.print(dateTime);
	}
	
	public static String getTimeS(long time) {
	    DateTime dateTime = new DateTime(time,DateTimeZone.forTimeZone(TimeZone.getDefault()));
	    DateTimeFormatter timeFormater = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss,SSS");
	    
		return timeFormater.print(dateTime);
	}

	public static long parseTimeMillis(String date) {
		DateTimeFormatter timeFormater = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss,SSS");
		return timeFormater.parseMillis(date);
	}
	
	public static long getRndLong() {
        byte []longBytes = new byte[8];
        SecureRandom rng = new SecureRandom();
        rng.nextBytes(longBytes);

        ByteBuffer bf = ByteBuffer.allocate(8);
        bf.put(longBytes);
        bf.position(0);

        return bf.getLong();
	}

	public static long getRndPositiveLong() {
        byte []longBytes = new byte[8];
        SecureRandom rng = new SecureRandom();
        long result = -1;
        
        for (int i = 0; i < 100; i++) {
	        rng.nextBytes(longBytes);
	        ByteBuffer bf = ByteBuffer.allocate(8);
	        bf.put(longBytes);
		    bf.position(0);
		    result = bf.getLong();
		    if (result > 0)
		    	break;
        }

        return result;
	}
	
	public static String getRndString(int numBytes) {
	        SecureRandom random = new SecureRandom();
	    byte bytes[] = new byte[numBytes];
	    random.nextBytes(bytes);
	    String result = null;
	
	    try {
	        result = Base64.encode(bytes);
	        } finally {
	        }
	
	    return result;
	}

}
