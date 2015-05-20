package com.aeviou.front.keyboardview.hexkeyboard;

import java.util.ArrayList;
import java.util.List;

import com.aeviou.Util.BitmapManager;
import com.aeviou.Util.Constants;
import com.aeviou.Util.Globals;
import com.aeviou.Util.WordsStatistics;
import com.aeviou.back.PinyinContext;
import com.aeviou.front.keyboardview.abstractkeyboard.AbstractKey;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

public class CandidateBar extends AbstractKey{
	private final int CANDIDATEBAR_WIDTH = Globals.screenwidth - Globals.hexkeywidth*2;
	
	private List<CandidateWord> candidates;
	private float onTouchX, onTouchY;
	/**
	 * operationStatus state:
	 * 1, inactivated: candidate bar is in idle state
	 * 2, ontouch: candidate bar is touched
	 * 3, turning: candidate bar page is turning
	 * 4, leftKey: leftKey is on touch
	 * 5, rightKey: rightKey is on touch
	 */
	private String operationStatus;
	private CandidateWord onTouchedWord;
	private int onTouchedWordIndex;
	private PinyinContext context;
	private Paint onTouchTextPaint;
	private int lastPageStartCandidateIndex;
	private int lastPageEndCandidateIndex;
	private boolean endOfCandidates;
	private boolean startOfCandidates;
	private boolean isMOS;//is manually organizing sentence
	private boolean isEOAW;//is end of appointed word
	private SentenceView sentenceView;
	private List<String> pinyin;
	private List<String> sentence;//chinese word
	private ArrayList<Character> fullPinyin;
	private HexKey leftKey;
	private HexKey rightKey;
	private ArrayList<ObjectAnimator> animators;
	private int offset;
	private float configurationOffset = 0;

	public CandidateBar(){
		status = Constants.KEY_STATUS_HIDE;
		name = "candidate";
		candidates = new ArrayList<CandidateWord>();
		operationStatus = "inactivated";
		onTouchX = 0;
		onTouchY = 0;
		onTouchedWord = null;
		onTouchedWordIndex = -1;
		onTouchTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		onTouchTextPaint.setTextSize(Globals.textsize);
		onTouchTextPaint.setColor(Color.rgb(253, 112, 31));
		context = PinyinContext.getInstance();
		lastPageStartCandidateIndex = 0;
		lastPageEndCandidateIndex = 0;
		endOfCandidates = false;
		startOfCandidates = false;
		isMOS = false;
		isEOAW = false;
		this.sentenceView = new SentenceView(Globals.view.getContext(), (int)(Globals.hexkeywidth*0.5));
		pinyin = new ArrayList<String>();
		sentence = new ArrayList<String>();
		fullPinyin = new ArrayList<Character>();
		animators = new ArrayList<ObjectAnimator>();
		offset = 0;
	}
	
	public void draw(Canvas Cvs){
		BitmapManager.getInstance().drawHexKeyBitmap(leftTopX, leftTopY, name, Constants.KEY_STATUS_VISIBLE, Cvs);
		if(this.status == Constants.KEY_STATUS_VISIBLE){
			for(int i=lastPageStartCandidateIndex; i<=lastPageEndCandidateIndex; i++){
				candidates.get(i).draw(Cvs);
			}
			this.leftKey.draw(Cvs);
			this.rightKey.draw(Cvs);
		}
	}
	
	public void onTouch(float x, float y){
		onTouchX = x;
		onTouchY = y;
		//any operation would be invalid if there's animation running
		if(isAnimationRunning()){
			return;
		}
		//record related position of candidates and finger
		setLastPageCandidateRelatedPosition(x, y);
		//make last onTouchedWord's text paint return to normal(BLACK) color
		if(onTouchedWord != null) {
			Paint tempPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			tempPaint.setTextSize(Globals.textsize);
			tempPaint.setColor(Color.BLACK);
			onTouchedWord.setPaint(tempPaint);
		}
		if(status == Constants.KEY_STATUS_VISIBLE) {
			if(x < Globals.hexkeywidth + Globals.hexkeypadding) {
				//if user's touching "left" page key
				Globals.view.clearAnimation();
				animators.clear();
				operationStatus = "leftKey";
				leftKey.setStatus(Constants.KEY_STATUS_SELECTED);
				if(!startOfCandidates) {
					lastPage();
				}
			} else if(x > Globals.screenwidth - Globals.hexkeywidth - Globals.hexkeypadding) {
				//if user's touching "right" page key
				Globals.view.clearAnimation();
				animators.clear();
				rightKey.setStatus(Constants.KEY_STATUS_SELECTED);
				operationStatus = "rightKey";
				if(!endOfCandidates){
					nextPage();
				}
			} else {
				//user is touching one of the candidates
				//make new onTouchedWord's text paint red
				onTouchedWordIndex = findCandidateWordByPoint(x, y);
				onTouchedWord = candidates.get(onTouchedWordIndex);
				Paint tempPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				tempPaint.setTextSize(Globals.textsize);
				tempPaint.setColor(Color.rgb(253, 112, 31));
				onTouchedWord.setPaint(tempPaint);
				//change operation status to "ontouch"
				operationStatus = "ontouch";
			}
		}
	}
	
