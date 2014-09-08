package com.mycompany.myapp;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.Base64DecoderException;
import com.cserver.shared.FileOps;



public class ShardConfCache {
	private Map<Integer, ShardConf> vsidConfMap = new HashMap<Integer, ShardConf>();
    private RedBlackBST<BigInteger, ShardConf> nodeIdConfTree = new RedBlackBST<BigInteger, ShardConf>();

	private static volatile ShardConfCache instance = null;
    private static final Logger log = LoggerFactory.getLogger(ShardConfCache.class);
    private static final String confPath = "conf";
    
	private ShardConfCache() {
		
	}
	
	public int getRandomShard() {
		int vsid;
		synchronized(this) {
			Random generator = new Random(System.currentTimeMillis());
			Integer[] vsids =  (Integer[])vsidConfMap.keySet().toArray(new Integer[vsidConfMap.keySet().size()]);
			vsid = vsids[generator.nextInt(vsids.length)];
		}
		return vsid;
	}
	
	private void __insert(ShardConf conf) throws Base64DecoderException {
		vsidConfMap.put(conf.vsid, conf);
		for (BigInteger id : conf.getIntNodeIds()) {
			nodeIdConfTree.put(id, conf);
			log.info("vsid=" + conf.vsid + " nodeId=" + id);
		}
		__saveConfFile(conf);
	}
	
	private String __fileName(int vsid) {
		return "shardconf_" + Integer.toString(vsid) + ".json";
	}

	private void __saveConfFile(ShardConf conf) throws Base64DecoderException {
		FileOps.writeFile(new File("conf", __fileName(conf.vsid)), conf.toString());
	}
	
	private void __deleteConfFile(int vsid) {
		FileOps.deleteFileRecursive(new File("conf", __fileName(vsid)));
	}
	
	private void __remove(int vsid) throws Base64DecoderException {
		if (vsidConfMap.get(vsid) != null) {
			ShardConf removed = vsidConfMap.remove(vsid);
			if (removed != null) {
				for (BigInteger id : removed.getIntNodeIds()) {
					nodeIdConfTree.delete(id);
				}
			}
		}
		__deleteConfFile(vsid);
	}
	
	public boolean update(ShardConf conf) throws Base64DecoderException
	{
		boolean success = false;
		synchronized(this) {
			log.info("putting conf with vsid=" + conf.vsid);
			__remove(conf.vsid);
			__insert(conf);

			success = true;
		}
		
		return success;
	}
	
	public boolean delete(int vsid) throws Base64DecoderException {
		boolean success = false;
		synchronized(this) {
			log.info("deleting conf with vsid=" + vsid);
			__remove(vsid);
		}
		
		return success;
	}
		
	private void load() throws Base64DecoderException {
		List<String> files = FileOps.getFileNames(FileSystems.getDefault().getPath(confPath));
		for (String file : files) {
			log.info("process file=" + file);
			if (file.matches("^.+shardconf_\\d+\\.json$")) {
				ShardConf conf = ShardConf.loadFromFile(new File(file));
				synchronized(this) {
					__remove(conf.vsid);
					__insert(conf);
				}
			}
		}
	}
	
	public ShardConfNearest getNearest(BigInteger id) {
		ShardConfNearest result = new ShardConfNearest();
		synchronized(this) {
			BigInteger key = nodeIdConfTree.ceiling(id);
			if (key == null) {
				key = nodeIdConfTree.min();
			}
			result.id = key;
			result.conf = nodeIdConfTree.get(key);
		}
		return result;
	}
	
	public ShardConf getByVsid(int vsid) {
		ShardConf conf = null;
		synchronized(this) {
			conf = vsidConfMap.get(vsid);
		}
		return conf;
	}
	
    static public ShardConfCache getInstance() {
    	if (instance != null)
    		return instance;
    	
    	synchronized(Dht.class) {
    		if (instance == null) {
    			ShardConfCache cache = new ShardConfCache();
    			try {
    				cache.load();
    			} catch (Exception e) {
    				log.error("exception", e);
    				cache = null;
    			}
     			instance = cache;
    		}
    	}
    	return instance;
    }
    
}
