package com.mycompany.myapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.Utils;


public class User {
    private static final Logger log = LoggerFactory.getLogger(User.class);
    private long id;    
    private String username;
    
    public User() {
    	username = null;
    }
    
    public User(User s){
    	username = s.getUserName();
    }   

    public long getId() {
        return id;
    }

    public String getUserName(){
        return username;
    }
    
    public void setId(long id){
        this.id = id;     
    }
    
    public void setUserName(String s){
        username = s;
    }    
    
    public String getTime() {
    	return Utils.getTimeS(ObjId.getTimeMillis(id));
    }
    
    static public boolean insert(int vsid, User user) {
    	boolean success = false;
    	Connection con = null;
    	PreparedStatement st = null;
    	try {
    		con = SqlCon.getCon(vsid);
    		st = con.prepareStatement("INSERT INTO Users(username) VALUES(?);");
    		st.setString(1, user.username);
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
    
    static public User load(long id) {
    	Connection con = null;
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	User user = null;
    	
    	int vsid = ObjId.getVsid(id);
    	try {
    		con = SqlCon.getCon(vsid);
    		st = con.prepareStatement("SELECT id, username FROM Users WHERE id = ?;");
    		st.setLong(1, id);
    		rs = st.executeQuery();
            if (rs.next()) {
            	user = new User();
            	user.id = rs.getLong(1);
            	user.username = rs.getString(2);
            }
    		con.commit();
    	} catch (SQLException e) {
    		log.error("exception", e);
    	} finally {
    		SqlCon.close(con, st, rs);
    	}
    	
    	return user;
    }
    
    static public List<Long> allIds(int vsid) {
    	Connection con = null;
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	List<Long> ids = new ArrayList<Long>();
    	try {
    		con = SqlCon.getCon(vsid);
    		st = con.prepareStatement("SELECT id FROM Users;");
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
