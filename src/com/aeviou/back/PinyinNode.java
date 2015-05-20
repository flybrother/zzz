package com.aeviou.back;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import com.aeviou.Util.ByteArrayCastor;

/**
 * File format: ================repeat===============
 * 
 * char pinyin 2 int wordAddress 4 int maxFreqency 4 char childrenSize 2 int
 * childrenAddress 4
 * 
 * ==============end repeat==============
 * 
 */
public class PinyinNode {
	public static final int NODE_SIZE = 16;

	String filename;
	RandomAccessFile file = null;
	byte[] readBuffer = new byte[NODE_SIZE];
	
	int lastGetChildCurrent;
	byte[] getChildBuffer = new byte[NODE_SIZE];
	
//	MappedByteBuffer out;
	int lastCurrent;
	int lastAddress;
	int lastFreqency;

	public void fileOpen() {
		
	}

	public void fileClose() {
		if (file == null)
			return;
		try {
			file.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		file = null;
	}

	PinyinNode(String filename) {
		this.filename = filename;
		lastCurrent = -1;
		lastAddress = -1;
		this.lastAddress = -1;
		try {
			file = new RandomAccessFile(new File(filename), "r");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
//		try {
//			FileChannel channel = this.file.getChannel();
//			this.out = channel.map(FileChannel.MapMode.READ_ONLY, 0, (int)channel.size());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	Map<Character, Integer> getChildrens(int current) {
		byte[] nodeBuf = new byte[NODE_SIZE];
		try {
			file.seek(current);
			file.read(nodeBuf);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		char childrenSize = ByteArrayCastor.getChar(nodeBuf, 10);
		int childrenAddress = ByteArrayCastor.getInteger(nodeBuf, 12);
		
		// not succeed
		if (childrenAddress == -1)
			return null;
		// else
		int address;
		char value;
		byte[] childrenBuffer = new byte[NODE_SIZE * childrenSize];
		Map<Character, Integer> result = new HashMap<Character, Integer>();

		try {
			file.seek(childrenAddress);
			file.read(childrenBuffer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		for (int i = 0; i < childrenSize; i++) {
			address = i * NODE_SIZE;
			value = ByteArrayCastor.getChar(childrenBuffer, address);
			result.put(value, childrenAddress+address);
		}
		
		return result;
	}

	int getChild(int current, char pinyin) {
		if (current != lastGetChildCurrent) {
			try {
//				out.position(current);
//				out.get(this.getChildBuffer, 0, this.getChildBuffer.length);
				file.seek(current);
				file.read(getChildBuffer);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			lastGetChildCurrent = current;
		}

		char childrenSize = ByteArrayCastor.getChar(getChildBuffer, 10);
		int childrenAddress = ByteArrayCastor.getInteger(getChildBuffer, 12);

		if (childrenAddress == -1)
			return -1;

		int i = 0;
		int j = childrenSize - 1;
		int m = 0;
		int address;
		char value;

		byte[] childrenBuffer = new byte[NODE_SIZE * childrenSize];
		try {
			file.seek(childrenAddress);
			file.read(childrenBuffer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		while (i <= j) {
			m = (i + j) / 2;
			address = m * NODE_SIZE;
			value = ByteArrayCastor.getChar(childrenBuffer, address);
			if (pinyin == value) {
				lastCurrent = childrenAddress + address;
				lastAddress = ByteArrayCastor.getInteger(childrenBuffer,
						address + 2);
				lastFreqency = ByteArrayCastor.getInteger(childrenBuffer,
						address + 6);
				return childrenAddress + address;
			} else if (pinyin > value) {
				i = m + 1;
			} else {
				j = m - 1;
			}
		}

		return -1;
	}
	
//	int getChild(int current, char pinyin) {
//		this.out.position(0);
//		int a = this.out.limit();
//		int b = a+1;
//		this.out.get(readBuffer, current, NODE_SIZE);
//		try {
//			file.seek(current);
//			file.read(readBuffer);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//		char childrenSize = PinyinUtils.getChar(readBuffer, 10);
//		int childrenAddress = PinyinUtils.getInteger(readBuffer, 12);
//
//		if (childrenAddress == -1)
//			return -1;
//
//		int i = 0;
//		int j = childrenSize - 1;
//		int m = 0;
//		int address;
//		char value;
//
//		byte[] childrenBuffer = new byte[NODE_SIZE * childrenSize];
//		try {
//			file.seek(childrenAddress);
//			file.read(childrenBuffer);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//		while (i <= j) {
//			m = (i + j) / 2;
//			address = m * NODE_SIZE;
//			value = PinyinUtils.getChar(childrenBuffer, address);
//			if (pinyin == value) {
//				lastCurrent = childrenAddress + address;
//				lastAddress = PinyinUtils.getInteger(childrenBuffer,
//						address + 2);
//				lastFreqency = PinyinUtils.getInteger(childrenBuffer,
//						address + 6);
//				return childrenAddress + address;
//			} else if (pinyin > value) {
//				i = m + 1;
//			} else {
//				j = m - 1;
//			}
//		}
//
//		return -1;
//	}

	int getHanziAddress(int current) {
		if (lastCurrent == current) {
			return lastAddress;
		}
		try {
			file.seek(current);
			file.read(readBuffer);
			lastCurrent = current;
			lastAddress = ByteArrayCastor.getInteger(readBuffer, 2);
			lastFreqency = ByteArrayCastor.getInteger(readBuffer, 6);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lastAddress;
	}

	int getMaxFreqency(int current, char pinyinId) {
		if (lastCurrent == current) {
			return lastFreqency;
		}
		try {
			file.seek(current);
			file.read(readBuffer);
			lastCurrent = current;
			lastAddress = ByteArrayCastor.getInteger(readBuffer, 2);
			lastFreqency = ByteArrayCastor.getInteger(readBuffer, 6);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lastFreqency;
	}
}
