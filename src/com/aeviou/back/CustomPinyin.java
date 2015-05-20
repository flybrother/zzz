package com.aeviou.back;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

// assume sd exists
public class CustomPinyin {

	private static CustomPinyin instance;

	private String dir = Environment.getExternalStorageDirectory().getPath()
			+ File.separator;
	private String file = "aeviou" + File.separator + "customPinyin.dat";

	private String fileContent;
	private JSONObject json;
	private JSONArray array;

	private HashMap<String, HashMap<String, CustomEntry>> pinyinHash;
	
	public static CustomPinyin getInstance() {
		if (instance == null){
            instance = new CustomPinyin();
        }
        return instance;
	}

	private CustomPinyin() {
		initFile();

		try {
			this.json = new JSONObject(fileContent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		this.pinyinHash = new HashMap<String, HashMap<String, CustomEntry>>();
		initPinyinMap();
	}

	private void initFile() {
		File CustomPinyinFile = new File(this.dir + this.file);

		if (!CustomPinyinFile.exists()) {
			this.writeFileSdcardFile(this.dir + this.file,
					"{\"customPinyin\":[[gaoji,搞基,100]]}");
		}
		this.fileContent = readFileSdcardFile(dir + file);

	}

	private void initPinyinMap() {
		try {
			this.array = this.json.getJSONArray("customPinyin");
			int length = this.array.length();
			// insert json to the hashmap
			for (int i = 0; i < length; i++) {
				JSONArray jsonEntry = this.array.getJSONArray(i);
				String pinyin = jsonEntry.getString(0);
				String hanzi = jsonEntry.getString(1);
				int frequency = jsonEntry.getInt(2);

				CustomEntry customEntry = new CustomEntry(pinyin, hanzi,
						frequency);

				HashMap<String, CustomEntry> miniMap = this.pinyinHash
						.get(pinyin);
				if (miniMap == null) {
					miniMap = new HashMap<String, CustomEntry>();
					this.pinyinHash.put(pinyin, miniMap);
					miniMap.put(hanzi, customEntry);
				} else {
					CustomEntry oldCustomEntry = miniMap.get(hanzi);
					if (oldCustomEntry == null) {
						miniMap.put(hanzi, customEntry);
					} else {
						Log.d("yzy", "same pinyin & hanzi have already existed!");
						System.exit(1);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// after find sd card
//	private void updatePinyinMap() {
//		initFile();
//		try {
//			this.array = this.json.getJSONArray("customPinyin");
//			int length = this.array.length();
//			// insert json to the hashmap
//			for (int i = 0; i < length; i++) {
//
//				JSONArray entry = this.array.getJSONArray(i);
//
//				String pinyin = entry.getString(0);
//				String hanzi = entry.getString(1);
//				int frequency = entry.getInt(2);
//
//				CustomEntry pinyinObject = new CustomEntry(pinyin, hanzi,
//						frequency);
//
//				HashMap<String, CustomEntry> miniMap = this.pinyinHash
//						.get(pinyin);
//				if (miniMap == null) {
//					miniMap = new HashMap<String, CustomEntry>();
//					this.pinyinHash.put(pinyin, miniMap);
//					miniMap.put(hanzi, pinyinObject);
//				} else {
//					CustomEntry oldObj = miniMap.get(hanzi);
//					if (oldObj == null) {
//						miniMap.put(hanzi, pinyinObject);
//					} else {
//						if (oldObj.frequency < pinyinObject.frequency) {
//							miniMap.put(hanzi, pinyinObject);
//						}
//					}
//				}
//			}
//			JSONArray organizedArray = new JSONArray();
//
//			for (String pinyin : this.pinyinHash.keySet()) {
//				HashMap<String, CustomEntry> miniMap = this.pinyinHash
//						.get(pinyin);
//				for (String hanzi : miniMap.keySet()) {
//					CustomEntry pinyinObject = miniMap.get(hanzi);
//					JSONArray entry = new JSONArray();
//					entry.put(pinyin);
//					entry.put(hanzi);
//					entry.put(pinyinObject.frequency);
//					organizedArray.put(entry);
//				}
//			}
//
//			this.array = organizedArray;
//			this.json.put("customPinyin", this.array);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public void writeFileSdcardFile(String fileName, String write_str) {

		try {
			File file = new File(fileName);
			File dir = file.getParentFile();

			if (!dir.exists()) {
				dir.mkdir();
			}

			FileOutputStream fout = new FileOutputStream(fileName);
			byte[] bytes = write_str.getBytes();

			fout.write(bytes);
			fout.close();
		}

		catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String readFileSdcardFile(String fileName) {
		String res = null;
		try {
			FileInputStream fin = new FileInputStream(fileName);

			int length = fin.available();

			byte[] buffer = new byte[length];
			fin.read(buffer);

			res = EncodingUtils.getString(buffer, "UTF-8");

			buffer = null;
			fin.close();
		}

		catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public boolean havePinyin(String pinyinList) {
		for (String key : this.pinyinHash.keySet())
			if (key.startsWith(pinyinList))
				return true;
		return false;
	}

	// assume pinyinList have corresponding entry already
	public void getAll(ArrayList<Character> pinyinList, ArrayList<CandidateType> candidates) {
		String pinyinStr = "";
		for (Character j : pinyinList)
			pinyinStr += PinyinTree.getInstance().getPinyinString(j);
		HashMap<String, CustomEntry> miniMap = this.pinyinHash.get(pinyinStr);
		if (miniMap == null)
			return;
		Collection<CustomEntry> entries = miniMap.values();
		for (CustomEntry entry : entries)
			entry.pinyinList = pinyinList;
		candidates.addAll(entries);
	}

	public void addCustom(String pinyinList, String hanzi, int frequency) {
		if (this.pinyinHash.containsKey(pinyinList) == false) {
			this.pinyinHash.put(pinyinList, new HashMap<String, CustomEntry>());
		}
		HashMap<String, CustomEntry> miniMap = this.pinyinHash.get(pinyinList);
		
		if (miniMap.containsKey(hanzi) == true) {
			addFrequency(pinyinList, hanzi, frequency);
		} else {
			CustomEntry entry = new CustomEntry(pinyinList, hanzi, frequency);
			miniMap.put(hanzi, entry);
		}
	}

	// assume entry exists
	public void addFrequency(String pinyinList, String hanzi, int frequency) {
		CustomEntry entry = this.pinyinHash.get(pinyinList).get(hanzi);
		entry.frequency++;
	}

//	public Collection<CustomEntry> getPinyin() {
//		HashMap<String, CustomEntry> miniMap = this.pinyinHash.get(this.pinyin
//				.toString());
//		if (miniMap == null) {
//			return null;
//		} else {
//			return miniMap.values();
//		}
//	}
//
//
//	public void debug() {
//		try {
//			ALog.v(this.json.getJSONArray("customPinyin"));
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		for (String pinyin : this.pinyinHash.keySet()) {
//			ALog.v(pinyin + this.pinyinHash.get(pinyin));
//		}
//		ALog.v("this.pinyin:" + this.pinyin.toString());
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.aeviou.pinyin.CustomPinyin#testCustomExist(java.lang.String)
//	 */
//	@Override
//	public boolean testCustomExist(String word) {
//		HashMap<String, CustomEntry> wordSet = this.pinyinHash.get(this.pinyin
//				.toString());
//		if (wordSet == null) {
//			return false;
//		} else {
//			return wordSet.containsKey(word);
//		}
//	}
//
//	@Override
//	public void recordPinyin(String word) {
//		// TODO Auto-generated method stub
//		HashMap<String, CustomEntry> miniMap = this.pinyinHash.get(this.pinyin
//				.toString());
//		if (miniMap == null) {
//			miniMap = new HashMap<String, CustomEntry>();
//			this.pinyinHash.put(this.pinyin.toString(), miniMap);
//		}
//
//		CustomEntry po = miniMap.get(word);
//		boolean isNew = false;
//		if (po == null) {
//			isNew = true;
//			po = new CustomEntry(pinyin.toString(), word,
//					CustomPinyin.DEFAULT_FREQUENCY);
//		}
//
//		po.frequency++;
//		if (po.frequency < 0) {
//			po.frequency = Integer.MAX_VALUE / 2;
//		}
//
//		miniMap.put(word, po);
//
//		if (isNew) {
//			JSONArray newEntry = new JSONArray();
//			ALog.v(po);
//			newEntry.put(this.pinyin.toString());
//			newEntry.put(word);
//			newEntry.put(po.frequency);
//			this.array.put(newEntry);
//		}
//		if (CustomPinyin.recordCounter++ % CustomPinyin.recordInterval == 0) {
//			record2SD();
//		}
//	}
//
	public void storeToSD() {
		// update this.json
		JSONArray organizedArray = new JSONArray();

		for (String pinyin : this.pinyinHash.keySet()) {
			HashMap<String, CustomEntry> miniMap = this.pinyinHash
					.get(pinyin);
			for (String hanzi : miniMap.keySet()) {
				CustomEntry pinyinObject = miniMap.get(hanzi);
				JSONArray entry = new JSONArray();
				entry.put(pinyin);
				entry.put(hanzi);
				entry.put(pinyinObject.frequency);
				organizedArray.put(entry);
			}
		}

		this.array = organizedArray;
		try {
			this.json.put("customPinyin", this.array);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeFileSdcardFile(this.dir + this.file, this.json.toString());
	}
}

class CustomEntry extends CandidateType {
	public String pinyin;

	public CustomEntry(String pinyin, String word, int frequency) {
		this.pinyin = pinyin;
		this.word = word;
		this.frequency = frequency;
		this.length = word.length() + 10086;
	}

	public String toString() {
		return pinyin + word + frequency;
	}

	public int hashCode() {
		return this.pinyin.hashCode() ^ this.word.hashCode();
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof CustomEntry))
			return false;
		CustomEntry po = (CustomEntry) o;
		return po.pinyin.equals(this.pinyin) && po.word.equals(this.word);
	}
}
