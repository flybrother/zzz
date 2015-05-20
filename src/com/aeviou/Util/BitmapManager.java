package com.aeviou.Util;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

public class BitmapManager {
	private static BitmapManager instance;
	private Map<String,Bitmap>hexKeybitmaps = new HashMap<String,Bitmap>();
	private Map<String,Bitmap>englishKeyBitmaps = new HashMap<String,Bitmap>();
	private Map<String,Bitmap>symbolKeyBitmaps = new HashMap<String,Bitmap>();
	private int hexkeyWidth,hexkeyHeight;
	private int englishkeyWidth,englishkeyHeight;
	private float englishkeyPadding;
	private int symbolkeyWidth,symbolkeyHeight,symbolkeyPadding;
	private Resources res;
	private Matrix matrix = null;
	
	public BitmapManager(){
	}
	
	public static BitmapManager getInstance(){
		return instance = (instance == null ? new BitmapManager() : instance);
	}
	
	/**
	 * initialize variables, calculate hex key width and height and padding, load hex key bitmaps into the local variables
	 * @param screenwidth	the width of the screen
	 * @param screenheight	the height of the screen
	 * @param resource	
	 */
	public void initializeHexKeyBitmap(int hexkeywidth, int hexkeyheight, int screenWidth, int screenHeight, Resources resource){
		/*
		 * initialize variables
		 */
		hexkeyWidth = hexkeywidth;
		hexkeyHeight = hexkeyheight;
		res = resource;
		XMLReader xmlreader = XMLReader.getXMLReaderInstance();
		Bitmap hexkeyBitmap = BitmapFactory.decodeResource(res, res.getIdentifier("hexkeys", "drawable", "com.aeviou.front"));
		int bitmapw = xmlreader.getHexKeyBitmapWidth();
		int bitmaph = xmlreader.getHexKeyBitmapHeight();
		//the actual bitmap(hexkeyBitmap) may be scaled automatically to adjust to the actual screen dip by using decodeResource method, so we have to scale it back
		hexkeyBitmap = Bitmap.createScaledBitmap(hexkeyBitmap, bitmapw, bitmaph, true);
		int hexkeyw = xmlreader.getHexKeyImageWidth();
		int hexkeyh = xmlreader.getHexKeyImageHeight();
		Bitmap hexkeyMiscsBitmap = BitmapFactory.decodeResource(res, res.getIdentifier("hexkeymiscs", "drawable", "com.aeviou.front"));
		int miscbitmapw = xmlreader.getHexKeyMiscsBitmapWidth();
		int miscbitmaph  = xmlreader.getHexKeyMiscsBitmapHeight();
		hexkeyMiscsBitmap = Bitmap.createScaledBitmap(hexkeyMiscsBitmap, miscbitmapw, miscbitmaph, true);
		/*
		 * load hex key bitmaps
		 */
		NodeList hexKeyBitmapList = xmlreader.getHexKeyBitmapNodeList();
		for(int i=0; i<hexKeyBitmapList.getLength(); i++){
			Element hexkey = (Element)hexKeyBitmapList.item(i);
			String keyName = hexkey.getAttribute("name");
			NodeList bitmapNodeList = hexkey.getElementsByTagName("bitmap");
			for(int j=0; j<bitmapNodeList.getLength(); j++){
				Element bitmap = (Element)bitmapNodeList.item(j);
				String keyStatus = bitmap.getAttribute("status");
				int column = Integer.valueOf(bitmap.getAttribute("column"));
				int row = Integer.valueOf(bitmap.getAttribute("row"));
				//Log.d("BitmapManager",keyName + "_" + keyStatus + ":" + column + " " + row);
				matrix = new Matrix();
				matrix.postScale(((float)hexkeyWidth)/((float)hexkeyw), ((float)hexkeyHeight)/((float)hexkeyh));
				Bitmap scaledBitmap = Bitmap.createBitmap(hexkeyBitmap, hexkeyw*column, hexkeyh*row, hexkeyw, hexkeyh, matrix, true);
				hexKeybitmaps.put(keyName+"_"+keyStatus, scaledBitmap);
				//Log.d("BitmapManager",scaledBitmap.getWidth() + " " + scaledBitmap.getHeight());
				matrix = null;
			}
		}

		if (hexkeyBitmap != null && !hexkeyBitmap.isRecycled()) {
			hexkeyBitmap.recycle();
			hexkeyBitmap = null;
			Log.d("BitmapManager", "recycle hexkeyBitmap");
		}
		
		/*
		 * load hex key miscs
		 */
		matrix = new Matrix();
		matrix.postScale(((float)screenWidth)/((float)miscbitmapw), ((float)hexkeyHeight)/((float)116));
		Bitmap candidate = Bitmap.createBitmap(hexkeyMiscsBitmap, 0, 0, 769, 116, matrix, true);
		Globals.candidateBarWidth = screenWidth;
		hexKeybitmaps.put("candidate_visible", candidate);
		matrix = new Matrix();
		matrix.postScale(((float)hexkeyWidth)/((float)hexkeyw), ((float)hexkeyHeight)/((float)hexkeyh));
		Bitmap left_normal = Bitmap.createBitmap(hexkeyMiscsBitmap, 0, 116, 98, 110, matrix, true);
		hexKeybitmaps.put("left_normal", left_normal);
		matrix = new Matrix();
		matrix.postScale(((float)hexkeyWidth)/((float)hexkeyw), ((float)hexkeyHeight)/((float)hexkeyh));
		Bitmap left_selected = Bitmap.createBitmap(hexkeyMiscsBitmap, 0, 226, 98, 110, matrix, true);
		hexKeybitmaps.put("left_selected", left_selected);
		matrix = new Matrix();
		matrix.postScale(((float)hexkeyWidth)/((float)hexkeyw), ((float)hexkeyHeight)/((float)hexkeyh));
		Bitmap right_normal = Bitmap.createBitmap(hexkeyMiscsBitmap, 98, 116, 98, 110, matrix, true);
		hexKeybitmaps.put("right_normal", right_normal);
		matrix = new Matrix();
		matrix.postScale(((float)hexkeyWidth)/((float)hexkeyw), ((float)hexkeyHeight)/((float)hexkeyh));
		Bitmap right_selected = Bitmap.createBitmap(hexkeyMiscsBitmap, 98, 226, 98, 110, matrix, true);
		hexKeybitmaps.put("right_selected", right_selected);
		
		if (hexkeyMiscsBitmap != null && !hexkeyMiscsBitmap.isRecycled()) {
			hexkeyMiscsBitmap.recycle();
			hexkeyMiscsBitmap = null;
			Log.d("BitmapManager", "recycle hexkeyMiscsBitmap");
		}
	}
	