	public void onMove(float x, float y){
		if(operationStatus.equals("ontouch")){
			if(Math.abs(x - onTouchX) > Globals.textsize/5){
				operationStatus = "turning";
			}
		}else if(operationStatus.equals("turning")){
			slide(x,y);
		}
	}
	
	/**
	 * @param x
	 * @param y
	 * @return if user finished this sentence return true. else return false.
	 */
	public boolean onRelease(float x, float y){
		if(operationStatus.equals("ontouch")){
			if(isMOS){
				sentence.add(onTouchedWord.getName());
				fullPinyin.addAll(context.getPinyinListOfCandidate(onTouchedWordIndex));
				if(sentence.size() >= pinyin.size()){
					String tempString = "";
					for(int i=0; i<sentence.size(); i++){
						String curStr = sentence.get(i);
						tempString += curStr;
						WordsStatistics.getInstance().addValidWords(curStr);
					}
					Log.d("CandidateBar","new word:" + tempString);
					Globals.service.getCurrentInputConnection().commitText(tempString, 1);
					context.recordCustom(fullPinyin, tempString);
					clearCandidateBar();
					operationStatus = "inactivated";
					return true;
				}else{
					commitSentenceToPopupWindow();
					chooseNextCandidate(pinyin.get(sentence.size()));
					operationStatus = "inactivated";
					return false;
				}
			}else{
				if(onTouchedWord.getName().length() == 1 && pinyin.size() > 1){
					isMOS = true;
					sentence.add(onTouchedWord.getName());
					fullPinyin.addAll(context.getPinyinListOfCandidate(onTouchedWordIndex));
					commitSentenceToPopupWindow();
					chooseNextCandidate(pinyin.get(sentence.size()));
					operationStatus = "inactivated";
					return false;
				}else{
					WordsStatistics.getInstance().addValidWords(onTouchedWord.getName());
					Globals.service.getCurrentInputConnection().commitText(onTouchedWord.getName(), 1);
					context.chooseCandidate(onTouchedWordIndex);
					clearCandidateBar();
					operationStatus = "inactivated";
					return true;
				}
			}
		}else if(operationStatus.equals("leftKey")){
			leftKey.setStatus(Constants.KEY_STATUS_NORMAL);
			operationStatus = "inactivated";
			return true;
		}else if(operationStatus.equals("rightKey")){
			rightKey.setStatus(Constants.KEY_STATUS_NORMAL);
			operationStatus = "inactivated";
			return true;
		}else{
			return false;
		}
	}
	
	public void addPinyinString(String word){
		if(isAnimationRunning()){
			return;//any operation would be invalid if there's animation running
		}
		if(isMOS){
			sentence = new ArrayList<String>();
			fullPinyin = new ArrayList<Character>();
			commitSentenceToPopupWindow();
			context.clearContext();
			for(String pinyinWord: pinyin){
				Log.d("CandidateBar:addPinyinString","word:" + pinyinWord);
				context.addJianpin(pinyinWord);
			}
			isMOS = false;
		}
		chooseNextCandidate(word);
	}
	
	private void chooseNextCandidate(String word){
		if(!isMOS){
			pinyin.add(word);
		}else{
			context.clearContext();
		}
		Log.d("CandidateBar","pinyin.size():" + pinyin.size());
		if(isEOAW){
			isEOAW = false;
			for(int i = 1; i<pinyin.size() ; i++){
				Log.d("CandidateBar:chooseNextCandidate isEOAW","word:" + pinyin.get(i));
				context.addJianpin(pinyin.get(i));
			}
			//2014-8-5
			if(pinyin.size() == 1){
				context.addJianpin(pinyin.get(0));
			}
		}else{
			Log.d("CandidateBar:chooseNextCandidate","word:" + word);
			context.addJianpin(word);
		}
		resetCandidateBar();
		initializeCandidateBar();
		commitSentenceToPopupWindow();
	}
	
