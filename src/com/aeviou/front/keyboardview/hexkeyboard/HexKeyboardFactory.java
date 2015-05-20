package com.aeviou.front.keyboardview.hexkeyboard;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.aeviou.Util.Constants;
import com.aeviou.Util.Globals;
import com.aeviou.Util.XMLReader;

public class HexKeyboardFactory {
	private static HexKeyboardFactory instance = null;
	private HexKeyboard hexKeyboardInstance = null;
	private int screenWidth,screenHeight;
	private int hexkeyWidth,hexkeyHeight;
	private float padding;

	private HexKeyboardFactory(){
	}
	
	public static HexKeyboardFactory getInstance(){
		if(instance == null){
			instance = new HexKeyboardFactory();
		}
		return instance;
	}
	
	public void initialize(int screenwidth, int screenheight){
		this.screenWidth = screenwidth;
		this.screenHeight = screenheight;
	}
	
	public HexKeyboard createKeyboard(){
		if(hexKeyboardInstance == null){
			XMLReader xmlreader = XMLReader.getXMLReaderInstance();
			float hexkeyScale = xmlreader.getHexKeyScale();
			float hexkeyPaddingScale = xmlreader.getHexKeyPadding();
			float edgeHeightScale = xmlreader.getHexKeyEdgeHeightScale();
			int hexkeyBigrow = xmlreader.getHexKeyBigRow();
			int bigrow = xmlreader.getHexKeyBigRow();
			HexKey[] keys = new HexKey[xmlreader.getHexKeyAmount()];
			CandidateBar candidateBar = null;
			NodeList rows = xmlreader.getHexKeysNodeList();
			int paddings = hexkeyBigrow + 1;
			hexkeyWidth = (int)((float)screenWidth/(float)(hexkeyBigrow + paddings*hexkeyPaddingScale) + 0.5); // hex key width has been rounded
			hexkeyHeight = (int)(hexkeyWidth*hexkeyScale + 0.5); // hex key height has been rounded
			padding = ((float)(screenWidth - hexkeyWidth*hexkeyBigrow))/((float)(hexkeyBigrow+1));// padding is float
			Globals.hexkeywidth = hexkeyWidth;
			Globals.hexkeyheight = hexkeyHeight;
			Globals.hexkeypadding = padding;
			int keyIndex = 0;
			for(int k=0; k<rows.getLength(); k++){ //k row
				Element row = (Element)rows.item(k);
				int keyamount = Integer.parseInt(row.getAttribute("keys"));
				NodeList keyList = row.getElementsByTagName("key");
				for(int i=0; i<keyList.getLength(); i++, keyIndex++){
					Element key = (Element)keyList.item(i);
					keys[keyIndex] = new HexKey(key.getAttribute("name"), Integer.parseInt(key.getAttribute("id")));
					//set key type
					keys[keyIndex].setType(key.getAttribute("type"));
					//set row
					keys[keyIndex].setRow(k);
					//set key's lefttop position, center position
					if(keyamount == bigrow){
						keys[keyIndex].setLeftTopX(padding + i*(padding+hexkeyWidth));
						keys[keyIndex].setCenterX(padding + i*(padding+hexkeyWidth) + (float)hexkeyWidth/2);
					}else{
						keys[keyIndex].setLeftTopX(padding + (float)hexkeyWidth/2 + i*(padding+hexkeyWidth));
						keys[keyIndex].setCenterX(padding + i*(padding+hexkeyWidth) + hexkeyWidth);
					}
					keys[keyIndex].setLeftTopY(k*(padding+hexkeyHeight*(1-(1-edgeHeightScale)/2)));
					keys[keyIndex].setCenterY(k*(padding+hexkeyHeight*(1-(1-edgeHeightScale)/2)) + (float)hexkeyHeight/2);
					//neighbour key
					NodeList neighbours = key.getElementsByTagName("neighbour");
					for(int j=0; j<neighbours.getLength() ;j++){
			    		Element neighbour = (Element)neighbours.item(j);
			    		List<HexKeyDynamic> NGs = new ArrayList<HexKeyDynamic>();
			    		//ngs under this neighbour
			    		NodeList ngs = neighbour.getElementsByTagName("ng");
			    		for(int l=0; l<ngs.getLength(); l++){
			    			Element NG = (Element)ngs.item(l);
			    			HexKeyDynamic ng = new HexKeyDynamic(Integer.valueOf(NG.getAttribute("id")), NG.getAttribute("name"));
			    			NGs.add(ng);
			    		}
			    		keys[keyIndex].addNeighbour(Integer.valueOf(neighbour.getAttribute("id")), neighbour.getAttribute("name"), NGs);
			    	}
					//dynamic key
					NodeList dynamicKeys = key.getElementsByTagName("dynamickey");
					for(int j=0; j<dynamicKeys.getLength();j++){
			    		Element dynamickey = (Element)dynamicKeys.item(j);
			    		keys[keyIndex].addDynamicKey(Integer.valueOf(dynamickey.getAttribute("id")), dynamickey.getAttribute("name"));
			    	}
					//set candidate bar
					if(k == 0 && i == 0){
						candidateBar = new CandidateBar();
						candidateBar.setLeftTopX(keys[i].getLeftTopX());
						candidateBar.setLeftTopY(keys[i].getLeftTopY()+1);
					}
					//set candidate bar left page key and right page key
					if(k == 0 && i == keyList.getLength()-1){
						HexKey leftHexKey = new HexKey();
						HexKey rightHexKey = new HexKey();
						leftHexKey.setName("left");
						rightHexKey.setName("right");
						leftHexKey.setStatus(Constants.KEY_STATUS_NORMAL);
						rightHexKey.setStatus(Constants.KEY_STATUS_NORMAL);
						leftHexKey.setLeftTopX(keys[0].getLeftTopX());
						leftHexKey.setLeftTopY(keys[0].getLeftTopY()+1);
						//Log.d("HexKeyboardFactory","leftHexKey leftx:" + keys[0].getLeftTopX() + ",lefty:" + keys[0].getLeftTopY());
						rightHexKey.setLeftTopX(keys[i].getLeftTopX());
						rightHexKey.setLeftTopY(keys[0].getLeftTopY()+1);
						leftHexKey.setType("left");
						rightHexKey.setType("right");
						candidateBar.setLeftKey(leftHexKey);
						candidateBar.setRightKey(rightHexKey);
					}
				}
			}
			hexKeyboardInstance = new HexKeyboard(keys, candidateBar, bigrow);
			Globals.textsize = hexkeyWidth/2;
		}
		return hexKeyboardInstance;
	}
	
	public int getHexkeyWidth() {
		return hexkeyWidth;
	}

	public void setHexkeyWidth(int hexkeyWidth) {
		this.hexkeyWidth = hexkeyWidth;
	}

	public int getHexkeyHeight() {
		return hexkeyHeight;
	}

	public void setHexkeyHeight(int hexkeyHeight) {
		this.hexkeyHeight = hexkeyHeight;
	}
	
	public float getPadding(){
		return this.padding;
	}
}
