package com.aeviou.Util;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import android.content.res.Resources;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLReader {	
	private static XMLReader instance;
	private InputStream bitmapStream;
	private InputStream hexkeyLayoutStream;
	private InputStream englishkeyLayoutStream;
	private InputStream symbolkeyLayoutStream;
	private InputStream presetsStream;
	private DocumentBuilderFactory dbFactory;
	private DocumentBuilder dbBuilder;
	private Document bitmapDoc;
	private Document hexkeyLayoutDoc;
	private Document englishkeyLayoutDoc;
	private Document symbolkeyLayoutDoc;
	private Document presetsDoc;
	
	public void initialize(Resources R){
		try{
			dbFactory = DocumentBuilderFactory.newInstance();
			dbBuilder = dbFactory.newDocumentBuilder();
			bitmapStream = R.getAssets().open("bitmaps.xml");
			bitmapDoc = dbBuilder.parse(bitmapStream);
			hexkeyLayoutStream = R.getAssets().open("hexkey_layout.xml");
			hexkeyLayoutDoc = dbBuilder.parse(hexkeyLayoutStream);
			englishkeyLayoutStream = R.getAssets().open("englishkey_layout.xml");
			englishkeyLayoutDoc = dbBuilder.parse(englishkeyLayoutStream);
			symbolkeyLayoutStream = R.getAssets().open("symbolkey_layout.xml");
			symbolkeyLayoutDoc = dbBuilder.parse(symbolkeyLayoutStream);
			presetsStream = R.getAssets().open("presets.xml");
			presetsDoc = dbBuilder.parse(presetsStream);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static XMLReader getXMLReaderInstance(){
		return instance = (instance == null ? new XMLReader() : instance);
	}
	
	public NodeList getHexKeysNodeList(){
		return hexkeyLayoutDoc.getElementsByTagName("row");
	}
	
	public NodeList getEnglishKeysNodeList(){
		return englishkeyLayoutDoc.getElementsByTagName("row");
	}
	
	public NodeList getSymbolKeysNodeList(){
		return symbolkeyLayoutDoc.getElementsByTagName("row");
	}
	
	public NodeList getHexKeyBitmapNodeList(){
		NodeList hexkeysNodeList = bitmapDoc.getElementsByTagName("hexkeys");
		Element hexkeys = (Element)hexkeysNodeList.item(0);
		return hexkeys.getElementsByTagName("hexkey");
	}
	
	public NodeList getHexKeyMiscsBitmapNodeList(){
		NodeList hexkeyMiscsNodeList = bitmapDoc.getElementsByTagName("hexkeymiscs");
		Element hexkeyMiscs = (Element)hexkeyMiscsNodeList.item(0);
		return hexkeyMiscs.getElementsByTagName("misc");
	}
	
	public NodeList getEnglishKeyBitmapNodeList(){
		NodeList englishkeysNodeList = bitmapDoc.getElementsByTagName("englishkeys");
		Element englishkeys = (Element)englishkeysNodeList.item(0);
		return englishkeys.getElementsByTagName("englishkey");
	}
	
	public NodeList getEnglishMiscsBitmapNodeList(){
		NodeList englishkeysNodeList = bitmapDoc.getElementsByTagName("englishmiscs");
		Element englishkeys = (Element)englishkeysNodeList.item(0);
		return englishkeys.getElementsByTagName("englishmisc");
	}
	
	public NodeList getSymbolKeyBitmapNodeList(){
		NodeList symbolkeysNodeList = bitmapDoc.getElementsByTagName("symbolkeys");
		Element symbolkeys = (Element)symbolkeysNodeList.item(0);
		return symbolkeys.getElementsByTagName("symbolkey");
	}

	public NodeList getSymbolMiscsBitmapNodeList(){
		NodeList symbolkeysNodeList = bitmapDoc.getElementsByTagName("symbolmiscs");
		Element symbolkeys = (Element)symbolkeysNodeList.item(0);
		return symbolkeys.getElementsByTagName("symbolmisc");
	}
	
	public int getHexKeyNormalBitmapAmount(){
		int hexkeyNormalBitmapAmount;
		NodeList hexkeysNodeList = bitmapDoc.getElementsByTagName("hexkeys");
		Element hexkeys = (Element)hexkeysNodeList.item(0);
		NodeList info = hexkeys.getElementsByTagName("info");
		Element hexkeyNormalBitmapAmountInfo = (Element)info.item(0);
		hexkeyNormalBitmapAmount = Integer.valueOf(hexkeyNormalBitmapAmountInfo.getAttribute("normalBitmapAmount"));
		return hexkeyNormalBitmapAmount;
	}

	public int getEnglishKeyNormalBitmapAmount(){
		int englishkeyNormalBitmapAmount;
		NodeList englishkeysNodeList = bitmapDoc.getElementsByTagName("englishkeys");
		Element englishkeys = (Element)englishkeysNodeList.item(0);
		NodeList info = englishkeys.getElementsByTagName("info");
		Element hexkeyNormalBitmapAmountInfo = (Element)info.item(0);
		englishkeyNormalBitmapAmount = Integer.valueOf(hexkeyNormalBitmapAmountInfo.getAttribute("normalBitmapAmount"));
		return englishkeyNormalBitmapAmount;
	}
	
	public int getHexKeySelectedBitmapAmount(){
		int hexkeySelectedBitmapAmount;
		NodeList hexkeysNodeList = bitmapDoc.getElementsByTagName("hexkeys");
		Element hexkeys = (Element)hexkeysNodeList.item(0);
		NodeList info = hexkeys.getElementsByTagName("info");
		Element hexkeySelectedBitmapAmountInfo = (Element)info.item(0);
		hexkeySelectedBitmapAmount = Integer.valueOf(hexkeySelectedBitmapAmountInfo.getAttribute("selectedBitmapAmount"));
		return hexkeySelectedBitmapAmount;
	}

	public int getEnglishKeySelectedBitmapAmount(){
		int englishkeySelectedBitmapAmount;
		NodeList englishkeysNodeList = bitmapDoc.getElementsByTagName("englishkeys");
		Element englishkeys = (Element)englishkeysNodeList.item(0);
		NodeList info = englishkeys.getElementsByTagName("info");
		Element hexkeySelectedBitmapAmountInfo = (Element)info.item(0);
		englishkeySelectedBitmapAmount = Integer.valueOf(hexkeySelectedBitmapAmountInfo.getAttribute("selectedBitmapAmount"));
		return englishkeySelectedBitmapAmount;
	}
	
	public float getHexKeyEdgeHeightScale(){
		float scale;
		NodeList hexkeysNodeList = bitmapDoc.getElementsByTagName("hexkeys");
		Element hexkeys = (Element)hexkeysNodeList.item(0);
		NodeList info = hexkeys.getElementsByTagName("info");
		Element infoElement = (Element)info.item(0);
		scale = Float.valueOf(infoElement.getAttribute("edgeHeightScale"));
		return scale;
	}

	public float getEnglishKeyEdgeHeightScale(){
		float scale;
		NodeList englishkeysNodeList = bitmapDoc.getElementsByTagName("englishkeys");
		Element englishkeys = (Element)englishkeysNodeList.item(0);
		NodeList info = englishkeys.getElementsByTagName("info");
		Element infoElement = (Element)info.item(0);
		scale = Float.valueOf(infoElement.getAttribute("edgeHeightScale"));
		return scale;
	}
	
	/**
	 * @return hex key scale (y/x)
	 */
	public float getHexKeyScale(){
		float x,y;
		NodeList preset = presetsDoc.getElementsByTagName("hexkeyScale");
		Element hexkeyScale = (Element)preset.item(0);
		x = Float.valueOf(hexkeyScale.getAttribute("x"));
		y = Float.valueOf(hexkeyScale.getAttribute("y"));
		return y/x;
	}
	
	/**
	 * @return the actual width of the bitmap
	 */
	public int getHexKeyBitmapWidth(){
		NodeList hexkeysNodeList = bitmapDoc.getElementsByTagName("hexkeys");
		Element hexkeys = (Element)hexkeysNodeList.item(0);
		return Integer.valueOf(hexkeys.getAttribute("bitmapwidth"));
	}
	
	/**
	 * @return the actual height of the bitmap
	 */
	public int getHexKeyBitmapHeight(){
		NodeList hexkeysNodeList = bitmapDoc.getElementsByTagName("hexkeys");
		Element hexkeys = (Element)hexkeysNodeList.item(0);
		return Integer.valueOf(hexkeys.getAttribute("bitmapheight"));
	}
	
	/**
	 * @return the actual hex key width in the bitmap
	 */
	public int getHexKeyImageWidth(){
		NodeList hexkeysNodeList = bitmapDoc.getElementsByTagName("hexkeys");
		Element hexkeys = (Element)hexkeysNodeList.item(0);
		return Integer.valueOf(hexkeys.getAttribute("hexkeywidth"));
	}
	
	/**
	 * @return the actual hex key height in the bitmap
	 */
	public int getHexKeyImageHeight(){
		NodeList hexkeysNodeList = bitmapDoc.getElementsByTagName("hexkeys");
		Element hexkeys = (Element)hexkeysNodeList.item(0);
		return Integer.valueOf(hexkeys.getAttribute("hexkeyheight"));
	}
	
	/**
	 * @return the actual hex key misc bitmap width
	 */
	public int getHexKeyMiscsBitmapWidth(){
		NodeList hexkeysNodeList = bitmapDoc.getElementsByTagName("hexkeymiscs");
		Element hexkeys = (Element)hexkeysNodeList.item(0);
		return Integer.valueOf(hexkeys.getAttribute("bitmapwidth"));
	}
	
	/**
	 * @return the actual hex key misc bitmap height
	 */
	public int getHexKeyMiscsBitmapHeight(){
		NodeList hexkeysNodeList = bitmapDoc.getElementsByTagName("hexkeymiscs");
		Element hexkeys = (Element)hexkeysNodeList.item(0);
		return Integer.valueOf(hexkeys.getAttribute("bitmapheight"));
	}
	
	public float getEnglishKeyScale(){
		float x,y;
		NodeList preset = presetsDoc.getElementsByTagName("englishkeyScale");
		Element englishkeyScale = (Element)preset.item(0);
		x = Float.valueOf(englishkeyScale.getAttribute("x"));
		y = Float.valueOf(englishkeyScale.getAttribute("y"));
		return y/x;
	}
	
	/**
	 * @return the actual width of the bitmap
	 */
	public int getEnglishKeyBitmapWidth(){
		NodeList hexkeysNodeList = bitmapDoc.getElementsByTagName("englishkeys");
		Element hexkeys = (Element)hexkeysNodeList.item(0);
		return Integer.valueOf(hexkeys.getAttribute("bitmapwidth"));
	}
	
	/**
	 * @return the actual height of the bitmap
	 */
	public int getEnglishKeyBitmapHeight(){
		NodeList hexkeysNodeList = bitmapDoc.getElementsByTagName("englishkeys");
		Element hexkeys = (Element)hexkeysNodeList.item(0);
		return Integer.valueOf(hexkeys.getAttribute("bitmapheight"));
	}

	/**
	 * @return the actual English key width in the bitmap
	 */
	public int getEnglishKeyImageWidth(){
		NodeList englishkeysNodeList = bitmapDoc.getElementsByTagName("englishkeys");
		Element englishkeys = (Element)englishkeysNodeList.item(0);
		return Integer.valueOf(englishkeys.getAttribute("enkeywidth"));
	}
	
	/**
	 * @return the actual English key height in the bitmap
	 */
	public int getEnglishKeyImageHeight(){
		NodeList englishkeysNodeList = bitmapDoc.getElementsByTagName("englishkeys");
		Element englishkeys = (Element)englishkeysNodeList.item(0);
		return Integer.valueOf(englishkeys.getAttribute("enkeyheight"));
	}
	
	/**
	 * @return the actual English key misc bitmap width
	 */
	public int getEnglishKeyMiscsBitmapWidth(){
		NodeList englishMiscsNodeList = bitmapDoc.getElementsByTagName("englishmiscs");
		Element englishmiscs = (Element)englishMiscsNodeList.item(0);
		return Integer.valueOf(englishmiscs.getAttribute("bitmapwidth"));
	}
	
	/**
	 * @return the actual English key misc bitmap height
	 */
	public int getEnglishKeyMiscsBitmapHeight(){
		NodeList englishMiscsNodeList = bitmapDoc.getElementsByTagName("englishmiscs");
		Element englishmiscs = (Element)englishMiscsNodeList.item(0);
		return Integer.valueOf(englishmiscs.getAttribute("bitmapheight"));
	}
	
	/**
	 * @return the actual width of the bitmap
	 */
	public int getSymbolKeyBitmapWidth(){
		NodeList hexkeysNodeList = bitmapDoc.getElementsByTagName("englishkeys");
		Element hexkeys = (Element)hexkeysNodeList.item(0);
		return Integer.valueOf(hexkeys.getAttribute("bitmapwidth"));
	}
	
	/**
	 * @return the actual height of the bitmap
	 */
	public int getSymbolKeyBitmapHeight(){
		NodeList hexkeysNodeList = bitmapDoc.getElementsByTagName("englishkeys");
		Element hexkeys = (Element)hexkeysNodeList.item(0);
		return Integer.valueOf(hexkeys.getAttribute("bitmapheight"));
	}

	/**
	 * @return the actual symbol key width in the bitmap
	 */
	public int getSymbolKeyImageWidth(){
		NodeList englishkeysNodeList = bitmapDoc.getElementsByTagName("englishkeys");
		Element englishkeys = (Element)englishkeysNodeList.item(0);
		return Integer.valueOf(englishkeys.getAttribute("enkeywidth"));
	}
	
	/**
	 * @return the actual symbol key height in the bitmap
	 */
	public int getSymbolKeyImageHeight(){
		NodeList englishkeysNodeList = bitmapDoc.getElementsByTagName("englishkeys");
		Element englishkeys = (Element)englishkeysNodeList.item(0);
		return Integer.valueOf(englishkeys.getAttribute("enkeyheight"));
	}
	
	/**
	 * @return the actual symbol key misc bitmap width
	 */
	public int getSymbolKeyMiscsBitmapWidth(){
		NodeList englishMiscsNodeList = bitmapDoc.getElementsByTagName("englishmiscs");
		Element englishmiscs = (Element)englishMiscsNodeList.item(0);
		return Integer.valueOf(englishmiscs.getAttribute("bitmapwidth"));
	}
	
	/**
	 * @return the actual symbol key misc bitmap height
	 */
	public int getSymbolKeyMiscsBitmapHeight(){
		NodeList englishMiscsNodeList = bitmapDoc.getElementsByTagName("englishmiscs");
		Element englishmiscs = (Element)englishMiscsNodeList.item(0);
		return Integer.valueOf(englishmiscs.getAttribute("bitmapheight"));
	}
	
	public float getSymbolKeyScale() {
		float x,y;
		NodeList preset = presetsDoc.getElementsByTagName("symbolkeyScale");
		Element symbolkeyScale = (Element)preset.item(0);
		x = Float.valueOf(symbolkeyScale.getAttribute("x"));
		y = Float.valueOf(symbolkeyScale.getAttribute("y"));
		return y/x;
	}
	
	/**
	 * @return hex key padding (padding/hexkeyWidth)
	 */
	public float getHexKeyPadding(){
		float width,padding;
		NodeList preset = presetsDoc.getElementsByTagName("hexkeyPadding");
		Element hexkeyPadding = (Element)preset.item(0);
		width = Float.valueOf(hexkeyPadding.getAttribute("width"));
		padding = Float.valueOf(hexkeyPadding.getAttribute("padding"));
		return padding/width;
	}
	
	public float getEnglishKeyPadding(){
		float width,padding;
		NodeList preset = presetsDoc.getElementsByTagName("englishkeyPadding");
		Element englishkeyPadding = (Element)preset.item(0);
		width = Float.valueOf(englishkeyPadding.getAttribute("width"));
		padding = Float.valueOf(englishkeyPadding.getAttribute("padding"));
		return padding/width;
	}
	
	public float getSymbolKeyPadding() {
		float width,padding;
		NodeList preset = presetsDoc.getElementsByTagName("englishkeyPadding");
		Element symbolkeyPadding = (Element)preset.item(0);
		width = Float.valueOf(symbolkeyPadding.getAttribute("width"));
		padding = Float.valueOf(symbolkeyPadding.getAttribute("padding"));
		return padding/width;
	}
	
	/**
	 * @return returns the amount of hex keys in the hexkeyboard
	 */
	public int getHexKeyAmount(){
		int hexkeyAmount = 0;
		NodeList preset = hexkeyLayoutDoc.getElementsByTagName("layout");
		Element amount = (Element)preset.item(0);
		hexkeyAmount = Integer.valueOf(amount.getAttribute("keyamount"));
		return hexkeyAmount;
	}
	
	public int getHexKeyBigRow(){
		int bigrow = 0;
		NodeList preset = hexkeyLayoutDoc.getElementsByTagName("layout");
		Element hexkeyBigRow = (Element)preset.item(0);
		bigrow = Integer.valueOf(hexkeyBigRow.getAttribute("bigrow"));
		return bigrow;
	}
	
	public int getHexKeyRows(){
		int rows = 0;
		NodeList preset = hexkeyLayoutDoc.getElementsByTagName("layout");
		Element hexkeyBigRow = (Element)preset.item(0);
		rows = Integer.valueOf(hexkeyBigRow.getAttribute("rows"));
		return rows;
	}
	
	public int getHexKeyWidth(){
		int width = 0;
		NodeList preset = hexkeyLayoutDoc.getElementsByTagName("layout");
		Element hexkeyWidth = (Element)preset.item(0);
		width = Integer.valueOf(hexkeyWidth.getAttribute("keywidth"));
		return width;
	}
	
	public int getHexKeyHeight(){
		int height = 0;
		NodeList preset = hexkeyLayoutDoc.getElementsByTagName("layout");
		Element hexkeyHeight = (Element)preset.item(0);
		height = Integer.valueOf(hexkeyHeight.getAttribute("keyheight"));
		return height;
	}
	
	/**
	 * @return returns the amount of hex keys in the englishkeyboard
	 */
	public int getEnglishKeyAmount(){
		int englishkeyAmount = 0;
		NodeList preset = englishkeyLayoutDoc.getElementsByTagName("layout");
		Element amount = (Element)preset.item(0);
		englishkeyAmount = Integer.valueOf(amount.getAttribute("keyamount"));
		return englishkeyAmount;
	}
	
	public int getEnglishKeyBigRow(){
		int bigrow = 0;
		NodeList preset = englishkeyLayoutDoc.getElementsByTagName("layout");
		Element englishkeyBigRow = (Element)preset.item(0);
		bigrow = Integer.valueOf(englishkeyBigRow.getAttribute("bigrow"));
		return bigrow;
	}
	
	public int getEnglishKeyRows(){
		int rows = 0;
		NodeList preset = englishkeyLayoutDoc.getElementsByTagName("layout");
		Element englishkeyBigRow = (Element)preset.item(0);
		rows = Integer.valueOf(englishkeyBigRow.getAttribute("rows"));
		return rows;
	}
	
	public int getEnglishKeyWidth(){
		int width = 0;
		NodeList preset = englishkeyLayoutDoc.getElementsByTagName("layout");
		Element englishkeyWidth = (Element)preset.item(0);
		width = Integer.valueOf(englishkeyWidth.getAttribute("keywidth"));
		return width;
	}
	
	public int getEnglishKeyHeight(){
		int height = 0;
		NodeList preset = englishkeyLayoutDoc.getElementsByTagName("layout");
		Element englishkeyHeight = (Element)preset.item(0);
		height = Integer.valueOf(englishkeyHeight.getAttribute("keyheight"));
		return height;
	}

	public int getSymbolKeyAmount(){
		int symbolkeyAmount = 0;
		NodeList preset = symbolkeyLayoutDoc.getElementsByTagName("layout");
		Element amount = (Element)preset.item(0);
		symbolkeyAmount = Integer.valueOf(amount.getAttribute("keyamount"));
		return symbolkeyAmount;
	}
	
	public int getSymbolKeyBigRow(){
		int bigrow = 0;
		NodeList preset = symbolkeyLayoutDoc.getElementsByTagName("layout");
		Element symbolkeyBigRow = (Element)preset.item(0);
		bigrow = Integer.valueOf(symbolkeyBigRow.getAttribute("bigrow"));
		return bigrow;
	}
	
	public int getSymbolMaxPage() {
		int maxPage = 0;
		NodeList preset = presetsDoc.getElementsByTagName("symbolPage");
		Element symbolPage = (Element)preset.item(0);
		maxPage = Integer.valueOf(symbolPage.getAttribute("maxPage"));
		return maxPage;
	}
}