	private void nextPage(){
		float slideDistance = 0;
		if(candidates.get(lastPageEndCandidateIndex).getCenterX() + candidates.get(lastPageEndCandidateIndex).getName().length()*Globals.textsize > Globals.screenwidth - Globals.hexkeywidth){
			slideDistance = candidates.get(lastPageEndCandidateIndex).getCenterX() + candidates.get(lastPageEndCandidateIndex).getName().length()*Globals.textsize - (Globals.screenwidth - Globals.hexkeywidth);
		}
		while(context.getCandidate(lastPageEndCandidateIndex + 1 - offset) != null){
			//set new candidate coordinate
			if(candidates.size() <= lastPageEndCandidateIndex + 1){
				addNewCandidate(context.getCandidate(lastPageEndCandidateIndex + 1 - offset), candidates.get(lastPageEndCandidateIndex).getCenterX() + (candidates.get(lastPageEndCandidateIndex).getName().length() + 1)*Globals.textsize, Globals.hexkeyheight/2);
			}else{
				candidates.get(lastPageEndCandidateIndex + 1).setCenterX(candidates.get(lastPageEndCandidateIndex).getCenterX() + (candidates.get(lastPageEndCandidateIndex).getName().length() + 1)*Globals.textsize);
			}
			lastPageEndCandidateIndex++;
			//set slide distance
			if(candidates.get(lastPageEndCandidateIndex).getCenterX() + (candidates.get(lastPageEndCandidateIndex).getName().length() + 1)*Globals.textsize >= Globals.screenwidth + CANDIDATEBAR_WIDTH){
				slideDistance = CANDIDATEBAR_WIDTH;
				break;
			}else{
				//Log.d("CandidateBar","this:" + candidates.get(lastPageEndCandidateIndex).getName());
				slideDistance = candidates.get(lastPageEndCandidateIndex).getCenterX() + candidates.get(lastPageEndCandidateIndex).getName().length()*Globals.textsize - (Globals.screenwidth - Globals.hexkeywidth);
				Log.d("CandidateBar","lastIndex:" + lastPageEndCandidateIndex + ",slideDistance:" + slideDistance + ",offset:" + offset);
				if(context.getCandidate(lastPageEndCandidateIndex + 1 - offset) == null){
					if(candidates.get(lastPageEndCandidateIndex).getName().length() > 1){
						context.clearContext();
						Log.d("CandidateBar:nextPage","word:" + pinyin.get(0));
						context.addJianpin(pinyin.get(0));
						if(context.getCandidate(0) != null){
							offset = lastPageEndCandidateIndex + 1;
						}else{
							endOfCandidates = true;
							break;
						}
					}
				}
			}
		}
		//set candidate state
		if(slideDistance > 0){
			startOfCandidates = false;
		}
		if(slideDistance < CANDIDATEBAR_WIDTH){
			endOfCandidates = true;
		}else{
			endOfCandidates = false;
		}
		
		
		
		
		//set slide animation
		for(int i=lastPageStartCandidateIndex; i<=lastPageEndCandidateIndex; i++){
			candidates.get(i).setCenterX(candidates.get(i).getCenterX() - slideDistance);
			/*
			ObjectAnimator anim = ObjectAnimator.ofFloat(candidates.get(i), "centerX", candidates.get(i).getCenterX(), candidates.get(i).getCenterX() - slideDistance).setDuration(700);
			anim.addUpdateListener(Globals.view);
			anim.setInterpolator(new DecelerateInterpolator());
			//animators.add(anim);
			if(i == lastPageEndCandidateIndex){
				anim.addListener(new AnimatorListenerAdapter() {
	                @Override
	                public void onAnimationEnd(Animator animation){
	                	while(candidates.get(lastPageStartCandidateIndex).getCenterX() + candidates.get(lastPageStartCandidateIndex).getName().length()*Globals.textsize <= Globals.hexkeywidth){
	                		lastPageStartCandidateIndex++;
	                	}
	                	animators.clear();
	                }
	            });
			}
			anim.start();
			*/
			
			
		}
		while(candidates.get(lastPageStartCandidateIndex).getCenterX() + candidates.get(lastPageStartCandidateIndex).getName().length()*Globals.textsize <= Globals.hexkeywidth){
    		lastPageStartCandidateIndex++;
    	}
		//start animation
		//startAnimation();
	}
	
