package com.mycompany.myapp.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.HttpConn;
import com.cserver.shared.SLog;
import com.mycompany.myapp.UserAdd;

public class UserAddTest {
    private static final Logger log = LoggerFactory.getLogger(ShardsUploadTest.class);
	public static void main( String[] args ) {
		SLog.startDefault();
		
		try {
			UserAdd inf = new UserAdd();
			inf.username = "petr";
			
			HttpConn.put("http", "0.0.0.0", 49153, "/user/add", inf.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("exception=", e);
		}
	}
}

