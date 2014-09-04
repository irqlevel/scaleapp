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
		for (int i = 0; i < 100; i++) {
			try {
				UserAdd inf = new UserAdd();
				inf.username = Utils.getRndString(8);
				//String out = HttpConn.get("http", "10.30.16.33", 49160, "/");
				//System.out.println("out=" + out);
				HttpConn.put("http", "10.30.16.33", 49160, "/user/add", inf.toString());
				System.out.println("added user=" + inf.username);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("exception=", e);
				System.out.println("exception=" + e.toString());
			}
		}
	}
}

