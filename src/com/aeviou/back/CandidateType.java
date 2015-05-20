package com.aeviou.back;

import java.util.ArrayList;

import android.util.Log;

public class CandidateType implements Comparable<Object>{
	public ArrayList<Character> pinyinList;
    public String word;
    public int length;
    public int frequency;
    public int offset; // offset in hanzi
    
    public int getFreqOffset() {
    	Log.d("yzy", "word "+word+", offset"+offset+", length"+length);
    	return offset + length * 2;
    }

    public int compareTo(Object o) {
        CandidateType node1 = (CandidateType)this;
        CandidateType node2 = (CandidateType)o;

        if (node1.length < node2.length){
            return 1;
        }else if(node1.length > node2.length){
            return -1;
        }else{
            if (node1.frequency < node2.frequency){
                return 1;
            }else if(node1.frequency > node2.frequency){
                return -1;
            }else{
                return 0;
            }
        }
    }
}
