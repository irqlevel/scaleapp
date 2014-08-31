package com.mycompany.myapp;

import java.util.regex.Matcher;

public interface ClusterNodeHandler {
	public boolean action(Matcher match, String node, int port, int cmd) throws Exception;
}
