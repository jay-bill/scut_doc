package com.scut.utils.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelFactory {
	
	public static ExcelParser getExcelInstance(String filepath){
		String ext = filepath.substring(filepath.lastIndexOf("."));  
		Workbook wb;
        try {  
            InputStream is = new FileInputStream(filepath);  
            if(".xls".equals(ext)){  
                wb = new HSSFWorkbook(is);
                return new XlsParser(wb);
            }else if(".xlsx".equals(ext)){
            	wb = new XSSFWorkbook(is);
            	return new XlsxParser(wb);
            }
        } catch (FileNotFoundException e) {
        	
        } catch (IOException e) {  
        	
        }
		return null;
	}
}
