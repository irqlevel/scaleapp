package com.mycompany.myapp.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.HttpConn;
import com.cserver.shared.SLog;
import com.mycompany.myapp.ShardConf;

public class ShardsUploadTest {
    private static final Logger log = LoggerFactory.getLogger(ShardsUploadTest.class);
	public static void main( String[] args ) {
		SLog.startDefault();
		
		try {
			ShardConf conf = ShardConf.generate("node", "db", "usr", "usr_pass", 1, 1, 42);
			HttpConn.put("http", "localhost", 8080, "/shards/" + 1, conf.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("exception=", e);
		}
	}
}
