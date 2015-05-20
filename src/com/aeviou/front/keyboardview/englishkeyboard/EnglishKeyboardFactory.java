package com.aeviou.front.keyboardview.englishkeyboard;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.aeviou.Util.Constants;
import com.aeviou.Util.XMLReader;

public class EnglishKeyboardFactory {

	private static EnglishKeyboardFactory instance = null;
	//private AbstractKeyboard abdstractKeyboardInstance = null;
	private EnglishKeyboard englishKeyboardInstance = null;
	private int screenWidth, screenHeight;
	private int englishkeyWidth, englishkeyHeight;
	private float padding;
	
	private EnglishKeyboardFactory(){
		
	}
	
	public static EnglishKeyboardFactory getInstance(){
		if(instance == null){
			instance = new EnglishKeyboardFactory();
		}
		return instance;
	}
	
	public void initialize(int screenwidth, int screenheight){
		this.screenWidth = screenwidth;
		this.screenHeight = screenheight;
	}
	
	public EnglishKeyboard createKeyboard(){
		if(englishKeyboardInstance == null){
			XMLReader xmlreader = XMLReader.getXMLReaderInstance();
			float englishkeyScale = xmlreader.getEnglishKeyScale();
			float englishkeyPaddingScale = xmlreader.getEnglishKeyPadding();
			int englishkeyBigrow = xmlreader.getEnglishKeyBigRow();
			EnglishKey[] keys = new EnglishKey[xmlreader.getEnglishKeyAmount()];
			NodeList rows = xmlreader.getEnglishKeysNodeList();
			int paddings = englishkeyBigrow + 1;
			englishkeyWidth = (int)((float)screenWidth/(float)(englishkeyBigrow + paddings*englishkeyPaddingScale) + 0.5);
			englishkeyHeight = (int)(englishkeyWidth*englishkeyScale + 0.5);
			padding = ((screenWidth - englishkeyWidth*englishkeyBigrow)/(float)(englishkeyBigrow+1));
			//int keyIndex = 0;
			for(int k=0; k<rows.getLength(); k++){
				Element row = (Element)rows.item(k);
				int keyamount = Integer.parseInt(row.getAttribute("keys"));
				NodeList keyList = row.getElementsByTagName("key");
				for(int i=0; i<keyList.getLength(); i++){
					Element key = (Element)keyList.item(i);
					float tab = Float.parseFloat(key.getAttribute("tab"));
					int keyId = Integer.parseInt(key.getAttribute("id"));
					String subtext = "";
					if (keyId < 26) {
						subtext = key.getAttribute("subtext");
					}
					keys[keyId] = new EnglishKey(key.getAttribute("name"), Constants.KEY_STATUS_NORMAL, key.getAttribute("type"), subtext, keyId);
					//set key type
					keys[keyId].setType(key.getAttribute("type"));
					//set row
					keys[keyId].setRow(k+1);
					//set key's lefttop position, center position
					if(keyamount == englishkeyBigrow){
						keys[keyId].setLeftTopX(padding + i*(padding+englishkeyWidth));
						keys[keyId].setCenterX(padding + i*(padding+englishkeyWidth) + (float)englishkeyWidth/2);
					}else{
						keys[keyId].setLeftTopX(padding + (float)englishkeyWidth/2 + i*(padding+englishkeyWidth));
						keys[keyId].setCenterX(padding + i*(padding+englishkeyWidth) + englishkeyWidth);
					}
					if(i == 0 && tab > 1.4 && tab < 1.6) {
						keys[keyId].setLeftTopX(padding + i*(padding+englishkeyWidth));
						keys[keyId].setCenterX(padding + i*(padding+englishkeyWidth) + (float)englishkeyWidth/2);
					}
					keys[keyId].setLeftTopY(k*(padding+englishkeyHeight) + englishkeyHeight*0.1f);
					keys[keyId].setCenterY(k*(padding+englishkeyHeight) + (float)englishkeyHeight/2 + englishkeyHeight*0.1f);
					keys[keyId].setWidth(englishkeyWidth*tab+padding*(tab-1));
					keys[keyId].setHeight(englishkeyHeight*tab+padding*(tab-1));
				}
			}
			englishKeyboardInstance = new EnglishKeyboard(keys);
		}
		return englishKeyboardInstance;
	}

	public int getEnglishkeyWidth() {
		return englishkeyWidth;
	}

	public void setEnglishkeyWidth(int englishkeyWidth) {
		this.englishkeyWidth = englishkeyWidth;
	}

	public int getEnglishkeyHeight() {
		return englishkeyHeight;
	}

	public void setEnglishkeyHeight(int englishkeyHeight) {
		this.englishkeyHeight = englishkeyHeight;
	}

	public float getPadding() {
		return padding;
	}

	public void setPadding(float padding) {
		this.padding = padding;
	}

}
