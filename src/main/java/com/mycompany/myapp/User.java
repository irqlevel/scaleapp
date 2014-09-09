package com.mycompany.myapp;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cserver.shared.BCrypt;
import com.cserver.shared.JsonHelper;
import com.cserver.shared.Utils;


public class User {
    private static final Logger log = LoggerFactory.getLogger(User.class);
    private long id = -1;
    private String username = null;
    private String hashp = null;
    private AppSession session = null;
    
    public AppSession getSession() {
    	return session;
    }
    public void setSession(AppSession session) {
    	this.session = session;
    }
    
    public void setPassword(String password) {
    	hashp = BCrypt.hashpw(password, BCrypt.gensalt(12, new SecureRandom()));
    }

    public boolean checkPassword(String password) {
    	return BCrypt.checkpw(password, hashp);
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
    
    static public long put(int vsid, User user) {
    	long id = -1;
    	Connection con = null;
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	try {
    		con = SqlCon.getCon(vsid);
    		con.setAutoCommit(true);
    		st = con.prepareStatement("INSERT INTO Users(username, hashp) VALUES(?, ?) RETURNING id;");
    		st.setString(1, user.username);
    		st.setString(2, user.hashp);
    		if (!st.execute()) {
    			throw new SQLException("no result set");
    		}
    		rs = st.getResultSet();
    		if (rs.next()) {
    			id = rs.getLong(1);
    		} else {
    			throw new SQLException("no returning id");
    		}
    	} catch (SQLException e) {
    		log.error("exception", e);
    	} finally {
    		SqlCon.close(con, st, rs);
    	}
    	return id;
    }

    static public boolean delete(long id) {
		int vsid = ObjId.getVsid(id);
		Connection con = null;
		PreparedStatement st = null;
		boolean success = false;

		try {
			con = SqlCon.getCon(vsid);
			st = con.prepareStatement("DELETE FROM Users WHERE id = ?;");
			st.setLong(1, id);
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
    
    static public User get(long id) {
    	Connection con = null;
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	User user = null;
    	
    	int vsid = ObjId.getVsid(id);
    	try {
    		con = SqlCon.getCon(vsid);
    		st = con.prepareStatement("SELECT id, username, hashp FROM Users WHERE id = ?;");
    		st.setLong(1, id);
    		rs = st.executeQuery();
            if (rs.next()) {
            	user = new User();
            	user.id = rs.getLong(1);
            	user.username = rs.getString(2);
            	user.hashp = rs.getString(3);
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
    
    public UserInfo toUserInfo() {
    	UserInfo inf = new UserInfo();
    	inf.username = this.username;
    	inf.uid = this.id;
    	
    	return inf;
    }
}
