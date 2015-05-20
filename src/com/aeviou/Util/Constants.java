package com.aeviou.Util;

public class Constants {
	// url
	public final static String addUserUrl = "http://aeviou.sinaapp.com/index.php/Index/addUserLog";
	public final static String updateSpeedUrl = "http://aeviou.sinaapp.com/index.php/Index/updateSpeed";
	public final static String getTopThreeUrl = "http://aeviou.sinaapp.com/index.php/Index/getTopThree";
	public final static String getRankUrl = "http://aeviou.sinaapp.com/index.php/Index/getRank";
	
	// dictionary file names
	public final static String PINYIN_ROOT_FILENAME = "proot.jpg";
	public final static String PINYIN_NODE_FILENAME = "pnode.jpg";
	public final static String PINYIN_HANZI_FILENAME = "phanzi.jpg";
	public final static String PINYIN_LIANXIANG_FILENAME = "plx.jpg";
	public final static String PINYIN_LIST_FILE = "pinyin_list";
	
	// key status
	public final static int KEY_STATUS_HIDE = 0;
	public final static int KEY_STATUS_NORMAL = 1;
	public final static int KEY_STATUS_VISIBLE = 2;
	public final static int KEY_STATUS_CHANGED = 3;
	public final static int KEY_STATUS_SELECTED = 4;
	public static final int KEY_STATUS_NEXT = 5;

	public static final String[] statusStr = {"hide", "normal", "visible", "changed", "selected", "next"};
}