	private void lastPage(){
		float slideDistance = 0;
		while(lastPageStartCandidateIndex - 1>=0){
			//set new candidate coordinate
			float centerX = candidates.get(lastPageStartCandidateIndex).getCenterX() - (candidates.get(lastPageStartCandidateIndex - 1).getName().length() + 1)*Globals.textsize;
			candidates.get(lastPageStartCandidateIndex - 1).setCenterX(centerX);
			lastPageStartCandidateIndex--;
			//slide distance
			if(candidates.get(lastPageStartCandidateIndex).getCenterX() + CANDIDATEBAR_WIDTH <= Globals.hexkeywidth){
				slideDistance = CANDIDATEBAR_WIDTH;
				break;
			}else{
				slideDistance = Globals.hexkeywidth - candidates.get(lastPageStartCandidateIndex).getCenterX() + Globals.textsize;//" + Constants.textsize" is a bug?
			}
		}
		if(slideDistance > 0){
			endOfCandidates = false;
		}
		if(slideDistance < CANDIDATEBAR_WIDTH){
			startOfCandidates = true;
		}else{
			startOfCandidates = false;
		}
		
		for(int i=lastPageStartCandidateIndex; i<=lastPageEndCandidateIndex; i++){
			candidates.get(i).setCenterX(candidates.get(i).getCenterX() + slideDistance);
			/*
			ObjectAnimator anim = ObjectAnimator.ofFloat(candidates.get(i), "centerX", candidates.get(i).getCenterX(), candidates.get(i).getCenterX() + slideDistance).setDuration(700);
			anim.addUpdateListener(Globals.view);
			anim.setInterpolator(new DecelerateInterpolator());
			animators.add(anim);
			if(i == lastPageEndCandidateIndex){
				anim.addListener(new AnimatorListenerAdapter() {
	                @Override
	                public void onAnimationEnd(Animator animation){
	                	while(candidates.get(lastPageEndCandidateIndex).getCenterX() >= Globals.screenwidth){
	                		lastPageEndCandidateIndex--;
	                	}
	                	animators.clear();
	                }
	            });
			}
			*/
		}
		while(candidates.get(lastPageEndCandidateIndex).getCenterX() >= Globals.screenwidth){
    		lastPageEndCandidateIndex--;
    	}
		//start animation
		//startAnimation();
	}
	
	private void startAnimation(){
		for(ObjectAnimator animator: animators){
			animator.start();
		}
	}
	
	/**
	 * initialize first page candidate words
	 */
	private void initializeCandidateBar(){
		if(context.getCandidate(0) != null){
			addNewCandidate(context.getCandidate(0), configurationOffset + Globals.hexkeywidth + Globals.textsize, Globals.hexkeyheight/2);
			while(candidates.get(lastPageEndCandidateIndex).getCenterX() + (candidates.get(lastPageEndCandidateIndex).getName().length()+1)*Globals.textsize < configurationOffset + Globals.candidateBarWidth){
				String word = context.getCandidate(lastPageEndCandidateIndex + 1 - offset);
				Log.d("CandidateBar:initializeCandidateBar","word:" + word);
				if(word == null){
					if(context.getCandidate(0).length() == 1){
						endOfCandidates = true;
						break;
					}else{
						context.clearContext();
						isEOAW = true;
						Log.d("CandidateBar:initializeCandidateBar","word:" + pinyin.get(0));
						context.addJianpin(pinyin.get(0));
						if(context.getCandidate(0) != null){
							offset = lastPageEndCandidateIndex + 1;
						}else{
							endOfCandidates = true;
							break;
						}
					}
				}else{
					endOfCandidates = false;
					addNewCandidate(word, (candidates.get(lastPageEndCandidateIndex).getCenterX()+(candidates.get(lastPageEndCandidateIndex).getName().length()+1)*Globals.textsize), Globals.hexkeyheight/2);
					lastPageEndCandidateIndex++;
				}
			}
			startOfCandidates = true;
			setStatus(Constants.KEY_STATUS_VISIBLE);
		}else{
			Log.d("CandidateBar","Context is null!!");
			if(pinyin.size() > 1){
				//manually organize sentence
				isMOS = true;
				chooseNextCandidate(pinyin.get(sentence.size()));
			}else{
				setStatus(Constants.KEY_STATUS_HIDE);
			}
		}
	}
	
