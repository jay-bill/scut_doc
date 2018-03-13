package com.scut.controller;

import java.text.NumberFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scut.utils.Consts;

/**
 * 获取完成进度的Controller
 * @author jaybill
 */
@Controller
@RequestMapping("process")
public class ProcessController {
	//转换成百分数
	private String doubleToBai(double proc){
		//转换成百分数
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMaximumFractionDigits(1);//这个1的意识是保存结果到小数点后几位，但是特别声明：这个结果已经先＊100了。
		String res = nf.format(proc);
		return res;
	}
	
	/**
	 * 获取从excel文件注册用户的进度
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("excelUsers")
	@ResponseBody
	public String[] getExcelUsersProcess(HttpServletRequest request,HttpServletResponse response){
		Object obj = request.getSession().getAttribute(Consts.EXCEL_USERS_PROC);
		double proc = 0.0;
		if(obj!=null){
			proc = (Double)obj;
		}
		if(proc>1.0){
			proc=1.0;
		}
		return new String[]{doubleToBai(proc)};//转换成百分数
	}
	
	
}
