package com.mycompany.myapp;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.Provider;
import java.security.Security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.CryptLib;
import com.cserver.shared.JsonHelper;
import com.cserver.shared.NsHttpRequest;
import com.cserver.shared.NsHttpResponse;
import com.cserver.shared.NsHttpServer;
import com.cserver.shared.SLog;


public class App 
{
    private static final Logger log = LoggerFactory.getLogger(App.class);
	private static final String TAG = "App";
	private NsHttpServer httpServer = null;
    private static volatile App instance = null;
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

    private void dbStart()
    {
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

    public void start() throws UnknownHostException {
    	log.info("App starting");
    	
    	log.info("My host name is " + InetAddress.getLocalHost().getHostName());
	SLog.startDefault();
    	loadSignKp();
    	dbStart();
    	httpStart();
    	//dbStart();
    	//serverStart();
        log.info("App started");
    }
    
    public static void main( String[] args ) throws UnknownHostException
    {
    	App.getInstance().start();
    }
}
