package com.mycompany.myapp.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.HttpConn;
import com.cserver.shared.SLog;
import com.cserver.shared.Utils;
import com.mycompany.myapp.UserJoin;

public class UserJoinTest {
    private static final Logger log = LoggerFactory.getLogger(ShardsUploadTest.class);
	public static void main( String[] args ) {
		SLog.startDefault();
		for (int i = 0; i < 2; i++) {
			try {
				UserJoin inf = new UserJoin();
				inf.username = Utils.getRndString(8);
				inf.password = Utils.getRndString(8);
				//String out = HttpConn.get("http", "10.30.16.33", 49160, "/");
				//System.out.println("out=" + out);
				HttpConn.put("http", "192.168.1.6", 49153, "/user/join", inf.toString());
				System.out.println("added user=" + inf.username);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("exception=", e);
				System.out.println("exception=" + e.toString());
			}
		}
	}
}
