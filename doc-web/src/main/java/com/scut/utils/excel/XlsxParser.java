package com.scut.utils.excel;
import org.apache.poi.ss.usermodel.Workbook;

public class XlsxParser extends ExcelParser{
	
    XlsxParser(Workbook wb) {  
        this.wb = wb; 
    }
}
