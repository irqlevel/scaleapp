package com.mycompany.myapp.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.HttpConn;
import com.cserver.shared.SLog;
import com.cserver.shared.Utils;
import com.mycompany.myapp.UserAdd;

public class UserAddTest {
    private static final Logger log = LoggerFactory.getLogger(ShardsUploadTest.class);
	public static void main( String[] args ) {
		SLog.startDefault();
		for (int i = 0; i < 1000; i++) {
			try {
				UserAdd inf = new UserAdd();
				inf.username = Utils.getRndString(8);

				HttpConn.put("http", "0.0.0.0", 49153, "/user/add", inf.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("exception=", e);
			}
		}
	}
}

