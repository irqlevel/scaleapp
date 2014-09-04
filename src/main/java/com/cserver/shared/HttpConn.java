package com.cserver.shared;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;


public class HttpConn {
	private static final String TAG = "HttpConn";
	
	public static StringBuffer readInputToString(InputStream in) {
		StringBuffer output = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while((line = reader.readLine())!=null){
				output.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
		} finally {
		} 
		
		return output;
	}

	public static String get(String protocol, String host, int port, String uri) throws Exception {
		String output = null;
		HttpURLConnection urlConnection = null;	
		try {
			URL url = new URL(protocol, host, port, uri);
			urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.setConnectTimeout(5000);
			urlConnection.setReadTimeout(5000);
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();
			if (urlConnection.getResponseCode() != 200)
				throw new Exception("GET " + uri + " code=" + urlConnection.getResponseCode());
			
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			output = readInputToString(in).toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
			throw e;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		} 

		return output;
	}
	
	public static void delete(String protocol, String host, int port, String uri) throws Exception {
		HttpURLConnection urlConnection = null;	
		try {
			URL url = new URL(protocol, host, port, uri);
			urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.setConnectTimeout(5000);
			urlConnection.setReadTimeout(5000);
			urlConnection.setRequestMethod("DELETE");
			urlConnection.connect();
			if (urlConnection.getResponseCode() != 200)
				throw new Exception("DELETE " + uri + " code=" + urlConnection.getResponseCode());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
			throw e;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
	}
	
	public static void put(String protocol, String host, int port, String uri, String data) throws Exception {
		HttpURLConnection urlConnection = null;	
		try {
			URL url = new URL(protocol, host, port, uri);
			urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.setConnectTimeout(5000);
			urlConnection.setReadTimeout(5000);
			urlConnection.setRequestMethod("PUT");
			
			urlConnection.setDoOutput(true);
			urlConnection.connect();
			
			byte[] rawData = data.getBytes(Charset.forName("UTF-8"));
			//urlConnection.setChunkedStreamingMode(rawData.length);

			OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
			out.write(rawData);
			out.flush();
			out.close();

			if (urlConnection.getResponseCode() != 200)
				throw new Exception("PUT " + uri + " code=" + urlConnection.getResponseCode());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
			throw e;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
	}
	
	
	public static String post(String protocol, String host, int port, String uri, String data) throws Exception {
		HttpURLConnection urlConnection = null;
		String output = null;
		try {
			URL url = new URL(protocol, host, port, uri);
			urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.setConnectTimeout(5000);
			urlConnection.setReadTimeout(5000);
			urlConnection.setRequestMethod("PUT");
			urlConnection.setDoOutput(true);
			urlConnection.connect();
			
			byte[] rawData = data.getBytes(Charset.forName("UTF-8"));
			//urlConnection.setChunkedStreamingMode(rawData.length);

			OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
			out.write(rawData);
			out.flush();
			out.close();
			if (urlConnection.getResponseCode() != 200)
				throw new Exception("POST " + uri + " code=" + urlConnection.getResponseCode());
			
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			output = readInputToString(in).toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			SLog.exception(TAG, e);
			throw e;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
		return output;
	}	
}
