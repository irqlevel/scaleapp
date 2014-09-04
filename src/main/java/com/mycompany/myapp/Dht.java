package com.mycompany.myapp;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.Base64DecoderException;

public class Dht implements IDht {
    private static final Logger log = LoggerFactory.getLogger(Dht.class);
    private static volatile Dht instance = null;
    private int vsid = -1;
    private ShardConf conf = null;
        
    private Dht() {
    	
    }
    
    private void setup() throws Base64DecoderException {
    	vsid = App.getInstance().getVsid();
    	conf = ShardConfCache.getInstance().getByVsid(vsid);
    }
    
    static public Dht getInstance() {
    	if (instance != null)
    		return instance;
    	synchronized(Dht.class) {
    		if (instance == null) {
    			Dht dht = new Dht();
    			try {
					dht.setup();
				} catch (Base64DecoderException e) {
					// TODO Auto-generated catch block
					log.error("exception", e);
					dht = null;
				}
    			instance = dht;
    		}
    	}
    	return instance;
    }
    
	@Override
	public DhtResult put(String key, String value) {
		// TODO Auto-generated method stub
		DhtResult result = new DhtResult();
		try {
			BigInteger hash = DhtKey.hashKey(key);
			ShardConfNearest nearest = ShardConfCache.getInstance().getNearest(hash);
			ShardConf conf = nearest.conf;
			log.info("put key=" + key + " hash=" + hash + " nearest=" + nearest.id + " vsid=" + conf.vsid);
			
			DhtKey keyO = new DhtKey(key, value);			
			if (DhtKey.insert(conf.vsid, keyO)) {
				result.error = 0;
			}
		} catch (Exception e) {
			log.error("exception", e);
		}
		
		return result;
	}
	
	@Override
	public DhtResult get(String key) {
		// TODO Auto-generated method stub
		DhtResult result = new DhtResult();
		try {
			BigInteger hash = DhtKey.hashKey(key);
			ShardConfNearest nearest = ShardConfCache.getInstance().getNearest(hash);
			ShardConf conf = nearest.conf;
			
			log.info("get key=" + key + " hash=" + hash + " nearest=" + nearest.id + " vsid=" + conf.vsid);
			
			DhtKey found = DhtKey.loadByKey(conf.vsid, key);
			if (found != null) {
				result.error = 0;
				result.value = found.getValue();
				result.id = found.getId();
			}
		} catch (Exception e) {
			log.error("exception", e);
		}
		
		return result;
	}

	@Override
	public DhtResult remove(String key) {
		// TODO Auto-generated method stub
		DhtResult result = new DhtResult();
		try {
			BigInteger hash = DhtKey.hashKey(key);
			ShardConfNearest nearest = ShardConfCache.getInstance().getNearest(hash);
			ShardConf conf = nearest.conf;

			log.info("remove key=" + key + " hash=" + hash + " nearest=" + nearest.id + " vsid=" + conf.vsid);

			if (DhtKey.delete(conf.vsid, key)) {
				result.error = 0;
				result.value = null;
				result.id = 0;
			}
		} catch (Exception e) {
			log.error("exception", e);
		}

		return result;
	}
}
