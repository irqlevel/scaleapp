package com.mycompany.myapp;


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.Utils;


public class DhtKey {
    private static final Logger log = LoggerFactory.getLogger(DhtKey.class);
    private String key = null;
    private String value = null;  
    private String hash = null;
    
    private long id = -1;
    
    private DhtKey() {
    	
    }
    
    public static BigInteger hashKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
    	MessageDigest md = MessageDigest.getInstance("SHA-256");
    	md.update(key.getBytes("UTF-8"));
    	return new BigInteger(md.digest());
    }
    
    public DhtKey(String key, String value) throws UnsupportedEncodingException, NoSuchAlgorithmException {
    	this.key = key;
    	this.value = value;
    	calcHash();
    }
    
    private void calcHash() throws UnsupportedEncodingException, NoSuchAlgorithmException {
    	this.hash = hashKey(this.key).toString();
    }
    
    public String getHash() {
    	return hash;
    }
    
    public String getKey() {
    	return key;
    }
    
    public long getId() {
    	return id;
    }
    
    public String getValue() {
    	return value;
    }

    public String getTime() {
    	return Utils.getTimeS(ObjId.getTimeMillis(id));
    }
    
    public static String FullTableName(String tableName) {
	return "DHT_" + tableName;
    }

    static public boolean insert(String tableName, int vsid, DhtKey key) {
    	Connection con = null;
    	PreparedStatement st = null;
    	boolean success = false;
   	
    	try {
    		con = SqlCon.getCon(vsid);
    		st = con.prepareStatement("INSERT INTO " + FullTableName(tableName) + " (key,value,hash) VALUES(?,?,?);");
    		st.setString(1, key.key);
    		st.setString(2, key.value);
    		st.setString(3, key.hash);
    		st.executeUpdate();
    		con.commit();
    		success = true;
    	} catch (SQLException e) {
    		log.error("exception", e);
    	} finally {
    		SqlCon.close(con, st, null);
    	}
    	
    	return success;
    }
    
    static public boolean update(String tableName, int vsid, String key, String value) {
    	Connection con = null;
    	PreparedStatement st = null;
    	boolean success = false;
   	
    	try {
    		con = SqlCon.getCon(vsid);
    		st = con.prepareStatement("UPDATE " + FullTableName(tableName) + " SET value = ? WHERE key = ?");
    		st.setString(1, value);
    		st.setString(2, key);
    		st.executeUpdate();
    		con.commit();
    		success = true;
    	} catch (SQLException e) {
    		log.error("exception", e);
    	} finally {
    		SqlCon.close(con, st, null);
    	}
    	
    	return success;
    }
    
    static public boolean delete(String tableName, int vsid, String key) {
    	Connection con = null;
    	PreparedStatement st = null;
    	boolean success = false;
   	
    	try {
    		con = SqlCon.getCon(vsid);
    		st = con.prepareStatement("DELETE " + FullTableName(tableName) + " FROM "
				+ FullTableName(tableName) + " WHERE key = ?");
    		st.setString(1, key);
    		st.executeUpdate();
    		con.commit();
    		success = true;
    	} catch (SQLException e) {
    		log.error("exception", e);
    	} finally {
    		SqlCon.close(con, st, null);
    	}
    	
    	return success;
    }
    
    static public DhtKey loadById(String tableName, long id) {
    	Connection con = null;
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	DhtKey found = null;
    	int vsid = ObjId.getVsid(id);
    	try {
    		con = SqlCon.getCon(vsid);
    		st = con.prepareStatement("SELECT id, key, value, hash FROM " + FullTableName(tableName) + " WHERE id = ?;");
    		st.setLong(1, id);
    		rs = st.executeQuery();
            if (rs.next()) {
            	found = new DhtKey();
            	found.id = rs.getLong(1);
            	found.key = rs.getString(2);
            	found.value = rs.getString(3);
            	found.hash = rs.getString(4);
            }
    		con.commit();
    	} catch (SQLException e) {
    		log.error("exception", e);
    	} finally {
    		SqlCon.close(con, st, rs);
    	}
    	
    	return found;
    }
    
    static public DhtKey loadByKey(String tableName, int vsid, String key) {
    	Connection con = null;
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	DhtKey found = null;
    	
    	try {
    		con = SqlCon.getCon(vsid);
    		st = con.prepareStatement("SELECT id, key, value, hash FROM " + FullTableName(tableName) + " WHERE key = ?;");
    		st.setString(1, key);
    		rs = st.executeQuery();
            if (rs.next()) {
            	found = new DhtKey();
            	found.id = rs.getLong(1);
            	found.key = rs.getString(2);
            	found.value = rs.getString(3);
            	found.hash = rs.getString(4);
            }
    		con.commit();
    	} catch (SQLException e) {
    		log.error("exception", e);
    	} finally {
    		SqlCon.close(con, st, rs);
    	}
    	
    	return found;
    }
    
    static public List<Long> allIds(String tableName, int vsid) {
    	Connection con = null;
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	List<Long> ids = new ArrayList<Long>();
    	try {
    		con = SqlCon.getCon(vsid);
		st = con.prepareStatement("SELECT id FROM " + FullTableName(tableName) + ";");
    		rs = st.executeQuery();
            while (rs.next()) {
            	ids.add(rs.getLong(1));
            }
    		con.commit();
    	} catch (SQLException e) {
    		log.error("exception", e);
    	} finally {
    		SqlCon.close(con, st, rs);
    	}
    	
    	return ids;
    }
}

