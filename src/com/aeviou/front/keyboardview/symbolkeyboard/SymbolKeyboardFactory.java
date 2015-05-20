package com.aeviou.front.keyboardview.symbolkeyboard;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.aeviou.Util.Constants;
import com.aeviou.Util.XMLReader;

// FIXME: is it possible to abstract the keyboardFactory class? 
public class SymbolKeyboardFactory {
	private static SymbolKeyboardFactory instance = null;
	//private AbstractKeyboard abdstractKeyboardInstance = null;
	private SymbolKeyboard symbolKeyboardInstance = null;
	private int screenWidth, screenHeight;
	private int symbolkeyWidth, symbolkeyHeight;
	private int padding;
	
	private SymbolKeyboardFactory(){
		
	}
	
	public static SymbolKeyboardFactory getInstance(){
		if(instance == null){
			instance = new SymbolKeyboardFactory();
		}
		return instance;
	}
	
	public void initialize(int screenwidth, int screenheight){
		this.screenWidth = screenwidth;
		this.screenHeight = screenheight;
	}
	
	public SymbolKeyboard createKeyboard(){
		if(symbolKeyboardInstance == null){
			XMLReader xmlreader = XMLReader.getXMLReaderInstance();
			float symbolkeyScale = xmlreader.getSymbolKeyScale();
			float symbolkeyPaddingScale = xmlreader.getSymbolKeyPadding();
			int symbolkeyBigrow = xmlreader.getSymbolKeyBigRow();
			
			SymbolKey[] keys = new SymbolKey[xmlreader.getSymbolKeyAmount()];
			NodeList rows = xmlreader.getSymbolKeysNodeList();
			
			int paddings = symbolkeyBigrow + 1;
			symbolkeyWidth = (int)((float)screenWidth/(float)(symbolkeyBigrow + paddings*symbolkeyPaddingScale));
			symbolkeyHeight = (int)(symbolkeyWidth*symbolkeyScale + 0.5);
			padding = (screenWidth - symbolkeyWidth*symbolkeyBigrow)/(symbolkeyBigrow+1);
			
			int keyIndex = 0;
			for(int k=0; k<rows.getLength(); k++){
				Element row = (Element)rows.item(k);
				int keyamount = Integer.parseInt(row.getAttribute("keys"));
				NodeList keyList = row.getElementsByTagName("key");
				
				for(int i=0; i<keyList.getLength(); i++, keyIndex++){
					Element key = (Element)keyList.item(i);
					float tab = Float.parseFloat(key.getAttribute("tab"));
					
					NodeList keyValues = key.getElementsByTagName("value");
					String[] values = null;
					if (keyValues != null && keyValues.getLength() > 0){
						values = new String[keyValues.getLength()];
						for (int j=0; j<keyValues.getLength(); j++){
							Element value = (Element)keyValues.item(j);
							values[j] = value.getAttribute("text");
						}
					}
					
					keys[keyIndex] = new SymbolKey(key.getAttribute("name"), values, Constants.KEY_STATUS_NORMAL, key.getAttribute("type"), Integer.parseInt(key.getAttribute("id")));
					keys[keyIndex].setRow(k+1);
					if(keyamount == symbolkeyBigrow){
						keys[keyIndex].setLeftTopX(padding + i*(padding+symbolkeyWidth));
						keys[keyIndex].setCenterX(padding + i*(padding+symbolkeyWidth) + (float)symbolkeyWidth/2);
					}else{
						keys[keyIndex].setLeftTopX(padding + (float)symbolkeyWidth/2 + i*(padding+symbolkeyWidth));
						keys[keyIndex].setCenterX(padding + i*(padding+symbolkeyWidth) + symbolkeyWidth);
					}
					
					if(i == 0 && tab > 1.4 && tab < 1.6) {
						keys[keyIndex].setLeftTopX(padding + i*(padding+symbolkeyWidth));
						keys[keyIndex].setCenterX(padding + i*(padding+symbolkeyWidth) + (float)symbolkeyWidth/2);
					}
					
					keys[keyIndex].setLeftTopY(k*(padding+symbolkeyHeight) + symbolkeyHeight*0.1f);
					keys[keyIndex].setCenterY(k*(padding+symbolkeyHeight) + (float)symbolkeyHeight/2 + symbolkeyHeight*0.1f);
					keys[keyIndex].setWidth(symbolkeyWidth*tab + padding*(tab-1));
					keys[keyIndex].setHeight(symbolkeyHeight*tab + padding*(tab-1));
				}
			}
			symbolKeyboardInstance = new SymbolKeyboard(keys);
			symbolKeyboardInstance.setMaxPage(xmlreader.getSymbolMaxPage());
		}
		return symbolKeyboardInstance;
	}

	public int getSymbolkeyWidth() {
		return symbolkeyWidth;
	}

	public void setSymbolkeyWidth(int symbolkeyWidth) {
		this.symbolkeyWidth = symbolkeyWidth;
	}

	public int getSymbolkeyHeight() {
		return symbolkeyHeight;
	}

	public void setSymbolkeyHeight(int symbolkeyHeight) {
		this.symbolkeyHeight = symbolkeyHeight;
	}
	
	public int getPadding() {
		return padding;
	}
}