	public void initializeEnglishKeyBitmap(int englishkeywidth, int englishkeyheight, float englishkeypadding, Resources resources){
		//initialize variables
		englishkeyWidth = englishkeywidth;
		englishkeyHeight = englishkeyheight;
		englishkeyPadding = englishkeypadding;
		
		res = resources;
		XMLReader xmlreader = XMLReader.getXMLReaderInstance();
		Bitmap enkeyBitmap = BitmapFactory.decodeResource(res, res.getIdentifier("enkeys", "drawable", "com.aeviou.front"));
		int bitmapw = xmlreader.getEnglishKeyBitmapWidth();
		int bitmaph = xmlreader.getEnglishKeyBitmapHeight();
		enkeyBitmap = Bitmap.createScaledBitmap(enkeyBitmap, bitmapw, bitmaph, true);
		int enkeyw = xmlreader.getEnglishKeyImageWidth();
		int enkeyh = xmlreader.getEnglishKeyImageHeight();
		Bitmap enkeyMiscsBitmap = BitmapFactory.decodeResource(res, res.getIdentifier("enkeymiscs", "drawable", "com.aeviou.front"));
		int miscbitmapw = xmlreader.getEnglishKeyMiscsBitmapWidth();
		int miscbitmaph  = xmlreader.getEnglishKeyMiscsBitmapHeight();
		enkeyMiscsBitmap = Bitmap.createScaledBitmap(enkeyMiscsBitmap, miscbitmapw, miscbitmaph, true);
		/*
		 * load english key bitmaps
		 * */
		NodeList englishKeyBitmapList = xmlreader.getEnglishKeyBitmapNodeList();
		for(int i=0; i<englishKeyBitmapList.getLength(); i++){
			Element enkey = (Element)englishKeyBitmapList.item(i);
			String keyName = enkey.getAttribute("name");
			NodeList bitmapNodeList = enkey.getElementsByTagName("bitmap");
			for(int j=0; j<bitmapNodeList.getLength(); j++){
				Element bitmap = (Element)bitmapNodeList.item(j);
				String keyStatus = bitmap.getAttribute("status");
				String keyType = bitmap.getAttribute("type");
				int column = Integer.valueOf(bitmap.getAttribute("column"));
				int row = Integer.valueOf(bitmap.getAttribute("row"));
				
				matrix = new Matrix();
				matrix.postScale(((float)englishkeyWidth)/((float)enkeyw),((float)englishkeyHeight)/((float)enkeyh));
				Bitmap scaledBitmap = Bitmap.createBitmap(enkeyBitmap, enkeyw*column, enkeyh*row, enkeyw, enkeyh, matrix, true);
				englishKeyBitmaps.put(keyName+"_"+keyStatus+"_"+keyType, scaledBitmap);
				matrix = null;
			}
		}

		if (enkeyBitmap != null && !enkeyBitmap.isRecycled()) {
			enkeyBitmap.recycle();
			enkeyBitmap = null;
		}
		
		/*
		 * load english key miscs
		 * */
		NodeList englishMiscsBitmapList = xmlreader.getEnglishMiscsBitmapNodeList();
		for(int i=0; i<englishMiscsBitmapList.getLength(); i++){
			Element enmisc = (Element)englishMiscsBitmapList.item(i);
			String miscName = enmisc.getAttribute("name");
			NodeList bitmapNodeList = enmisc.getElementsByTagName("bitmap");
			for(int j=0; j<bitmapNodeList.getLength(); j++){
				Element bitmap = (Element)bitmapNodeList.item(j);
				String miscStatus = bitmap.getAttribute("status");
				String miscType = bitmap.getAttribute("type");
				int misckeyw = Integer.valueOf(bitmap.getAttribute("width"));
				int misckeyh = Integer.valueOf(bitmap.getAttribute("height"));
				int misckeyl = Integer.valueOf(bitmap.getAttribute("left"));
				int misckeyt = Integer.valueOf(bitmap.getAttribute("top"));
				
				matrix = new Matrix();
				if(miscName.equals("space")){
					int tab = Integer.valueOf(bitmap.getAttribute("tab"));
					matrix.postScale((float)(englishkeyWidth*tab+englishkeyPadding*(tab-1))/misckeyw,(float)englishkeyHeight/misckeyh);
				}else{
					matrix.postScale(((float)(englishkeyWidth*1.5f))/misckeyw,((float)englishkeyHeight)/misckeyh);
				}
				Bitmap scaledBitmap = Bitmap.createBitmap(enkeyMiscsBitmap, misckeyl, misckeyt, misckeyw, misckeyh, matrix, true);
				englishKeyBitmaps.put(miscName+"_"+miscStatus+"_"+miscType, scaledBitmap);
				matrix = null;
			}
		}
		
		if (enkeyMiscsBitmap != null && !enkeyMiscsBitmap.isRecycled()) {
			enkeyMiscsBitmap.recycle();
			enkeyMiscsBitmap = null;
		}
	}
	
