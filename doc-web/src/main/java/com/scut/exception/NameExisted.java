package com.scut.exception;

public class NameExisted extends RuntimeException{
	private static final long serialVersionUID = 8987128726277851392L;
	public NameExisted(String msg){
		super(msg);
	}
}
