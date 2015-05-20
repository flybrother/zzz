package com.aeviou.Util;

import com.aeviou.front.AeviouIMEService;
import com.aeviou.front.keyboardview.AeviouKeyboardView;

public class Globals {
	// general
	public static AeviouIMEService service;
	public static AeviouKeyboardView view;
	public static int screenwidth;
	public static int screenheight;

	// hexkeyboard related
	public static int hexkeywidth;
	public static int hexkeyheight;
	public static float hexkeypadding;
	public static int textsize;
	public static float candidateBarWidth;
	
	// users settings
	public static boolean isVibratorOn;
	public static boolean isTipsViewOn = true;
	public static boolean isSpeedViewOn = true;
	public static boolean ifReDrawSpeedView = false;
	
	public static String PINYIN_FILE_DIRECTORY;
}
