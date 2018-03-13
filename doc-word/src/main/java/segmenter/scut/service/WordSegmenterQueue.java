package segmenter.scut.service;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class WordSegmenterQueue {
	
	private final static Logger logger = LoggerFactory.getLogger(WordSegmenterQueue.class);
	
	private final ConcurrentLinkedQueue<String> queue;

	public WordSegmenterQueue(){
		queue = new ConcurrentLinkedQueue<String>();
	}

	public ConcurrentLinkedQueue<String> getQueue() {
		return queue;
	}
	
	public String addUrl(String url){
		queue.offer(url);
		logger.info("add wordfile url to the queue！");
		return url;
	}
}
