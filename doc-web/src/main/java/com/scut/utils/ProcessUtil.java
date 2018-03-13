package com.scut.utils;

import javax.servlet.http.HttpSession;

/**
 * 修改进度
 * @author jaybill
 *
 */
public class ProcessUtil {
	/**
	 * 修改进度
	 * @param session
	 * @param key
	 * @param size
	 */
	public void modifyProcess(HttpSession session,String key,int size){
		double proc = (Double)session.getAttribute(key);
		proc = proc+1.0/size;
		session.setAttribute(key, proc);
	}
}
