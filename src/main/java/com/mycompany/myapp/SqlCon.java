package com.mycompany.myapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlCon {
	private static Map<Integer, PGPoolingDataSource> sourceMap = new TreeMap<Integer, PGPoolingDataSource>();
    private static final Logger log = LoggerFactory.getLogger(User.class);
    
	public static Connection getCon(int vsid) throws SQLException {
		ShardConf conf = ShardConfCache.getInstance().getByVsid(vsid);
		return getCon(conf);
	}
	
	public static Connection getCon(ShardConf conf) throws SQLException {
		PGPoolingDataSource source = null;
		
		if ((source = sourceMap.get(conf.vsid)) == null) {
			synchronized(SqlCon.class) {
				source = sourceMap.get(conf.vsid);
				if (source == null) {
					
					source = new PGPoolingDataSource();
					source.setDataSourceName("shard ds " + conf.vsid);
					source.setServerName(conf.node);
					source.setDatabaseName(conf.db);
					source.setUser(conf.usr);
					source.setPassword(conf.usr_pass);
					source.setMaxConnections(10);
					source.setPortNumber(conf.portNumber);
					
					sourceMap.put(conf.vsid, source);
				}
			}
		}
		
		Connection con = source.getConnection();
		con.setAutoCommit(false);
		return con;
	}
	
	public static void close(Connection con, PreparedStatement st, ResultSet rs) {    	
		if (rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				log.error("exception", e);
			}
		if (st != null)
			try {
				st.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				log.error("exception", e);
			}
		if (con != null)
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				log.error("exception", e);
			}
	} 
}
