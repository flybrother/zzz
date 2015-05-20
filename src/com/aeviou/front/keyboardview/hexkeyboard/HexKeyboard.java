package com.aeviou.front.keyboardview.hexkeyboard;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.aeviou.Util.Constants;
import com.aeviou.Util.Globals;
import com.aeviou.Util.WordsStatistics;
import com.aeviou.back.PinyinContext;
import com.aeviou.back.PinyinTree;
import com.aeviou.front.keyboardview.abstractkeyboard.AbstractKeyboard;

public class HexKeyboard extends AbstractKeyboard{
	
	
	private HexKey lastKey;
	private HexKey startupKey;
	private HexKeySlidePath slidePath;
	private PinyinTree pinyinTree;
	private HexKey backupKey;
	private CandidateBar candidateBar;
	private List<String> words;
	private boolean isOperatingCandidateBar;
	private int candidateTextSize = 0;
	private TipsView tipsView;
	private SpeedView speedView;
	private boolean ifContinue = true;
	protected static int LONG_PRESS_TIME = 100;
	protected final Handler _handler = new Handler();
	protected Runnable _longPressed = new Runnable() {
		public void run() {
			if (lastKey != null && lastKey.getType().equals("delete")) {
				deleteLastWord();
				_handler.postDelayed(this, LONG_PRESS_TIME);
			}
		}
	};
	protected final int UPDATE_SPEED = 1;
	protected final int HIDE_SPEED = 2;
	@SuppressLint("HandlerLeak")
	protected final Handler _speedHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_SPEED:
				speedView.update();
				break;
			case HIDE_SPEED:
				speedView.closeWindow();
				break;
			default:
				break;
			}
		}
	};
	protected Thread _showSpeedThread = new Thread() {
		public void run() {
			try {
				while (true) {
					synchronized (this) {
	                    while (!ifContinue || !Globals.isSpeedViewOn) {
	                    	_speedHandler.sendEmptyMessageDelayed(HIDE_SPEED, 0);
							wait();
	                    }
	                }
					Thread.sleep(100);
					_speedHandler.sendEmptyMessageDelayed(UPDATE_SPEED, 0);
					//speedView.update();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	public HexKeyboard(HexKey[] keys, CandidateBar candidatebar, int bigrow){
		this.keys = keys;
		this.candidateBar = candidatebar;
		this.slidePath = new HexKeySlidePath();
		this.pinyinTree = PinyinTree.getInstance();
		this.words = new ArrayList<String>();
		this.tipsView = new TipsView(Globals.view.getContext());
		this.speedView = new SpeedView(Globals.view.getContext());
		this.isOperatingCandidateBar = false;
		this.tipsView.setKeys(this.keys);
	}

	@Override
	public void draw(Canvas Cvs) {
		// TODO Auto-generated method stub
		//draw candidate keys
		candidateBar.draw(Cvs);
		//draw hex keys
		for(int i=0; i<keys.length-3; i++){
			keys[i].draw(Cvs);
		}
		//speedView.draw(Cvs);
	}

	@Override
	public boolean onTouch(float x, float y) {
		// TODO Auto-generated method stub
		int id = getHexKeyByPointOnTouch(x,y);
		_handler.postDelayed(_longPressed, LONG_PRESS_TIME * 3);
		//
		if(keys[id].getRow()==0 && candidateBar.getStatus() == Constants.KEY_STATUS_VISIBLE){
			isOperatingCandidateBar = true;
			candidateBar.onTouch(x, y);
			return true;
		}
		//
		keys[id].setStatus(Constants.KEY_STATUS_SELECTED);
		lastKey = (HexKey)keys[id];
		startupKey = lastKey;
		if(!startupKey.getType().equals("aoe")){
			slidePath.addToPath(startupKey, true);
		}
		if(keys[id].getType().equals("letter") || keys[id].getType().equals("aoe")){
			String nextLetters = String.valueOf(pinyinTree.getNextLetters(slidePath.getPinyin()));
			// show key[id]'s neighbours
			List <HexKeyNeighbour> neighbours = ((HexKey)keys[id]).getNeighbours();
			for(int i=0; i<neighbours.size(); i++){
				String neighbourName = neighbours.get(i).dynamicName;
				if(!neighbourName.equals("")&&neighbourName!=null){
					int neighbourID = neighbours.get(i).hexKeyId;
					((HexKey)keys[neighbourID]).setDynamicName(neighbourName);
					keys[neighbourID].setStatus(Constants.KEY_STATUS_CHANGED);
					for(int j=0; j<nextLetters.length(); j++){
						if(String.valueOf(nextLetters.charAt(j)).equals(neighbourName)){
							keys[neighbourID].setStatus(Constants.KEY_STATUS_NEXT);
						}else if(startupKey.getType().equals("aoe")){
							keys[neighbourID].setStatus(Constants.KEY_STATUS_NEXT);
						}
					}
				}
			}
			// show key[id]'s dynamic key
			List <HexKeyDynamic> dynamicHexKeys = ((HexKey)keys[id]).getDynamicHexKeys();
			for(int i=0; i<dynamicHexKeys.size(); i++){
				int dynamicID = dynamicHexKeys.get(i).hexKeyId;
				String dynamicName = dynamicHexKeys.get(i).hexKeyName;
				((HexKey)keys[dynamicID]).setDynamicName(dynamicName);
				keys[dynamicID].setStatus(Constants.KEY_STATUS_CHANGED);
			}
			// update tips view window
			tipsView.setVisibility(View.VISIBLE);
			tipsView.update();
		}
		
		return true;
	}

	@Override
	public boolean onMove(float x, float y) {
		if(startupKey != null){
			int id = getHexKeyByPointOnMove(x, y);
			if(lastKey.getId() != id){
				_handler.removeCallbacks(_longPressed);
				if(keys[id].getStatus() == Constants.KEY_STATUS_NEXT){
					//if user slide into "h"
					if(((HexKey)keys[id]).getDynamicName().equals("h") && backupKey == null){
						backupKey = new HexKey();
						String keyname = startupKey.getName() + "h";
						copyInformation(backupKey, (HexKey)keys[id]);
						copyInformation((HexKey)keys[id],(HexKey)keys[findKeyIdByName(keyname)]);
						clearKeyboard();
						onTouch(x,y);
						return true;
					}
					//erase old "next" keys
					for(int i=0; i<lastKey.getNeighbours().size(); i++){
						int neighbourId = lastKey.getNeighbours().get(i).hexKeyId;
						if(keys[neighbourId].getStatus() == Constants.KEY_STATUS_NEXT){
							keys[neighbourId].setStatus(Constants.KEY_STATUS_CHANGED);
						}
					}
					//set variables and update slidePath
					keys[id].setStatus(Constants.KEY_STATUS_SELECTED);
					lastKey = (HexKey)keys[id];
					slidePath.addToPath(lastKey, false);
					//draw ng keys
					if(slidePath.getSize() == 2){
						for(int i=0; i<startupKey.getNeighbours().size(); i++){
							if(startupKey.getNeighbours().get(i).hexKeyId == id){
								List<HexKeyDynamic> ngs = startupKey.getNeighbours().get(i).NGs;
								for(int j=0; j<ngs.size(); j++){
									((HexKey)keys[ngs.get(j).hexKeyId]).setDynamicName(ngs.get(j).hexKeyName);
									if(keys[ngs.get(j).hexKeyId].getStatus() == Constants.KEY_STATUS_NORMAL){
										keys[ngs.get(j).hexKeyId].setStatus(Constants.KEY_STATUS_CHANGED);
									}
								}
								break;
							}
						}
					}
					//draw new "next" keys
					String nextLetters = null;
					if(startupKey.getType().equals("aoe")){
						nextLetters = String.valueOf(pinyinTree.getNextLetters("v" + slidePath.getPinyin()));
					}else{
						nextLetters = String.valueOf(pinyinTree.getNextLetters(slidePath.getPinyin()));
					}
					for(HexKeyNeighbour neighbour: lastKey.getNeighbours()){
						if(!((HexKey)keys[neighbour.hexKeyId]).getDynamicName().equals("")){
							char neighbourName = ((HexKey)keys[neighbour.hexKeyId]).getDynamicName().charAt(0);
							for(int j=0; j<nextLetters.length(); j++){
								if(neighbourName == nextLetters.charAt(j)){
									keys[neighbour.hexKeyId].setStatus(Constants.KEY_STATUS_NEXT);
								}
							}
						}
					}
					tipsView.update();
				}
				else if(keys[id].getStatus() == Constants.KEY_STATUS_SELECTED) {//user has slide back
					//if user returned to the startupKey
					if(startupKey == (HexKey)keys[id]){
						clearKeyboard();
						return onTouch(x,y);
					}
					//erase all the "next" and unnecessary "selected"
					slidePath.cutSlidePath(slidePath.findKeyByKeyId(id));
					for(int i=0; i<keys.length;i++){
						if(keys[i].getStatus() == Constants.KEY_STATUS_NEXT) {
							keys[i].setStatus(Constants.KEY_STATUS_CHANGED);
						}
						else if(keys[i].getStatus() == Constants.KEY_STATUS_SELECTED) {
							if(slidePath.findKeyByKeyId(keys[i].getId()) == -1){
								if(!keys[i].getType().equals("aoe")){
									keys[i].setStatus(Constants.KEY_STATUS_CHANGED);
								}
							}
						}
					}
					//draw new "next" keys
					lastKey = (HexKey)keys[id];
					String nextLetters;
					if(startupKey.getType().equals("aoe")){
						nextLetters = String.valueOf(pinyinTree.getNextLetters("v" + slidePath.getPinyin()));
					}else{
						nextLetters = String.valueOf(pinyinTree.getNextLetters(slidePath.getPinyin()));
					}
					for(int i=0; i<lastKey.getNeighbours().size();i++){
						if(!((HexKey)keys[lastKey.getNeighbours().get(i).hexKeyId]).getDynamicName().equals("")){
							char neighbourName = ((HexKey)keys[lastKey.getNeighbours().get(i).hexKeyId]).getDynamicName().charAt(0);
							for(int j=0; j<nextLetters.length(); j++){
								if(neighbourName == nextLetters.charAt(j)){
									keys[lastKey.getNeighbours().get(i).hexKeyId].setStatus(Constants.KEY_STATUS_NEXT);
								}
							}
						}
					}
					tipsView.update();
				}
				return true;
			}else{
				//this key is identical as the last key, we don't need to redraw the keyboard
				return false;
			}
		}else if(isOperatingCandidateBar){
			candidateBar.onMove(x,y);
			return true;
		}
		else{
			return false;
			
			
		}
	}

	@Override
	public String onRelease(float x, float y) {
		// release tips view and long press handler
		tipsView.setVisibility(View.INVISIBLE);
		tipsView.update();
		_handler.removeCallbacks(_longPressed);
		// handle the keys
		if(startupKey != null && slidePath.getSize() != 0){
			if(slidePath.getFirstHexKey().getType().equals("letter")){
				candidateBar.addPinyinString(slidePath.getPinyin());
				//candidateBar.setStatus("visible");
				words.add(slidePath.getPinyin());
				if(backupKey!=null){
					copyInformation(startupKey,backupKey);
					backupKey = null;
				}
				WordsStatistics.getInstance().addInputWords(slidePath.getPinyin());
				clearKeyboard();
			}else if(slidePath.getFirstHexKey().getType().equals("delete")){
				deleteLastWord();
				clearKeyboard();
			}else if(slidePath.getFirstHexKey().getType().equals("space")){
				onResetHexKeyboard();
				Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE));
				Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SPACE));
			}else if(slidePath.getFirstHexKey().getType().equals("enter")){
				onResetHexKeyboard();
				Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
				Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
			}else if(slidePath.getFirstHexKey().getType().equals("comma")){
				onResetHexKeyboard();
				Globals.service.getCurrentInputConnection().commitText("£¬", 1);
			}else if(slidePath.getFirstHexKey().getType().equals("question")){
				onResetHexKeyboard();
				Globals.service.getCurrentInputConnection().commitText("£¿", 1);
			}else if(slidePath.getFirstHexKey().getType().equals("period")){
				onResetHexKeyboard();
				Globals.service.getCurrentInputConnection().commitText("¡£", 1);
			}else if(slidePath.getFirstHexKey().getType().equals("en")){
				onResetHexKeyboard();
				stopShowSpeed();
				return "en";
			}else if(slidePath.getFirstHexKey().getType().equals("setting")){
				onResetHexKeyboard();
				return "setting";
			}else if(slidePath.getFirstHexKey().getType().equals("symbols")){
				onResetHexKeyboard();
				stopShowSpeed();
				return "symbols";
			}else{
				clearKeyboard();
			}
		}else if(isOperatingCandidateBar){
			boolean isSentenceFinished = candidateBar.onRelease(x, y);
			if(isSentenceFinished){
				words.clear();
			}
			isOperatingCandidateBar = false;
		}
		clearKeyboard();
		return null;
	}
	
	/**
	 * @param x
	 * @param y
	 * @return the id of the hex key
	 */
	private int getHexKeyByPointOnTouch(float x, float y){
		int hexkeyID = -1;
		float shortest = 10000000;
		for(int i=0; i<keys.length-3; i++){
			float distance = (keys[i].getCenterX() - x)*(keys[i].getCenterX() - x) + (keys[i].getCenterY() - y)*(keys[i].getCenterY() - y);
			if(distance < shortest){
				shortest = distance;
				hexkeyID = i;
			}
		}
		return hexkeyID;
	}
	
	/**
	 * @param x
	 * @param y
	 * @return the neighbour hex key that is closest to the this.lastKey
	 */
	private int getHexKeyByPointOnMove(float x, float y){
		int hexkeyID = -1;
		float shortest = 10000000;
		/*
		float distance = 0;
		for(int i=0; i<lastKey.getNeighbours().size(); i++){
			distance = (keys[lastKey.getNeighbours().get(i).hexKeyId].getCenterX() - x)*(keys[lastKey.getNeighbours().get(i).hexKeyId].getCenterX() - x) + (keys[lastKey.getNeighbours().get(i).hexKeyId].getCenterY() - y)*(keys[lastKey.getNeighbours().get(i).hexKeyId].getCenterY() - y);
			if(distance < shortest){
				shortest = distance;
				hexkeyID = lastKey.getNeighbours().get(i).hexKeyId;
			}
		}
		distance = (lastKey.getCenterX() - x)*(lastKey.getCenterX() - x) + (lastKey.getCenterY() - y)*(lastKey.getCenterY() - y);
		if(distance < shortest){
			shortest = distance;
			hexkeyID = lastKey.getId();
		}
		*/
		for(int i=0; i<keys.length-3; i++){
			float distance = (keys[i].getCenterX() - x)*(keys[i].getCenterX() - x) + (keys[i].getCenterY() - y)*(keys[i].getCenterY() - y);
			if(distance < shortest){
				shortest = distance;
				hexkeyID = i;
			}
		}
		return hexkeyID;
	}
	
	private void clearKeyboard(){
		for(int i=0; i<keys.length;i++){
			((HexKey)keys[i]).setDynamicName("");
			keys[i].setStatus(Constants.KEY_STATUS_NORMAL);
		}
		startupKey = null;
		lastKey = null;
		slidePath.clear();
	}
	
	private void clearWords(){
		words = new ArrayList<String>();
		candidateBar.clearCandidateBar();
	}
	
	private int findKeyIdByName(String name){
		for(int i=0; i<keys.length; i++){
			if(keys[i].getName().equals(name)){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * this will copy the key2's name, neighbours, dynamicHexKeys information into key1
	 * 
	 * @param key1 the hex key to copy
	 * @param key2 the hex key to be copied
	 */
	private void copyInformation(HexKey key1, HexKey key2){
		key1.setName(key2.getName());
		key1.setNeighbours(key2.getNeighbours());
		key1.setDynamicHexKeys(key2.getDynamicHexKeys());
	}
	
	private void deleteLastWord(){
		if(candidateBar.deleteTriggered()){
			Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
			Globals.service.getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
		}
	}
	
	public int getCandidateTextSize() {
		return candidateTextSize;
	}

	public void setCandidateTextSize(int candidateTextSize) {
		this.candidateTextSize = candidateTextSize;
	}
	
	public void onResetHexKeyboard(){
		clearKeyboard();
		clearWords();
		candidateBar.clearCandidateBar();
	}
	
	
	public void setHexKeyOffset(float offset){
		for(int i=0; i<keys.length-3; i++){
			float leftTop = keys[i].getLeftTopX();
			float centerX = keys[i].getCenterX();
			keys[i].setLeftTopX(leftTop + offset);
			keys[i].setCenterX(centerX + offset);
		}
		float candidateBarLeftTop = candidateBar.getLeftTopX();
		float candidateBarCenterX = candidateBar.getCenterX();
		candidateBar.setLeftTopX(candidateBarLeftTop + offset);
		candidateBar.setCenterX(candidateBarCenterX + offset);
		float candidateBarLeftKeyLeftTop = candidateBar.getLeftKey().getLeftTopX();
		float candidateBarLeftKeyCenterX = candidateBar.getLeftKey().getCenterX();
		candidateBar.getLeftKey().setLeftTopX(candidateBarLeftKeyLeftTop + offset);
		candidateBar.getLeftKey().setCenterX(candidateBarLeftKeyCenterX + offset);
		float candidateBarRightKeyLeftTop = candidateBar.getRightKey().getLeftTopX();
		float candidateBarRightKeyCenterX = candidateBar.getRightKey().getCenterX();
		candidateBar.getRightKey().setLeftTopX(candidateBarRightKeyLeftTop + offset);
		candidateBar.getRightKey().setCenterX(candidateBarRightKeyCenterX + offset);
		//candidateBar.setConfigurationOffset(offset);
	}

	public void startShowSpeed() {
		this.ifContinue = true;
		if (!this._showSpeedThread.isAlive()) {
			this._showSpeedThread.start();
		} else {
			synchronized (this._showSpeedThread) {
				this._showSpeedThread.notify();
			}
		}
	}
	
	public void resumeShowSpeed() {
		synchronized (this._showSpeedThread) {
			this.ifContinue = true;
			this._showSpeedThread.notify();
		}
	}
	
	public void stopShowSpeed() {
		synchronized (this._showSpeedThread) {
			this.ifContinue = false;
		}
	}
}
