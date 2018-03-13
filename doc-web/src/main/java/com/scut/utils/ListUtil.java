package com.scut.utils;

import java.util.List;

public class ListUtil {

	private static int k = 6;
	/**
	 * 对相似度列表、名称列表进行排序，选择最大的六个
	 * @param doubleList
	 * @param strList
	 */
	public static void getMaxSix(List<Double> doubleList,List<String> strList) {
		if(doubleList.size()<k){
			sort(doubleList,strList,doubleList.size());
			return;
		}
		quickSort(doubleList,strList,0,doubleList.size()-1);
		sort(doubleList,strList,k);
	}
	private static void quickSort(List<Double> doubleList,List<String> strList,int start,int end){
		if(start==end)	return;
		double flag = doubleList.get(start);
		String flagStr = strList.get(start);
		int i=start;
		int j=end;
		while(i<j){
			while(i<j&&flag>doubleList.get(j)){
				j--;
			}
			if(i<j){
				doubleList.set(i, doubleList.get(j));
				strList.set(i, strList.get(j));
				i++;
			}
			while(i<j&&flag<=doubleList.get(i)){
				i++;
			}
			if(i<j){
				doubleList.set(j, doubleList.get(i));
				strList.set(j, strList.get(i));
				j--;
			}
		}
		doubleList.set(i, flag);
		strList.set(i, flagStr);
		if(i>k-1){
			quickSort(doubleList,strList,start,i-1);
		}else if(i<k-1){
			quickSort(doubleList,strList,i+1,end);
		}else{
			return;
		}
	}
	
	private static void sort(List<Double> doubleList,List<String> strList,int len){
		for(int i=0;i<len;i++){
			for(int j=1;j<len-i;j++){
				if(doubleList.get(j)>doubleList.get(j-1)){
					double tmp = doubleList.get(j-1);
					doubleList.set(j-1, doubleList.get(j));
					doubleList.set(j, tmp);
					String tmpStr = strList.get(j-1);
					strList.set(j-1, strList.get(j));
					strList.set(j, tmpStr);
				}
			}
		}
	}
}
