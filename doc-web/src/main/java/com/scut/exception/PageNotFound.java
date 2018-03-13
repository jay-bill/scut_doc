package com.scut.exception;
/**
 * 找不到资源
 * @author jaybill
 *
 */
public class PageNotFound extends RuntimeException {
	
	private static final long serialVersionUID = -1134378794727393216L;

	public PageNotFound(String msg){
		super(msg);
	}
}
