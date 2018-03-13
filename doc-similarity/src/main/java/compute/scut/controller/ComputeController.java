package compute.scut.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import compute.scut.pojo.RequestResult;
import compute.scut.pojo.Similarity;
import compute.scut.service.CosineSimilarityService;

@RestController
public class ComputeController {
	
	private static final Logger logger = LoggerFactory.getLogger(ComputeController.class);

	@Autowired
	private RedisTemplate<String,HashMap<String, ArrayList<String>>> redisTemplate;
	/**
	 * 接收分词数组，并存入redis数据库
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/receive")
	public RequestResult receive(HttpServletRequest request){
		String value = (String)request.getParameter("wordMap");
		System.out.println("得到的值是："+value);
		if(value==null){
			return new RequestResult("406","no value!","json data is null!");
		}
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String, ArrayList<String>> wordMap=null;
		try {
			wordMap = mapper.readValue(value, HashMap.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
			return new RequestResult("406","no value!","json data is null!");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return new RequestResult("406","no value!","json data is null!");
		} catch (IOException e) {
			e.printStackTrace();
			return new RequestResult("406","no value!","json data is null!");
		}
		Set<String> setKey = wordMap.keySet();
		Iterator<String> iterator = setKey.iterator();
		// 从while循环中读取key
		String key = null;
		while(iterator.hasNext()){
			key=iterator.next();
			if(key!=null)	break;
		}
		String [] keyComponents = key.split("\\+");
		logger.info("keyComponents is :"+keyComponents[0]+","+keyComponents[1]+","+keyComponents[2]);
		//redis的hash类型的key
		String redisKey = keyComponents[0]+"-"+keyComponents[1]+"-"+keyComponents[2];
		//把分词数组放入redis内存数据库中
		//格式为(redisKey,[<老师id+工程名+项目名+学生账号-学生姓名,分词数组>,....])
		redisTemplate.opsForHash().putAll(redisKey, wordMap);
		logger.info("wordarrays is already stored in redis db!");
		return new RequestResult("200","send to compute-similarity success!","ok!");
	}
	
	/**
	 * 计算相似度
	 * @param key 老师id-项目名-模块名
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("/compute/admin/{key}")
	public List<Similarity> computeForAdmin(@PathVariable String key){
		Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
		Iterator<Map.Entry<Object, Object>> entries = map.entrySet().iterator();  	
		List<HashMap<String, ArrayList<String>>> list = 
				new ArrayList<HashMap<String, ArrayList<String>>>();
		while (entries.hasNext()) {  
		    Map.Entry<Object, Object> entry = entries.next();  		  
		    HashMap<String,ArrayList<String>> hashValue = 
		    		new HashMap<String,ArrayList<String>>();
		    hashValue.put((String)entry.getKey(), (ArrayList<String>)entry.getValue());
		    list.add(hashValue);
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  
		}  
		CosineSimilarityService css = new CosineSimilarityService();
		List<Similarity>  resList = css.analyseSimilarity(list);
		for(int i=0;i<resList.size();i++){
			Similarity sm = resList.get(i);
			ArrayList<Double> dbs = sm.getSimilarity();
			ArrayList<String> as = sm.getsId();
			String name = sm.getId();
			for(int j=0;j<dbs.size();j++){
				double d = dbs.get(j);
				String aname = as.get(j);
				logger.info(name+"-"+aname+":"+d);
			}
			logger.info("---------------------------");
		}
		return resList;
	}
	
	/**
	 * 只计算一个用户的相似度
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("/compute/{key}/value/{value}")
	public Similarity computeForUser(@PathVariable String key,@PathVariable String value){
		logger.info("只计算一个用户的相似度：key="+key+",user="+value);
		Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
		Iterator<Map.Entry<Object, Object>> entries = map.entrySet().iterator();  	
		List<HashMap<String, ArrayList<String>>> list = 
				new ArrayList<HashMap<String, ArrayList<String>>>();
		while (entries.hasNext()) {  
		    Map.Entry<Object, Object> entry = entries.next();  		  
		    HashMap<String,ArrayList<String>> hashValue = 
		    		new HashMap<String,ArrayList<String>>();
		    hashValue.put((String)entry.getKey(), (ArrayList<String>)entry.getValue());
		    list.add(hashValue);
		    logger.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());  
		}  
		CosineSimilarityService css = new CosineSimilarityService();
		Similarity res = css.analyseSimilarityForUser(list,value);
		return res;
	}
	
	@DeleteMapping("/key/{key}/value/{value}")
	public Long deteleRedisKey(@PathVariable String key,@PathVariable String value){
		HashOperations<String, String, String> hashOpt = redisTemplate.opsForHash();
		logger.info("此次删除的redis的key为："+value);
		return hashOpt.delete(key, value);
	}
}
