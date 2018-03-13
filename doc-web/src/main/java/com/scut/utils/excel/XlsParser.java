package com.scut.utils.excel;
import org.apache.poi.ss.usermodel.Workbook;

public class XlsParser extends ExcelParser{
	
    XlsParser(Workbook wb) {  
        this.wb = wb; 
    }
}
