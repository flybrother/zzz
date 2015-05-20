package com.aeviou.front.keyboardview.hexkeyboard;

import java.util.ArrayList;
import java.util.List;

public class HexKeySlidePath {
	private List<HexKey> list;
	private List<String> pinyin;
	
	HexKeySlidePath(){
		list = new ArrayList<HexKey>();
		pinyin = new ArrayList<String>();
	}
	
	public void addToPath(HexKey hexkey, boolean isFirst){
		if(list == null){
			list = new ArrayList<HexKey>();
		}
		if(isFirst){
			pinyin.add(hexkey.getName());
		}else{
			pinyin.add(hexkey.getDynamicName());
		}
		list.add(hexkey);
	}
	
	/**
	 * @param keyid
	 * @return return the index of this key in the slide path list, if there is no such key returns -1
	 */
	public int findKeyByKeyId(int keyid){
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getId() == keyid){
				return i;
			}
		}
		return -1;
	}
	
	
	/**
	 * this will remove all the hex key elements who's index is bigger than the param idx
	 * 
	 * @param idx
	 */
	public void cutSlidePath(int idx){
		for(int i=list.size()-1; i>idx; i--){
			list.remove(i);
			pinyin.remove(i);
		}
	}
	
	public String getPinyin(){
		String result = "";
		for(int i=0; i<pinyin.size(); i++){
			result = result + pinyin.get(i);
		}
		return result;
	}
	
	public void clear(){
		list.clear();
		pinyin.clear();
	}
	
	public int getSize(){
		if(list == null){
			return 0;
		}else{
			return list.size();
		}
	}
	
	public boolean isEmpty(){
		return list.isEmpty();
	}
	
	public HexKey getFirstHexKey(){
		return list.get(0);
	}
	
	public String getFirstHexKeyName(){
		return pinyin.get(0);
	}
	
	public HexKey getLastHexKey(){
		return list.get(list.size()-1);
	}
	
	public String getLastHexKeyName(){
		return pinyin.get(pinyin.size()-1);
	}
}
