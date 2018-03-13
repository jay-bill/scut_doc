package com.scut.utils.excel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.scut.pojo.User;
import com.scut.utils.Consts;
import com.scut.utils.ProcessUtil;

public class ExcelParser {
	
	private static final int DEFAULT_COL_SUM=3;//默认为3列
	protected Workbook wb;
	/** 
     * 读取Excel表格表头的内容  
     * @param InputStream 
     * @return String 表头内容的数组 
     */  
    public String[] parseExcelTitle() throws Exception{  
        if(wb==null){  
            throw new Exception("Workbook对象为空！");  
        }  
        Sheet sheet = wb.getSheetAt(0);  
        Row row = sheet.getRow(0);  
        // 标题总列数  
        int colNum = row.getPhysicalNumberOfCells();  
        System.out.println("colNum:" + colNum);  
        String[] title = new String[colNum];  
        for (int i = 0; i < colNum; i++) {  
            // title[i] = getStringCellValue(row.getCell((short) i));  
            title[i] = row.getCell(i).getCellFormula();  
        }  
        return title;  
    }
    
    /** 
     * 读取Excel数据内容   
     *
     */  
    public List<User> parseExcelContent(HttpSession session) throws Exception{  
        if(wb==null){  
            throw new Exception("Workbook对象为空！");  
        }  
        List<User> users = new ArrayList<User>();         
        Sheet sheet = wb.getSheetAt(0);  
        // 得到总行数  
        int rowNum = sheet.getLastRowNum();  
        Row row = sheet.getRow(0);  
        int colNum = row.getPhysicalNumberOfCells();  
        // 正文内容应该从第二行开始,第一行为表头的标题  
        for (int i = 1; i <= rowNum; i++) {  
            row = sheet.getRow(i);   
            String account = getCellFormatValue(row.getCell(0));
            String name = getCellFormatValue(row.getCell(1));
            String unit = getCellFormatValue(row.getCell(2));
            User user = new User(account,name,unit);
            users.add(user);
            //修改完成进度
            ProcessUtil putl = new ProcessUtil();
            putl.modifyProcess(session, Consts.EXCEL_USERS_PROC, rowNum*2);
        }  
        return users;  
    } 
    
    /**   
     * 根据Cell类型设置数据 
     * @param cell 
     * @return 
     */  
    private String getCellFormatValue(Cell cell) {  
    	String cellvalue = "";  
        if (cell != null) {  
            // 判断当前Cell的Type  
            switch (cell.getCellType()) {  
            	case Cell.CELL_TYPE_NUMERIC:// 如果当前Cell的Type为NUMERIC  
            	case Cell.CELL_TYPE_FORMULA: {  
            		// 判断当前的cell是否为Date  
            		if (DateUtil.isCellDateFormatted(cell)) {  
	                    // 如果是Date类型则，转化为Data格式  
	                    // data格式是带时分秒的：2013-7-10 0:00:00  
	                    // cellvalue = cell.getDateCellValue().toLocaleString();  
	                    // data格式是不带带时分秒的：2013-7-10  
	                    Date date = cell.getDateCellValue();  
	                    cellvalue = date.toString();  
            		} else {// 如果是纯数字  
	                    // 取得当前Cell的数值  
	                    cellvalue = String.valueOf(cell.getNumericCellValue());  
            		}  
            		break;  
            	}  
            	case Cell.CELL_TYPE_STRING:// 如果当前Cell的Type为STRING  
            		// 取得当前的Cell字符串  
            		cellvalue = cell.getRichStringCellValue().getString();  
            		break;  
            	default:// 默认的Cell值  
            		cellvalue = "";  
            }  
        }
        return cellvalue;  
    }  
}
