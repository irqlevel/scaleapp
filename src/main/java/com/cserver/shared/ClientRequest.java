package com.cserver.shared;

import java.util.HashMap;
import java.util.Map;



public	class ClientRequest implements IMapDumpable {
	private static final String TAG = "ClientRequest";
	public int type = -1;
	public int status = -1;
	public byte[] data = null;
	
	public String clientId = null;
	public String authId = null;
	public String hostId = null;
	
	public String userSid = null;
	public String userName = null;
	public String programName = null;
	public String windowTitle = null;
	public long localTime = -1;
	public long serverTime = -1;
	
	public int sessionId = -1;
	public int pid = -1;
	public int tid = -1;
	
	public static final int TYPE_BASE = 0x900;
	public static final int TYPE_UNDEFINED = TYPE_BASE+1;
	public static final int TYPE_ECHO = TYPE_BASE+2;
	public static final int TYPE_KEYBRD = TYPE_BASE+3;
	public static final int TYPE_SCREENSHOT = TYPE_BASE+4;
	public static final int TYPE_USER_WINDOW = TYPE_BASE+5;
	
	public static final int SERROR_SUCCESS = 0x0;
    public static final int SERROR_ERROR = 0xD00E0000;
    public static final int SERROR_UNDEFINED = SERROR_ERROR + 1;
    public static final int SERROR_NOT_SUPPORTED = SERROR_ERROR + 2;
    public static final int SERROR_JSON_DUMPS = SERROR_ERROR + 3;
    public static final int SERROR_NO_MEM = SERROR_ERROR + 4;
    public static final int SERROR_NO_RESPONSE = SERROR_ERROR + 5;
    public static final int SERROR_SERVER_ERROR = SERROR_ERROR + 6;
    public static final int SERROR_AUTH_ERROR = SERROR_ERROR + 7;
    public static final int SERROR_ACCESS_DENIED = SERROR_ERROR + 8;
    public static final int SERROR_NOT_IMPLEMENTED = SERROR_ERROR + 9;
    public static final int SERROR_OBJ_NOT_FOUND = SERROR_ERROR + 10;
    public static final int SERROR_OBJ_ALREADY_EXISTS = SERROR_ERROR + 11;
    public static final int SERROR_JSON_LOADS = SERROR_ERROR + 12;
    public static final int SERROR_INVALID_HEADER = SERROR_ERROR + 13;
    public static final int SERROR_INVALID_BODY = SERROR_ERROR + 14;
    public static final int SERROR_INVALID_DATA = SERROR_ERROR + 15;
    
    
	public ClientRequest()
	{
		type = TYPE_UNDEFINED;
		status = SERROR_UNDEFINED;
		data = null;
	}
	
	public ClientRequest(int status)
	{
		this.type = TYPE_UNDEFINED;
		this.status = status;
		this.data = null;
	}
	
	@Override
	public Map<String, String> toMap() {
		// TODO Auto-generated method stub
		Map<String, String> map = new HashMap<String, String>();
		map.put("type", Integer.toString(type));
		map.put("status", Integer.toString(status));
		
		if (data != null)
			map.put("data", Base64.encode(data));
		
		map.put("clientId", clientId);
		map.put("authId", authId);
		map.put("hostId", hostId);
		
		
		map.put("sessionId", Integer.toString(sessionId));
		map.put("pid", Integer.toString(pid));
		map.put("tid", Integer.toString(tid));
		
		map.put("userSid", userSid);
		map.put("userName", userName);
		map.put("programName", programName);
		map.put("windowTitle", windowTitle);
		map.put("localTime", Utils.getTimeS(localTime));
		map.put("serverTime", Utils.getTimeS(serverTime));
		
		return map;
	}

	@Override
	public boolean parseMap(Map<String, String> map) {
		// TODO Auto-generated method stub
		type = Integer.parseInt(map.get("type"));
		status = Integer.parseInt(map.get("status"));
		
		clientId = map.get("clientId");		
		authId = map.get("authId");
		hostId = map.get("hostId");
		
		String sessionIdS = map.get("sessionId");
		if (sessionIdS != null)
			sessionId = Integer.parseInt(sessionIdS);
		
		String pidS = map.get("pid");
		if (pidS != null)
			pid = Integer.parseInt(pidS);
		
		String tidS = map.get("tid");
		if (tidS != null)
			tid = Integer.parseInt(tidS);
		
		userSid = map.get("userSid");
		userName = map.get("userName");
		programName = map.get("programName");
		windowTitle = map.get("windowTitle");
		localTime = Utils.parseTimeMillis(map.get("localTime"));
		serverTime = Utils.currentTime();
		
		String encodedData = map.get("data");
		if (encodedData != null) {
			try {
				data = Base64.decode(encodedData);
			} catch (Base64DecoderException e) {
				// TODO Auto-generated catch block
				SLog.exception(TAG, e);
			}
		} else {
			data = null;
		}
		
		return true;
	}

	public static ClientRequest clone(ClientRequest request)
	{
		ClientRequest clone = new ClientRequest();
		clone.parseMap(request.toMap());
		
		return clone;
	}
}
