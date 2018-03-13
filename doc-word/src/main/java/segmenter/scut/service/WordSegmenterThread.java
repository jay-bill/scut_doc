package segmenter.scut.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 获取文本字符串；
 * 分词数组。
 * @author jaybill
 *
 */
public class WordSegmenterThread implements Callable<HashMap<String,ArrayList<String>>>{

	private final static Logger logger = LoggerFactory.getLogger(WordSegmenterThread.class);
	private String url=null;
	private StopRecognition filter;
	public WordSegmenterThread(String url,StopRecognition filter){
		this.url = url;
		this.filter = filter;
	}
	public HashMap<String, ArrayList<String>> call() throws Exception {
		logger.info("step 1:分割 url获取信息组成key；");
		//分割url获取信息
		String [] strArray = url.split("/");
		StringBuilder sb = new StringBuilder();
		//linux下的路径
		sb.append(strArray[5]);
		sb.append("+");
		sb.append(strArray[7]);
		sb.append("+");
		sb.append(strArray[8]);
		sb.append("+");
		sb.append(strArray[9]);
		//windows下的路径
//		sb.append(strArray[1]);
//		sb.append("+");
//		sb.append(strArray[3]);
//		sb.append("+");
//		sb.append(strArray[4]);
//		sb.append("+");
//		sb.append(strArray[5]);
		//分词数组的key，由（老师id+项目名+模块名+学生）构成
		String key = sb.toString();
		logger.info("step 2 ：根据url获取文本字符串；");
		//获取文本
		String text = null;
		text = getText();
		logger.info("step 3：获取分词数组！");
		//获取分词数组
		Result resArray = ToAnalysis.parse(text).recognition(filter);
		ArrayList<String> value = new ArrayList<String>();
		List<Term> terms = resArray.getTerms();
		System.out.println("url为："+url);
		for (int i = 0; i < terms.size(); i++) {
			Term term = terms.get(i);
			String tmp = term.getName();
			if(tmp.equals(" ")||tmp.equalsIgnoreCase("\t")||
				tmp.equalsIgnoreCase("\n")||tmp.equalsIgnoreCase("\\r")
				||tmp.equalsIgnoreCase("\r\n")||tmp.equals("　")||
				tmp.equals(" ")||tmp.equalsIgnoreCase("\r")){
				continue;
			}
			value.add(tmp);
		}
		
		logger.info("step 4：放入map中返回");
		//放入map中
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		map.put(key, value);
		return map;
	}
	
	//获取文本内容
	private String getText(){
		String wordText="";
		File file = new File(url);  
		//.docx
		if(file.getName().endsWith(".docx")){
			OPCPackage opcPackage;
			try {
				opcPackage = POIXMLDocument.openPackage(url);
				POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage); 
				wordText = extractor.getText();//获取word文档内容，字符串形式
				opcPackage.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlException e) {
				e.printStackTrace();
			} catch (OpenXML4JException e) {
				e.printStackTrace();
			}  
		}else if(file.getName().endsWith(".doc")){
			 FileInputStream stream;
			try {
				stream = new FileInputStream(file);
				WordExtractor word = new WordExtractor(stream);  
	            wordText = word.getText();
	            stream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}           
		}	
		return wordText;
	}
}
