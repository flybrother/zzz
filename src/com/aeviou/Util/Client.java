package com.aeviou.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

import android.util.Log;


public class Client {
	private static Client instance;
	
	public static Client getInstance() {
        return instance = (instance == null ? new Client() : instance);
	}
	
	public void addUserlog(final String deviceId, final String model) {
		Thread mThread = new Thread(new Runnable() {
	        public void run() {
	        	try {
	    			URL url = new URL(Constants.addUserUrl+"?id="+deviceId+"&model="+model);
	    			HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
	    			urlConn.connect();
	    			
	    			InputStreamReader in = new InputStreamReader(urlConn.getInputStream());    
	                BufferedReader buffer = new BufferedReader(in);  
	                String inputLine = null;  
	                while (((inputLine = buffer.readLine()) != null))  
	                {
	                }
	                in.close();  
	    			
	    			urlConn.disconnect();
	    		} catch (Exception e) {
	    		}
	        }
	    });
		mThread.start();
/*	    try {
			mThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
	}
	
	public void updateSpeed(final String deviceId, final int speed, final String contact) {
		Thread mThread = new Thread(new Runnable() {
	        public void run() {
	        	try {
	    			URL url = new URL(Constants.updateSpeedUrl+"?id=deviceId+"+deviceId+"&speed="+speed+"&contact="+contact);
	    			HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
	    			urlConn.connect();
	    			
	    			InputStreamReader in = new InputStreamReader(urlConn.getInputStream());    
	                BufferedReader buffer = new BufferedReader(in);  
	                String inputLine = null;  
	                while (((inputLine = buffer.readLine()) != null))  
	                {
	                }
	                in.close();  
	    			
	    			urlConn.disconnect();
	    		} catch (Exception e) {
	    			
	    		}
	        }
	    });
		mThread.start();
/*	    try {
			mThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		
	}
	
	// ref: http://stackoverflow.com/questions/5133452/what-if-i-need-to-return-an-object-from-threads-runnable-object
	public JSONObject getTopThree() {
		final JSONObject[] jsons = new JSONObject[1];
		Thread mThread = new Thread(new Runnable() {
	        public void run() {
	        	try {
	    			URL url = new URL(Constants.getTopThreeUrl);
	    			HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
	    			urlConn.connect();
	    			
	    			String response = "", inputLine = null;
	    			InputStreamReader in = new InputStreamReader(urlConn.getInputStream());    
	                BufferedReader buffer = new BufferedReader(in);    
	                while (((inputLine = buffer.readLine()) != null))  
	                {
	                	response = response.concat(inputLine);
	                }
	                in.close();  
	    			urlConn.disconnect();
	    			jsons[0] = new JSONObject(response);
	    		} catch (Exception e) {
	    			
	    		}
	        }
	    });
		mThread.start();
	    try {
			mThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return jsons[0];
	}
	
	public String getRank(final String deviceId) {
		final String[] rank = new String[1];
		Thread mThread = new Thread(new Runnable() {
	        public void run() {
	        	try {
	    			URL url = new URL(Constants.getRankUrl+"?id=deviceId+"+deviceId);
	    			HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
	    			urlConn.connect();
	    			
	    			String response = "", inputLine = null;
	    			InputStreamReader in = new InputStreamReader(urlConn.getInputStream());    
	                BufferedReader buffer = new BufferedReader(in);    
	                while (((inputLine = buffer.readLine()) != null))  
	                {
	                	response = response.concat(inputLine);
	                }
	                in.close();  
	    			urlConn.disconnect();
	    			
	    			rank[0] = response;
	    		} catch (Exception e) {
	    		}
	        }
	    });
		mThread.start();
	    try {
			mThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return rank[0];
	}
}