	/**
	 * make candidate words slide as user slide candidate bar
	 * @param x
	 * @param y
	 */
	private void slide(float x, float y){
		if(x < onTouchX){
			if(!endOfCandidates){
				for(int i=lastPageStartCandidateIndex; i<=lastPageEndCandidateIndex; i++){
					candidates.get(i).setCenterX(x + candidates.get(i).getxDifference());
				}
				slideToNextPage(x, y);
			}
		}else{
			if(!startOfCandidates){
				for(int i=lastPageStartCandidateIndex; i<=lastPageEndCandidateIndex; i++){
					candidates.get(i).setCenterX(x + candidates.get(i).getxDifference());
				}
				slideToLastPage(x, y);
			}
		}
	}
	
	/**
	 * when user slide to next candidate
	 * make new(upcoming) candidate appear, make old(slide away) candidate disapear
	 */
	private void slideToNextPage(float x, float y){
		//make new appear
		while(candidates.get(lastPageEndCandidateIndex).getCenterX() + (candidates.get(lastPageEndCandidateIndex).getName().length()+1)*Globals.textsize < Globals.screenwidth){
			String word = context.getCandidate(lastPageEndCandidateIndex + 1 - offset);
			if(word == null){
				if(context.getCandidate(0).length() == 1){
					endOfCandidates = true;
					break;
				}else{
					context.clearContext();
					Log.d("CandidateBar:slideToNextPage","word:" + pinyin.get(0));
					context.addJianpin(pinyin.get(0));
					if(context.getCandidate(0) != null){
						offset = lastPageEndCandidateIndex + 1;
					}else{
						endOfCandidates = true;
						break;
					}
				}
			}else{
				endOfCandidates = false;
				startOfCandidates = false;
				if(candidates.size() > lastPageEndCandidateIndex + 1){//already exist, user had just slide back
					candidates.get(lastPageEndCandidateIndex+1).setCenterX(candidates.get(lastPageEndCandidateIndex).getCenterX()+(candidates.get(lastPageEndCandidateIndex).getName().length()+1)*Globals.textsize);
				}else{
					addNewCandidate(word, (candidates.get(lastPageEndCandidateIndex).getCenterX()+(candidates.get(lastPageEndCandidateIndex).getName().length()+1)*Globals.textsize), Globals.hexkeyheight/2);
				}
				lastPageEndCandidateIndex++;
				
			}
		}
		//make old disapear
		while(candidates.get(lastPageStartCandidateIndex).getCenterX() - Globals.textsize < 0){
			lastPageStartCandidateIndex++;
		}
		//reset the related candidate position
		setLastPageCandidateRelatedPosition(x, y);
	}
	
	/**
	 * when user slide to last candidate
	 * make new(upcoming) candidate appear, make old(slide away) candidate disapear
	 * @param x
	 * @param y
	 */
	private void slideToLastPage(float x, float y){
		while(candidates.get(lastPageStartCandidateIndex).getCenterX() - (candidates.get(lastPageStartCandidateIndex).getName().length() + 1)*Globals.textsize > 0){
			if(lastPageStartCandidateIndex <= 0){
				startOfCandidates = true;
				//this is for adjustment: there might be some frame lose causing "slide left" cross the start line boundary
				//resetCandidateBar();
				//initializeCandidateBar();
				break;
			}else{
				lastPageEndCandidateIndex--;
				lastPageStartCandidateIndex--;
				candidates.get(lastPageStartCandidateIndex).setCenterX(candidates.get(lastPageStartCandidateIndex + 1).getCenterX() - (candidates.get(lastPageStartCandidateIndex).getName().length() + 1)*Globals.textsize);
				startOfCandidates = false;
				endOfCandidates = false;
				setLastPageCandidateRelatedPosition(x, y);
			}
		}
	}
	
	private void addNewCandidate(String word, float centerX, float centerY){
		CandidateWord newWord = new CandidateWord();
		newWord.setCenterX(centerX);
		newWord.setStartX(centerX);
		newWord.setCenterY(centerY);
		newWord.setStartY(centerY);
		Paint tempPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		tempPaint.setTextSize(Globals.textsize);
		tempPaint.setColor(Color.BLACK);
		newWord.setPaint(tempPaint);
		newWord.setName(word);
		candidates.add(newWord);
	}
	
