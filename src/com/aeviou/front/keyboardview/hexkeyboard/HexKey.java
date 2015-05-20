package com.aeviou.front.keyboardview.hexkeyboard;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Canvas;
import com.aeviou.Util.BitmapManager;
import com.aeviou.Util.Constants;
import com.aeviou.front.keyboardview.abstractkeyboard.AbstractKey;

public class HexKey extends AbstractKey{
	protected String dynamicName;
	protected List<HexKeyNeighbour> neighbours = new ArrayList<HexKeyNeighbour>();
	protected List<HexKeyDynamic> dynamicHexKeys = new ArrayList<HexKeyDynamic>();
	
	public HexKey(){
		this.name="";
		this.id=-1;
		this.status = Constants.KEY_STATUS_NORMAL;
		this.type = "hide";
		this.dynamicName = "";
	}
	public HexKey(String name, int id){
		this.name = name;
		this.id = id;
		this.status = Constants.KEY_STATUS_NORMAL;
		this.dynamicName = "";
	}

	@Override
	public void draw(Canvas Cvs) {
		// TODO Auto-generated method stub
		if(this.dynamicName.equals("")){
			if(type.equals("hide")){
				//do not draw
			}else{
				BitmapManager.getInstance().drawHexKeyBitmap(leftTopX, leftTopY, name, status, Cvs);
			}
		}else{
			BitmapManager.getInstance().drawHexKeyBitmap(leftTopX, leftTopY, dynamicName, status, Cvs);
		}
	}
	
	public String getDynamicName() {
		return dynamicName;
	}

	public void setDynamicName(String dynamicName) {
		this.dynamicName = dynamicName;
	}

	public List<HexKeyNeighbour> getNeighbours() {
		return neighbours;
	}
	
	public void setNeighbours(List<HexKeyNeighbour> neighbours){
		this.neighbours = neighbours;
	}
	
	public void addNeighbour(int neighbourID, String dynamicName, List<HexKeyDynamic> NGs){
		HexKeyNeighbour newNeighbour = new HexKeyNeighbour(neighbourID, dynamicName, NGs);
		neighbours.add(newNeighbour);
	}

	public List<HexKeyDynamic> getDynamicHexKeys() {
		return dynamicHexKeys;
	}
	
	public void setDynamicHexKeys(List<HexKeyDynamic> dynamicHexKeys){
		this.dynamicHexKeys = dynamicHexKeys;
	}

	public void addDynamicKey(int dynamicid, String dynamicname){
		HexKeyDynamic newDynamic = new HexKeyDynamic(dynamicid, dynamicname);
		dynamicHexKeys.add(newDynamic);
	}
}
