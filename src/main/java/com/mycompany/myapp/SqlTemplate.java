package com.mycompany.myapp;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.rythmengine.RythmEngine;

import com.cserver.shared.FileOps;

public class SqlTemplate {
	private static File sqlFilesPath = new File(new File("scripts"), "db").getAbsoluteFile();
	private static RythmEngine rythm = null;
	
	static {
		rythm = new RythmEngine();
		Map<String, Object> conf = new HashMap<String, Object>();
		conf.put("rythm.engine.mode", "dev");
		conf.put("rythm.home.template.dir", sqlFilesPath.getAbsolutePath());
		rythm = new RythmEngine(conf);
	}
	
	public static String render(String file, Map<String, Object> params) {		
		return rythm.render(FileOps.readFile(new File(sqlFilesPath, file)), params);
	}
}
