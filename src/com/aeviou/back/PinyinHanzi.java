package com.aeviou.back;

import android.annotation.SuppressLint;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

import com.aeviou.Util.ByteArrayCastor;

public class PinyinHanzi {
	public static final int MAX_CACHE_SIZE = 100;
	HashMap<Integer, String> firstCache;

	String filename;
	RandomAccessFile file = null;
	byte[] readBuffer = new byte[4];

	public void clearCache() {
		firstCache.clear();
	}

	@SuppressLint("UseSparseArrays")
	PinyinHanzi(String filename) {
		this.filename = filename;
		firstCache = new HashMap<Integer, String>();
		try {			
			file = new RandomAccessFile(new File(filename), "rw");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	String getFirst(int offset) {
		String value = firstCache.get(offset);
		if (value != null) {
			return value;
		}

		try {
			file.seek(offset);
			file.read(readBuffer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		int length = ByteArrayCastor.getChar(readBuffer, 0);

		byte[] listBuffer = new byte[length + length + 4];
		try {
			file.read(listBuffer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		StringBuffer sb = new StringBuffer();
		char c;
		for (int i = 0; i < length; i++) {
			c = ByteArrayCastor.getChar(listBuffer, i + i);
			sb.append(c);
		}

		String ret = sb.toString();
		if (firstCache.size() < MAX_CACHE_SIZE)
			firstCache.put(offset, ret);
		return ret;
	}

	void getAll(int offset, ArrayList<CandidateType> list, ArrayList<Character> pinyinList) {
		try {
			file.seek(offset);
			file.read(readBuffer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		int length = ByteArrayCastor.getChar(readBuffer, 0);
		int size = ByteArrayCastor.getChar(readBuffer, 2);

		byte[] listBuffer = new byte[size * (length*2 + 4)];
		try {
			file.read(listBuffer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		StringBuffer sb = new StringBuffer();
		char c;
		int address = 0;
		for (int k = 0; k < size; k++) {
			sb.delete(0, length);
			for (int i = 0; i < length; i++) {
				c = ByteArrayCastor.getChar(listBuffer, address + i*2);
				sb.append(c);
			}
			int frequency = ByteArrayCastor.getInteger(listBuffer, address + length
					+ length);
			CandidateType candidate = new CandidateType();
			candidate.pinyinList = pinyinList;
			candidate.word = sb.toString();
			candidate.frequency = frequency;
			candidate.length = length;
			candidate.offset = offset + 4 + address;
			list.add(candidate);
			address += length + length + 4;
		}
	}

	// assume offset valid
	public int getFrequency(int freqOffset) {
		byte[] buffer = new byte[4];
		try {
			file.seek(freqOffset);
			file.read(buffer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		int frequency = ByteArrayCastor.getInteger(buffer, 0);
		return frequency;
	}
	
	// assume offset valid
	public void setFrequency(int freqOffset, int frequency) {
		try {
			file.seek(freqOffset);
			file.writeInt(frequency);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