	public void initializeSymbolKeyBitmap(int symbolkeywidth, int symbolkeyheight, int symbolkeypadding, Resources resources) {
		//initialize variables
		symbolkeyWidth = symbolkeywidth;
		symbolkeyHeight = symbolkeyheight;
		symbolkeyPadding = symbolkeypadding;
		
		res = resources;
		XMLReader xmlreader = XMLReader.getXMLReaderInstance();
		Bitmap symkeyBitmap = BitmapFactory.decodeResource(res, res.getIdentifier("symkeys", "drawable", "com.aeviou.front"));
		int bitmapw = xmlreader.getSymbolKeyBitmapWidth();
		int bitmaph = xmlreader.getSymbolKeyBitmapHeight();
		symkeyBitmap = Bitmap.createScaledBitmap(symkeyBitmap, bitmapw, bitmaph, true);
		int symkeyw = xmlreader.getSymbolKeyImageWidth();
		int symkeyh = xmlreader.getSymbolKeyImageHeight();
		Bitmap symkeyMiscsBitmap = BitmapFactory.decodeResource(res, res.getIdentifier("symkeymiscs", "drawable", "com.aeviou.front"));
		int miscbitmapw = xmlreader.getSymbolKeyMiscsBitmapWidth();
		int miscbitmaph  = xmlreader.getSymbolKeyMiscsBitmapHeight();
		symkeyMiscsBitmap = Bitmap.createScaledBitmap(symkeyMiscsBitmap, miscbitmapw, miscbitmaph, true);
		/*
		 * load symbol keys
		 * */
		NodeList symbolKeyBitmapList = xmlreader.getSymbolKeyBitmapNodeList();
		for(int i=0; i<symbolKeyBitmapList.getLength(); i++){
			Element symkey = (Element)symbolKeyBitmapList.item(i);
			String keyName = symkey.getAttribute("name");
			NodeList bitmapNodeList = symkey.getElementsByTagName("bitmap");
			for(int j=0; j<bitmapNodeList.getLength(); j++){
				Element bitmap = (Element)bitmapNodeList.item(j);
				String keyStatus = bitmap.getAttribute("status");
				String keyPage = bitmap.getAttribute("page");
				int column = Integer.valueOf(bitmap.getAttribute("column"));
				int row = Integer.valueOf(bitmap.getAttribute("row"));
				
				Bitmap scaledBitmap = null;
				matrix = new Matrix();
				matrix.postScale(((float)symbolkeyWidth)/((float)symkeyw),((float)symbolkeyHeight)/((float)symkeyh));
				if(keyName.equals("27") && keyPage.equals("2")){
					int tab = 2;
					//matrix.postScale((float)(symbolkeyWidth*tab+symbolkeyPadding*(tab-1))/symkeyw,(float)symbolkeyHeight/symkeyh);
					scaledBitmap = Bitmap.createBitmap(symkeyBitmap, symkeyw*column, symkeyh*row, symkeyw*tab, symkeyh, matrix, true);
				}else if(keyName.equals("28") && keyPage.equals("2")){
					keyPage="0";
				}else{
					scaledBitmap = Bitmap.createBitmap(symkeyBitmap, symkeyw*column, symkeyh*row, symkeyw, symkeyh, matrix, true);
				}
				symbolKeyBitmaps.put(keyName+"_"+keyStatus+"_p"+keyPage, scaledBitmap);
				//Log.d("BitmapManager", keyName+"_"+keyStatus+"_"+keyPage);
				matrix = null;
			}
		}
		
		if (symkeyBitmap != null && !symkeyBitmap.isRecycled()) {
			symkeyBitmap.recycle();
			symkeyBitmap = null;
		}
		
		/*
		 * load symbol key miscs
		 * */
		NodeList symbolMiscsBitmapList = xmlreader.getSymbolMiscsBitmapNodeList();
		for(int i=0; i<symbolMiscsBitmapList.getLength(); i++){
			Element symmisc = (Element)symbolMiscsBitmapList.item(i);
			String miscName = symmisc.getAttribute("name");
			NodeList bitmapNodeList = symmisc.getElementsByTagName("bitmap");
			for(int j=0; j<bitmapNodeList.getLength(); j++){
				Element bitmap = (Element)bitmapNodeList.item(j);
				String miscStatus = bitmap.getAttribute("status");
				String miscType = bitmap.getAttribute("type");
				int misckeyw = Integer.valueOf(bitmap.getAttribute("width"));
				int misckeyh = Integer.valueOf(bitmap.getAttribute("height"));
				int misckeyl = Integer.valueOf(bitmap.getAttribute("left"));
				int misckeyt = Integer.valueOf(bitmap.getAttribute("top"));
				
				matrix = new Matrix();
				if(miscName.equals("space")){
					int tab = Integer.valueOf(bitmap.getAttribute("tab"));
					matrix.postScale((float)(symbolkeyWidth*tab+symbolkeyPadding*(tab-1))/misckeyw,(float)symbolkeyHeight/misckeyh);
				}else{
					matrix.postScale(((float)(symbolkeyWidth*1.5f))/misckeyw,((float)symbolkeyHeight)/misckeyh);
				}
				if(miscName.equals("page")){
					miscType = "p"+bitmap.getAttribute("page");
				}
				Bitmap scaledBitmap = Bitmap.createBitmap(symkeyMiscsBitmap, misckeyl, misckeyt, misckeyw, misckeyh, matrix, true);
				symbolKeyBitmaps.put(miscName+"_"+miscStatus+"_"+miscType, scaledBitmap);
				matrix = null;
			}
		}
		
		if (symkeyMiscsBitmap != null && !symkeyMiscsBitmap.isRecycled()) {
			symkeyMiscsBitmap.recycle();
			symkeyMiscsBitmap = null;
			Log.d("BitmapManager", "recycle syskeymiscsBitmap");
		}
		
		/*
		for(int i=0; i<symbolKeyBitmapList.getLength(); i++){
			Element key = (Element)symbolKeyBitmapList.item(i);
			String keyName = key.getAttribute("name");
			NodeList bitmapNodeList = key.getElementsByTagName("bitmap");
			for(int j=0; j<bitmapNodeList.getLength(); j++){
				Element bitmap = (Element)bitmapNodeList.item(j);
				String keyContent = bitmap.getTextContent();
				int id = res.getIdentifier(keyContent, "drawable", "com.aeviou.front");//
				if(id!=0){
					Bitmap scaledBitmap = null;
					Bitmap tempBitmap = BitmapFactory.decodeResource(res, id);
					if(matrix == null){
						matrix = new Matrix();
						matrix.postScale((float)symbolkeyWidth/tempBitmap.getWidth(),(float)symbolkeyHeight/tempBitmap.getHeight());
					}
					if(keyName.equals("space")){
						int tab = Integer.parseInt(bitmap.getAttribute("tab"));
						matrix = new Matrix();
						matrix.postScale((float)(symbolkeyWidth*tab+symbolkeyPadding*(tab-1))/tempBitmap.getWidth(),(float)symbolkeyHeight/tempBitmap.getHeight());
						scaledBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix, true);
						matrix = null;
					} else if(keyName.equals("27") && bitmap.getAttribute("page").equals("2")){
						int tab = 2;
						matrix = new Matrix();
						matrix.postScale((float)(symbolkeyWidth*tab+symbolkeyPadding*(tab-1))/tempBitmap.getWidth(),(float)symbolkeyHeight/tempBitmap.getHeight());
						scaledBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix, true);
						matrix = null;
					} else{
						scaledBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix, true);//
					}
					
					symbolKeyBitmaps.put(keyContent, scaledBitmap);
					//tempBitmap = null;
				}
			}
		}
		matrix = null;*/
	}
	
