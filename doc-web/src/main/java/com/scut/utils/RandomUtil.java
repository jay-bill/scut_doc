package com.scut.utils;

import java.util.Date;
import java.util.Random;

public class RandomUtil {
	/**
	 * 生成6位随机数
	 * @return
	 */
	public static String generateIdentify(){
		Random random = new Random(new Date().getTime());
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<6;i++){
			int flag = random.nextInt(10);
			sb.append(flag);
		}
		return sb.toString();
	}
}
