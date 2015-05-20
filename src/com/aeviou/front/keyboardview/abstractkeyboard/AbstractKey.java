package com.aeviou.front.keyboardview.abstractkeyboard;

import android.graphics.Canvas;

public abstract class AbstractKey {
	
	protected String name;
	protected int id;
	protected float leftTopX, leftTopY;
	protected float centerX, centerY;
	protected float width, height;
	
	protected String type;
	protected int row;
	protected int status;
	
	public AbstractKey(){
	}
	
	public abstract void draw(Canvas Cvs);
	
	public String getName(){
		return name;
	}
	
	public void setName(String Name){
		this.name = Name;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int ID){
		this.id = ID;
	}
	
	public float getLeftTopX() {
		return leftTopX;
	}

	public void setLeftTopX(float leftTopX) {
		this.leftTopX = leftTopX;
	}

	public float getLeftTopY() {
		return leftTopY;
	}

	public void setLeftTopY(float leftTopY) {
		this.leftTopY = leftTopY;
	}
	
	public float getCenterX() {
		return centerX;
	}

	public void setCenterX(float centerX) {
		this.centerX = centerX;
	}

	public float getCenterY() {
		return centerY;
	}

	public void setCenterY(float centerY) {
		this.centerY = centerY;
	}
	
	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public String getType(){
		return type;
	}

	public void setType(String Type){
		this.type = Type;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}
	
	public int getStatus(){
		return status;
	}
	
	public void setStatus(int Status){
		this.status = Status;
	}
}