	public void drawHexKeyBitmap(float leftTopX, float leftTopY, String keyName, int status, Canvas cvs){
		String mapKey = keyName + "_" + Constants.statusStr[status];
		Bitmap bitmap = hexKeybitmaps.get(mapKey);
		if(bitmap != null){
			cvs.drawBitmap(hexKeybitmaps.get(mapKey), leftTopX, leftTopY, null);
		}
	}
	
	public void drawEnglishKeyBitmap(float leftTopX, float leftTopY, String keyName, int status, String type, int id, Canvas cvs){
		String mapKey = keyName + "_" + Constants.statusStr[status] + "_" + type;
		Bitmap bitmap = englishKeyBitmaps.get(mapKey);
		if(bitmap != null){
			cvs.drawBitmap(englishKeyBitmaps.get(mapKey), leftTopX, leftTopY, null);
		}
	}
	
	public void drawSymbolKeyBitmap(float leftTopX, float leftTopY, String keyName, int status, String type, String page, Canvas cvs){
		String mapKey = "";
		if (type.equals("text") || type.equals("pair")) {
			mapKey = keyName + "_" + Constants.statusStr[status] + "_" + page;
			//Log.d("BitmapManager", "Draw: "+mapKey);
		}
		else {
			if (keyName.equals("page"))
				mapKey = keyName + "_" + Constants.statusStr[status] + "_" + page;
			else
				mapKey = keyName + "_" + Constants.statusStr[status] + "_" + type;
		}
		
		Bitmap bitmap = symbolKeyBitmaps.get(mapKey);
		if(bitmap != null){
			cvs.drawBitmap(bitmap, leftTopX, leftTopY, null);
		}
	}
	
	public void getEnKeyBitmap(){
	
	}
	
	public void getSymbolKeyBitmap(){
	
	}
	
	public void getNumKeyBitmap(){
		
	}
	
	public void getCandidateBarBitmap(){
		
	}
	
	public void adjustHexKeyBitmap(int width, int height){
		
	}
	
	public void deleteHexKeyBitmap(){
		hexKeybitmaps = new HashMap<String,Bitmap>();
	}
	
	public void deleteEnglishKeyBitmap(){
		englishKeyBitmaps = new HashMap<String,Bitmap>();
	}
	
	public void deleteSymbolKeyBitmap(){
		symbolKeyBitmaps = new HashMap<String,Bitmap>();
	}
}
