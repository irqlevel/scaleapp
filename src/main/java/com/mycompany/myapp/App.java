package com.mycompany.myapp;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.CryptLib;
import com.cserver.shared.JsonHelper;
import com.cserver.shared.NSServer;
import com.cserver.shared.NsHttpRequest;
import com.cserver.shared.NsHttpResponse;
import com.cserver.shared.NsHttpServer;
import com.cserver.shared.SLog;
import com.cserver.shared.Utils;
import com.mycompany.myapp.User;


public class App 
{
    private static final Logger log = LoggerFactory.getLogger(App.class);
	private static final String TAG = "App";
	private NSServer server = null;
	private NsHttpServer httpServer = null;
    private static volatile App instance = null;
    private int vsid = -1;
    private KeyPair signKp = null;
    
	public static App getInstance() {
		if (instance != null)
			return instance;
		
		synchronized(SLog.class) {
			if (instance == null) {
				App app = new App();
				app.setup();
				instance = app;
			}
		}
		
		return instance;
	}
	
	private App() {
	}
	
	private void setup() {
	}
	
	public int getVsid() {
		return -1;
	}
	
	private void slogStart() {
		SLog.start(false, new File("slog.log").getAbsolutePath(), new PostLogger());
		SLog.getInstance().setLogSize(3000000);
		SLog.i(TAG, "slog starting...");
	}
	
    private void serverStart()
    {		
		server  = new NSServer(9111);
		server.setup(new ServerHandler(), null, null, null, null);
		server.start();
		SLog.i(TAG, "server started...");
    }
    
    private void dbUsersTest() {
        User user = null;
        String name = null;
        int vsid = 0;
        for (int i = 0; i < 20; i++) {
	        user = new User();
	        name = "petr" + i;
	        vsid = (int)(Utils.getRndPositiveLong()%2);
	        user.setUserName(name);
	        log.info("inserting " + name + " vsid=" + vsid);
	        User.insert(vsid, user);
	        log.info("inserted " + name);
        }
        
        List<Long> ids = User.allIds(0);
        ids.addAll(User.allIds(1));
        
        Collections.sort(ids);
        for (Long id : ids) {
        	log.info("loading user by id=" + id +  " vsid=" + ObjId.getVsid(id));
        	user = User.load(id);
            log.info("found user=" + user.getUserName() + " id=" + user.getId() + " vsid=" + ObjId.getVsid(user.getId())
            		+ " time=" + user.getTime());
        }
    }
    
    private void dhtTest() 
    {
    	Map<String, String> values = new HashMap<String, String>();
    	for (int i = 0; i < 100; i++) {
    		String key = Utils.getRndString(8);
    		String value = Utils.getRndString(8);
    		values.put(key, value);
    	}
    	
    	for (String key : values.keySet()) {
    		String value = values.get(key);
    		
    		log.info("putting key=" + key + " value=" + value);
    		DhtResult result = Dht.getInstance().put(key, value);
    		log.info("put error=" + result.error + " key=" + key + " value=" + value);
    	}
    	
    	for (String key : values.keySet()) {
    		String value = values.get(key);
    		
    		log.info("getting key=" + key + " value=" + value);
    		DhtResult result = Dht.getInstance().get(key);
    		log.info("get error=" + result.error + " key=" + key + " value=" + result.value);
    	}
    }
    
    private void dbStart()
    {
    	ShardConfCache.getInstance();
    	dhtTest();
    	//dbUsersTest();
    }
    
    private void httpStart() 
    {
    	NsHttpRequest.setup(signKp);
    	NsHttpResponse.setup(signKp);
    	
    	httpServer  = new NsHttpServer(8080);
    	httpServer.setup(UriMatcher.getInstance(), null, null, null, null);
		httpServer.start();
		SLog.i(TAG, "httpServer started...");
    }
    
    public void providersTest() {
    	Provider[] providerList = Security.getProviders();
	    for (int i = 0; i < providerList.length; i++) {
	    	log.info("[" + (i + 1) + "] - Provider name: " + providerList[i].getName());
	    }
    }
    
    public void loadSignKp()
    {
    	CryptLib crypt = CryptLib.getInstance();
    	try {
    		signKp = crypt.getKeysByKs(new File("/home/andrey/mykey.bks"), "1q2w3es5", "mykey", "1q2w3es5");
			log.info("pubKey=" + JsonHelper.publicKeyToString(signKp.getPublic()));
			log.info("privKey=" + JsonHelper.privateKeyToString(signKp.getPrivate()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("exception=", e);
		}
    }

    public void start() {
    	log.info("App starting");
    	slogStart();
    	loadSignKp();
    	httpStart();
    	//dbStart();
    	//serverStart();
        log.info("App started");
    }
    
    public static void main( String[] args )
    {
    	App.getInstance().start();
    }
}
