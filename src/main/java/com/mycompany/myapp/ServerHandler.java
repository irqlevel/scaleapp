package com.mycompany.myapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.INSServerHandler;

public class ServerHandler implements INSServerHandler {

    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);
    
	@Override
	public byte[] handle(byte[] input) {
		return null;
	}
}
