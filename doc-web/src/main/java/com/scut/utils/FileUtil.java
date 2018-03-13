package com.scut.utils;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public class FileUtil {
	
	private static final String DEFAULT_PATH="/home/jbli/project/tmp/";
	
	public String transfer(MultipartFile file){
		String dest = DEFAULT_PATH+file.getOriginalFilename();
		File newFile = new File(dest);
		try {
			file.transferTo(newFile);			
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dest;
	}
	
	@Deprecated
	public void deleteFile(String path){
		File f = new File(path);
		f.delete();
	}
	
	public static void delete(File f){
		if(f.isDirectory()){
			File [] files = f.listFiles();
			for(int i=0;i<files.length;i++){
				delete(files[i]);
			}
		}else{
			f.delete();
		}
		f.delete();
	}
}