	private void setLastPageCandidateRelatedPosition(float x, float y){
		for(int i=lastPageStartCandidateIndex; i<=lastPageEndCandidateIndex; i++){
			candidates.get(i).setxDifference(candidates.get(i).getCenterX() - x);
		}
	}
	
	public CandidateWord getLastWord(){
		if(candidates.size()>0){
			return candidates.get(candidates.size()-1);
		}else{
			return null;
		}
	}
	
	public CandidateWord getFirstWord(){
		if(candidates.size()>0){
			return candidates.get(0);
		}else{
			return null;
		}
	}
	
	public int candidateBarCandidateSize(){
		if(candidates!=null){
			return candidates.size();
		}else{
			return 0;
		}
	}
	
	public void resetCandidateBar(){
		onTouchedWordIndex = -1;
		onTouchedWord = null;
		candidates = new ArrayList<CandidateWord>();
		lastPageStartCandidateIndex = 0;
		lastPageEndCandidateIndex = 0;
		offset = 0;
	}
	
	public void clearCandidateBar(){
		context.clearContext();
		setStatus(Constants.KEY_STATUS_HIDE);
		resetCandidateBar();
		isMOS = false;
		pinyin = new ArrayList<String>();
		sentence = new ArrayList<String>();
		fullPinyin = new ArrayList<Character>();
		closeSentencePopupWindow();
	}
	
	/**
	 * handles all operations after delete event is triggered
	 * @return if pinyin string is empty return true, else return false
	 */
	public boolean deleteTriggered(){
		/*
		if(isAnimationRunning()){
			return false;//any operation would be invalid if there's animation running
		}
		*/
		if(pinyin.size() > 1){
			if(sentence.size() > 0){
				Log.d("CandidateBar", "sentence.size() > 0");
				sentence.remove(sentence.size()-1);
				fullPinyin.remove(fullPinyin.size()-1);
				chooseNextCandidate(pinyin.get(sentence.size()));
			}else{
				Log.d("CandidateBar", "sentence.size() == 0");
				isMOS = false;
				pinyin.remove(pinyin.size()-1);
				String temp = pinyin.get(pinyin.size()-1);
				pinyin.remove(pinyin.size()-1);
				resetCandidateBar();
				context.clearContext();
				for(int i=0; i<pinyin.size(); i++){
					Log.d("CandidateBar:deleteTriggered","word:" + pinyin.get(0));
					context.addJianpin(pinyin.get(i));
				}
				Log.d("CandidateBar", "temp:" + temp);
				chooseNextCandidate(temp);
			}
			return false;
		}else if(pinyin.size() == 1){
			Log.d("CandidateBar","pinyin.size() == 1");
			clearCandidateBar();
			return false;
		}else{
			//means there are no candidates in the candidate bar
			return true;
		}
	}
	
	private int findCandidateWordByPoint(float x, float y){
		float shortest = 1000000000;
		int selectedWordIndex = -1;
		for(int i=lastPageStartCandidateIndex; i<=lastPageEndCandidateIndex; i++){
			float distance = (candidates.get(i).getCenterX() - x)*(candidates.get(i).getCenterX() - x);
			if(shortest > distance){
				shortest = distance;
				selectedWordIndex = i;
			}
		}
		return selectedWordIndex;
	}
	
	private void commitSentenceToPopupWindow(){
		int pinyinSize = pinyin.size();
		int sentenceSize = sentence.size();
		String popupSentence = "";
		for(int i=0; i<pinyinSize; i++){
			if(i<sentenceSize){
				popupSentence += sentence.get(i);
			}else{
				popupSentence += pinyin.get(i) + "'";
			}
		}
		sentenceView.setSentence(popupSentence);
		sentenceView.commitUpdate();
	}
	
	private void closeSentencePopupWindow(){
		sentenceView.closeWindow();
	}
	
	private boolean isAnimationRunning(){
		for(ObjectAnimator anim: animators){
			if(anim.isRunning()){
				return true;
			}
		}
		return false;
	}
	
	public HexKey getLeftKey() {
		return leftKey;
	}

	public void setLeftKey(HexKey leftKey) {
		this.leftKey = leftKey;
	}

	public HexKey getRightKey() {
		return rightKey;
	}

	public void setRightKey(HexKey rightKey) {
		this.rightKey = rightKey;
	}
	
	public float getConfigurationOffset() {
		return configurationOffset;
	}

	public void setConfigurationOffset(float configurationOffset) {
		this.configurationOffset = configurationOffset;
	}
}
