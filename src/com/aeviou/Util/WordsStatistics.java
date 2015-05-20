package com.aeviou.Util;

import java.util.Vector;

import android.util.Log;

public class WordsStatistics {
	public class StatisticsItem {
		public String word;
		public long time;
		
		public StatisticsItem() {
			this.word = "";
			this.time = System.currentTimeMillis();
		}
		
		public StatisticsItem(String w) {
			this.word = w;
			this.time = System.currentTimeMillis();
		}
	}
	
	private static WordsStatistics instance = null;
	private static int timespan = 10;
	private static Vector<StatisticsItem> inputRecords = new Vector<StatisticsItem>();
	private static Vector<StatisticsItem> validRecords = new Vector<StatisticsItem>();
	private static double speed = 0;
	private static double maxSpeed = 0;
	private static double upperLimit = 100;
	
	private WordsStatistics() {}
	
	public static WordsStatistics getInstance () {
		return instance = (instance == null ? new WordsStatistics() : instance);
	}
	
	public static void addInputWords (String w) {
		inputRecords.add(instance.new StatisticsItem(w));
	}
	
	public static void addValidWords (String w) {
		validRecords.add(instance.new StatisticsItem(w));
		Log.d("WordsStatistics","valid size: " + validRecords.size());
	}
	
	public static void cleanWordsBefore (long time) {
		//todo
		inputRecords.clear();
		validRecords.clear();
	}
	
	public static int getTotalNumberofWords () {
		return validRecords.size();
	}
	
	public static double getCurrentSpeed () {
		int length = inputRecords.size();
		int count = 0;
		long curTime = System.currentTimeMillis();
		long lastTime = curTime;
		
		for (int i = length-1; i >= 0; i--) {
			if (lastTime > inputRecords.get(i).time + timespan * 1000) {
				break;
			} else {
				count++;
				lastTime = inputRecords.get(i).time;
			}
		}
		
		if (curTime == lastTime) {
			speed = 0;
		} else { 
			speed = (count * 1000) / ((double)(curTime - lastTime)) * 60;
		}
		
		if (speed > maxSpeed)
			maxSpeed = speed;
		
		return speed;
	}

	public static double getMaxSpeed() {
		int length = validRecords.size();
		int count = 0;
		long beginTime = 0, endTime = 0;
		double curSpeed = -1;
		
		maxSpeed = -1;
		
		for (int i = 0; i < length; i++) {
			Log.d("WordsStatistics", "record "+validRecords.get(i).word);
			if (endTime + timespan * 3000 < validRecords.get(i).time) {
				long t = endTime - beginTime;
				if (t > timespan * 1000)
					curSpeed = count * 1000 / (double)t * 60;
				if (curSpeed > maxSpeed)
					maxSpeed = curSpeed;
				
				count = 1;
				beginTime = validRecords.get(i).time;
				endTime = beginTime;
			} else {
				count++;
				endTime = validRecords.get(i).time;
			}
		}
		
		long t = endTime - beginTime;
		if (t > 60000) // time threshold
			curSpeed = count * 1000 / (double)t * 60;
		if (curSpeed > maxSpeed)
			maxSpeed = curSpeed;
		
		return maxSpeed;
	}
	
	public static double getUpperLimit() {
		return upperLimit;
	}

	public static void setUpperLimit(double upperLimit) {
		WordsStatistics.upperLimit = upperLimit;
	}
}
