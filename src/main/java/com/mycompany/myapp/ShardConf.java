package com.mycompany.myapp;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.Base64;
import com.cserver.shared.Base64DecoderException;
import com.cserver.shared.FileOps;
import com.cserver.shared.JsonHelper;

public class ShardConf {
	private static final Logger log = LoggerFactory.getLogger(ShardConf.class);
	public String db = null;
	public String usr = null;
	public Integer vsid = null;
	public String usr_pass = null;
	public String node = null;
	public String nodeIds = null;
	public List<String> nodeIdsList = new ArrayList<String>();
	
	
	public List<BigInteger> getIntNodeIds() throws Base64DecoderException {
		List<BigInteger> iids = new ArrayList<BigInteger>();
		for (String id : nodeIdsList) {
			iids.add(new BigInteger(Base64.decode(id)));
		}
		
		return iids;
	}
	
	public static ShardConf loadFromFile(File file) {
		String json = FileOps.readFile(file);
		ShardConf conf = new JsonTransformer().parse(json, ShardConf.class);
		conf.nodeIdsList = JsonHelper.stringToStringList(conf.nodeIds);
		return conf;
	}
}
