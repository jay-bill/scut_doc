package segmenter.scut.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.ansj.recognition.impl.StopRecognition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import segmenter.scut.pojo.RequestResult;
import segmenter.scut.service.WordSegmenterQueue;
import segmenter.scut.service.WordSegmenterThread;

@Component
@EnableScheduling
public class QueueMonitor {
	
	private final static Logger logger = LoggerFactory.getLogger(QueueMonitor.class);
	@Autowired
	private WordSegmenterQueue queue;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private StopRecognition filter;
	
	@Value("${compute.similarity.receive}")
	private String destination;
	
	private ExecutorService es = Executors.newFixedThreadPool(5);
	
	@Scheduled(fixedRate=2000)//每两秒查看一次队列
	public void consumeUrl(){
		String url = queue.getQueue().poll();
		if(url==null)	return;
		//放入线程池中处理
		WordSegmenterThread thread = new WordSegmenterThread(url,filter);
		Future<HashMap<String, ArrayList<String>>> ft = es.submit(thread);
		HashMap<String, ArrayList<String>> resMap = null;
		try {
			resMap= ft.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return;
		}
		//发送到“相似度计算微服务”中
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, Object> map= new LinkedMultiValueMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		String value = null;
		try {
			value = mapper.writeValueAsString(resMap);
			logger.info("map tranfer to json:"+value);
		} catch (JsonProcessingException e) {
			logger.warn("出现JsonProcessingException异常了");
			e.printStackTrace();
		}
		map.add("wordMap", value);
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);
		ResponseEntity<RequestResult> response = restTemplate.postForEntity( destination, request , RequestResult.class);
		logger.info("send to calarute result:",response);
	}
}
