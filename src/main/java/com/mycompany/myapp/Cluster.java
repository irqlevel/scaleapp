package com.mycompany.myapp;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.SLog;
import com.cserver.shared.HttpConn;

public class Cluster {
	private static final String TAG = "Cluster";
    private static final Logger log = LoggerFactory.getLogger(Cluster.class);
    private static Map<Pattern, ClusterNodeHandler> handlers = new HashMap<Pattern, ClusterNodeHandler>();
    public static final int PRE_SETUP = 1;
    public static final int SETUP = 2;
    private static Map<Integer, ShardConf> shards = new TreeMap<Integer, ShardConf>();
    
	static private void slogStart() {
		SLog.start(false, new File("slog.log").getAbsolutePath(), new PostLogger());
		SLog.getInstance().setLogSize(3000000);
		SLog.i(TAG, "slog starting...");
	}
	
	public static boolean setupApp(String node, int id, int port) {
		boolean success = false;
		log.info("setup node=" + node + " id=" + id + " port=" + port);
		
		for (Integer vsid : shards.keySet()) {
			ShardConf conf = shards.get(vsid);
			try {
				HttpConn.put("http", node, port, "/shards/" + vsid, conf.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("exception=", e);
			}
		}
		
		return success;
	}
	
	public static boolean setupDb(String node, int id, int port) {
		boolean success = false;
		log.info("setup db=" + node + " id=" + id + " port=" + port);
		
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = SqlCon.getCon(shards.get(id));
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("vsid", id);
			st = con.prepareStatement(SqlTemplate.render("db_init.sql", params));
			st.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error("exception", e);
		} finally {
			SqlCon.close(con, st, null);
		}
		
		return success;
	}
	
	public static boolean preSetupDb(String node, int id, int port) throws UnsupportedEncodingException {
		boolean success = false;
		log.info("presetup db=" + node + " id=" + id + " port=" + port);
		ShardConf conf = ShardConf.generate(node, "docker", "docker", "docker", id, 1, port);
		
		shards.put(conf.vsid, conf);
		log.info("conf=" + conf.toString());
		//conf = ShardConf.loadFromString(conf.toString());
		//log.info("conf=" + conf.toString());
		
		success = true;
		return success;
	}
	
	static {
		handlers.put(Pattern.compile("^app(\\d+)\\..+$"), new ClusterNodeHandler() {
			@Override
			public boolean action(Matcher match, String node, int port, int cmd) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				boolean success = false;
				int id = Integer.parseInt(match.group(1));
				
				switch (cmd) {
				case SETUP:
					success = setupApp(node, id, port);
					break;
				default:
					break;
				}
				return success;
			}});
		
		handlers.put(Pattern.compile("^db(\\d+)\\..+$"), new ClusterNodeHandler() {
			@Override
			public boolean action(Matcher match, String node, int port, int cmd) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				boolean success = false;
				int id = Integer.parseInt(match.group(1));
				
				switch (cmd) {
				case PRE_SETUP:
					success = preSetupDb(node, id, port);
					break;
				case SETUP:
					success = setupDb(node, id, port);
					break;
				default:
					break;
				}
				return success;
			}});
	}
	
	public static boolean action(String node, int cmd) {
		Pattern np = Pattern.compile("^(.+):(\\d+)$");
		
		try {
			Matcher m = np.matcher(node);
			if (m.matches()) {
				node = m.group(1);
				int port = Integer.parseInt(m.group(2));
				for (Pattern p : handlers.keySet()) {
					m = p.matcher(node);
					if (m.matches()) {
						return handlers.get(p).action(m, node, port, cmd);
					}
				}
			}
			throw new Exception("no match for=" + node);
		} catch (Exception e) { 
			log.error("exception=", e);
		}
		
		return false;
	}

	
	public static void main( String[] args ) {
		String [] nodes = {"app1.scaleapp_img.dev.docker:8080", "app2.scaleapp_img.dev.docker:8080",
				"app3.scaleapp_img.dev.docker:8080", "db1.pgsql_img.dev.docker:5432", "db2.pgsql_img.dev.docker:5432",
				"db3.pgsql_img.dev.docker:5432"};
		
		slogStart();

		Integer [] cmds = {PRE_SETUP, SETUP};
		for (Integer cmd : cmds) {
			for (String node : nodes) {
				action(node, cmd);
			}
		}
	}
}
