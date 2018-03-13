package segmenter.scut.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import segmenter.scut.pojo.RequestResult;
import segmenter.scut.service.WordSegmenterQueue;

@RestController
public class WordSegmenterController {
	
	@Autowired
	private WordSegmenterQueue wsQueue;
	
	@GetMapping("/word/{url}")
	@ResponseBody
	public RequestResult wordSegmenter(@PathVariable String url){
		url = url.replaceAll("_", "/");
		url = url.replace("+", ".");
		System.out.println("放入队列的url:"+url);
		String path = wsQueue.addUrl(url);
		return new RequestResult("200","send success!","url:"+path);
	}
}
