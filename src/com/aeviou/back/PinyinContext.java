package com.aeviou.back;

import java.util.ArrayList;
import java.util.Map;

import android.util.Log;

import com.aeviou.Util.Constants;
import com.aeviou.Util.Globals;
import com.aeviou.Util.Detector;

public class PinyinContext {
	private static PinyinContext instance;

	private ArrayList<ArrayList<Character>> jianpinList;
	private ArrayList<Integer> jianpinOffsetList;
	private ArrayList<Boolean> hasCustomList;
	private int jianpinListSize;
	
	private ArrayList<CandidateType> candidates;

	private PinyinTree pytree;
	private PinyinRoot root;
	private PinyinNode node;
	private PinyinHanzi hanzi;
	private CustomPinyin custompy;
	private FreqCalibrator calibrator;
	
	private PinyinContext() {
		jianpinList = new ArrayList<ArrayList<Character>>();
		jianpinOffsetList = new ArrayList<Integer>();
		hasCustomList = new ArrayList<Boolean>();
		jianpinListSize = 0;

		candidates = new ArrayList<CandidateType>();
		
		/** 
		 * initial PinyinTree and CustomPinyin in order to avoid high latency
		 * of first call to them
		 */
		pytree = PinyinTree.getInstance();
		
		custompy = Detector.hasSD() ? CustomPinyin.getInstance() : null;
		
		calibrator = FreqCalibrator.getInstance();
		
		root = new PinyinRoot(Globals.PINYIN_FILE_DIRECTORY
				+ Constants.PINYIN_ROOT_FILENAME);
		node = new PinyinNode(Globals.PINYIN_FILE_DIRECTORY
				+ Constants.PINYIN_NODE_FILENAME);
		hanzi = new PinyinHanzi(Globals.PINYIN_FILE_DIRECTORY
				+ Constants.PINYIN_HANZI_FILENAME);

		this.clearContext();
		System.gc();
	}
	
	public static PinyinContext getInstance() {
        return instance = (instance == null ? new PinyinContext() : instance);
	}

	/**
	 * call this before each session of input
	 * it will start new session of input
	 */
	public void clearContext() {
		candidates.clear();
		this.jianpinList.clear();
		this.jianpinListSize = 0;
		this.jianpinOffsetList.clear();
		this.hasCustomList.clear();
		
		hanzi.clearCache();
		
		System.gc();
	}
	
	/**
	 *  call this to add input 'jianpin' in this session 
	 */
	public void addJianpin(String jianpin) {
		Log.d("yzy", "add "+jianpin);
		
		// get all possible syllable begining with 'sheng'
		ArrayList<Character> possibleSyllableIds = new ArrayList<Character>();
		switch (jianpin.charAt(0)) {
		case 'a':
		case 'e':
		case 'i':
		case 'o':
		case 'u':
			jianpin = 'v' + jianpin;
			break;
		}
		char exactId = pytree.getPinyinId(jianpin); 
		if (exactId == (char)-1)
			possibleSyllableIds = pytree.getSyllableIdsStartWith(jianpin);
		else
			possibleSyllableIds.add(exactId);

		// join new syllable with syllables before, and update jian'pin offset list
		if (jianpinList.size() == 0 && this.jianpinListSize == 0) {
			for (int j = 0; j < possibleSyllableIds.size(); j++) {
				char newSyllable = possibleSyllableIds.get(j);
				ArrayList<Character> word = new ArrayList<Character>();
				word.add(newSyllable);
				this.jianpinList.add(word);
				this.jianpinOffsetList.add(root.getIndex(newSyllable));
				
				if (this.custompy != null && this.custompy.havePinyin(pytree.getPinyinString(newSyllable)))
					this.hasCustomList.add(true);
				else
					this.hasCustomList.add(false);
			}
		} else {
			int size = this.jianpinList.size();
			for (int i = 0; i < size; i++) {
				ArrayList<Character> oldJianpinIdList = jianpinList.remove(0);
				int offset = jianpinOffsetList.remove(0);
				boolean hasCustom = hasCustomList.remove(0);
				
				Map<Character, Integer> children = (offset == -1 ? null : this.node.getChildrens(offset));
				
				String pinyinList = "";
				for (Character j: oldJianpinIdList)
					pinyinList += pytree.getPinyinString(j);
				
				ArrayList<Character> word;
				for (Character j : possibleSyllableIds) {
					word = null;
					
					boolean custom = false;
					boolean origin = false;
					if (hasCustom && this.custompy != null
							&& this.custompy.havePinyin(pinyinList + pytree.getPinyinString(j)))
						custom = true;
					if (children != null && children.get(j) != null)
						origin = true;
						
					if (custom || origin) {
						word = new ArrayList<Character>();
						for (Character c : oldJianpinIdList)
							word.add(c);
						word.add(j);
						this.jianpinList.add(word);
						this.jianpinOffsetList.add(origin ? children.get(j) : -1);
						this.hasCustomList.add(custom ? true : false);
					}
				}
				
			}
		}
		
		this.jianpinListSize++;
		
		// get all words
		ArrayList<CandidateType> tmp = new ArrayList<CandidateType>();
		for (int i = 0; i < this.jianpinOffsetList.size(); i++) {
			// get custom
			boolean hasCustom = this.hasCustomList.get(i);
			if (hasCustom)
				this.custompy.getAll(this.jianpinList.get(i), tmp);
			// get origin
			if (jianpinOffsetList.get(i) > 0) {
				int hanziAddr = node.getHanziAddress(jianpinOffsetList.get(i));
				if (hanziAddr != -1)
					this.hanzi.getAll(hanziAddr, tmp, this.jianpinList.get(i));
			}
			
		}
		
		java.util.Collections.sort(tmp);
		this.candidates.clear();
		String str = "";
		for (CandidateType c : tmp) {
			if (str.equals(c.word) == false) {
				str = c.word;
				this.candidates.add(c);
			}
		}
		
	}
	
	/**
	 * call this to get the candidate i
	 *  if null is returned, there's no more candidate
	 */
	public String getCandidate(int index) {
		if (index < 0 || index >= candidates.size())
			return null;
		return candidates.get(index).word;
	}
	
	/**
	 * call this to get the pinyinList of candidate i
	 */
	public ArrayList<Character> getPinyinListOfCandidate(int index) {
		if (index < 0 || index >= candidates.size())
			return null;
		return candidates.get(index).pinyinList;
	}
	
	/**
	 *  call this after user create new word
	 *  it will record the new word
	 */
	public void recordCustom(ArrayList<Character> pinyinList, String hanzi) {
		String pinyinStr = "";
		for (Character j : pinyinList)
			pinyinStr += PinyinTree.getInstance().getPinyinString(j);
		
		if (this.custompy == null)
			return;
			
		for (CandidateType candidate : this.candidates)
			if (candidate.word.equals(hanzi))
				return;

		this.custompy.addCustom(pinyinStr, hanzi, 1);
	}
	
	/**
	 * call this after user choosing candidate i
	 * it will update the frequency 
	 */
	public void chooseCandidate(int index) {
		// check if this is custom
		if (this.candidates.get(index).getClass() != CandidateType.class) {
			CustomEntry entry = (CustomEntry) this.candidates.get(index);
			entry.frequency++;
			if (Detector.hasSD())
				this.custompy.storeToSD();
		} else {
			Log.d("yzy", "choose "+index+", "+this.candidates.get(index).word);
			this.calibrator.calibrate(this.candidates, index, this.hanzi);
		}
	}
}