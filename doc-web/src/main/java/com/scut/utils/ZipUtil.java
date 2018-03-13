package com.scut.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	public static void zipFileWithTier(String srcFiles, String zipPath) {  
	    try {  
	        FileOutputStream zipFile = new FileOutputStream(zipPath);  
	        BufferedOutputStream buffer = new BufferedOutputStream(zipFile);  
	        ZipOutputStream out = new ZipOutputStream(buffer);  
	        zipFiles(srcFiles, out, "");  
	        out.close();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	}  
	
	
	public static void zipFiles(String filePath, ZipOutputStream out, String prefix)  
	        throws IOException {  
	    File file = new File(filePath);  
	    if (file.isDirectory()) {  
	        if (file.listFiles()==null||file.listFiles().length == 0) {  
		        ZipEntry zipEntry = new ZipEntry(prefix + file.getName() + File.separator);  
		        out.putNextEntry(zipEntry);  
		        out.closeEntry();  
	        } else {  
		        prefix += file.getName() + File.separator;  
		        for (File f : file.listFiles())  
		            zipFiles(f.getAbsolutePath(), out, prefix);  
	        }  
	    } else {  
	        FileInputStream in = new FileInputStream(file);  
	        ZipEntry zipEntry = new ZipEntry(prefix + file.getName());  
	        out.putNextEntry(zipEntry);  
	        byte[] buf = new byte[1024];  
	        int len;  
	        while ((len = in.read(buf)) > 0) {  
	        	out.write(buf, 0, len);  
	        }  
	        out.closeEntry();  
	        in.close();  
	    }  	  
	 } 
}
