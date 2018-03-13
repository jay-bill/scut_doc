package segmenter.scut.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.ansj.recognition.impl.StopRecognition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

@Configuration
public class AnsjConfig {
	@Bean
	public StopRecognition getStopRecognition(){
		InputStream in=null;
		ClassPathResource classPathResource = new ClassPathResource("classpath:stopWords.txt");
		try {
			in = classPathResource.getInputStream();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
//		BufferedReader br=null;
//		try {
//			br = new BufferedReader(new FileReader(ResourceUtils.getFile("classpath:stopWords.txt")));
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		String val = null;
		List<String> stopWords = new ArrayList<String>();
		try {
			while((val = br.readLine())!=null){
				stopWords.add(val);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		StopRecognition filter = new StopRecognition();
		filter.insertStopWords(stopWords);
		return filter;
	}
}
