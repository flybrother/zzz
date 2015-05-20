package com.aeviou.front.keyboardview.hexkeyboard;

import java.util.List;

public class HexKeyNeighbour {
	public int hexKeyId;
	public String dynamicName;
	public List<HexKeyDynamic> NGs;
	
	HexKeyNeighbour(int hexkeyid, String dynamicname, List<HexKeyDynamic> ngs){
		this.hexKeyId = hexkeyid;
		this.dynamicName = dynamicname;
		this.NGs = ngs;
	}
}
