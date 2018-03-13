package compute.scut.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import compute.scut.pojo.Similarity;


/**
 * 余弦相似度
 * @author jaybill
 *
 */
public class CosineSimilarityService implements Callable<Similarity>{
	
	private List<HashMap<String,ArrayList<String>>> list;
	private HashMap<String,ArrayList<String>> currentStu;
	protected ExecutorService es = Executors.newFixedThreadPool(5);
	protected CompletionService<Similarity> cs = new ExecutorCompletionService<Similarity>(es);
	public CosineSimilarityService(){}
	private CosineSimilarityService(List<HashMap<String,ArrayList<String>>> list,
			HashMap<String,ArrayList<String>> currentStu){
		this.list = list;
		this.currentStu = currentStu;
	}
	
	public List<Similarity> analyseSimilarity(List<HashMap<String, ArrayList<String>>> list) {
		List<Similarity> resList = new ArrayList<Similarity>();
		for(int i=0;i<list.size();i++){
			HashMap<String,ArrayList<String>> curMap = list.get(i);
			cs.submit(new CosineSimilarityService(list,curMap));
		}
		//等待各个线程完成
		for(int i=0;i<list.size();i++){
			try {
				Similarity sm = cs.take().get();
				resList.add(sm);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		es.shutdown();
		cs=null;
		//整理格式
		List<Similarity> res = new ArrayList<Similarity>();
		if(resList.size()<=0){
			return res;
		}
		ArrayList<String> arr = resList.get(0).getsId();
		for(int i=0;i<arr.size();i++){
			String flag = arr.get(i);
			for(int j=0;j<resList.size();j++){
				if(resList.get(j).getId().equals(flag)){
					Similarity sm = resList.get(j);
					res.add(sm);
					break;
				}
			}			
		}
		return res;
	}

	/**
	 * 一个用户与其他用户的相似度，比如张三-李四；张三-王八；张三-赵六......等等
	 * @param list
	 * @return
	 */
	public Similarity analyseSimilarityForUser(List<HashMap<String, ArrayList<String>>> list,String value) {
		//先获取key为参数value的map对象的索引值，然后用该索引值对应的数据一一和其他的计算
		int index = getMapIndex(list,value);
		if(index==-1){
			return null;
		}
		HashMap<String, ArrayList<String>> flagMap = list.get(index);
		return analyse(list,flagMap);
	}
	
	private int getMapIndex(List<HashMap<String, ArrayList<String>>> list,String value){
		for(int i=0;i<list.size();i++){
			HashMap<String, ArrayList<String>> map = list.get(i);
			String key = getFirstKey(map);
			if(value.equalsIgnoreCase(key)){
				return i;
			}
		}
		return -1;
	}
	/**
	 * 余弦相似度
	 * @param map
	 * @return
	 */
	private double cosSimilarityFacade(HashMap<String, int[]> map) {
		//点乘相加
		int sum = pointMulti(map);
		//平方相乘，再开根号
		double squareMuti = squareMuti(map);
		//求相似度
		double simi = sum/squareMuti;
		return simi;
	}

	/**
	 * 先对每个向量值平方相加后，在把两个向量所得的结果相乘，最后开平方
	 * @param map
	 * @return
	 */
	private double squareMuti(HashMap<String, int[]> map) {
		Iterator<Entry<String, int[]>> it = map.entrySet().iterator();
		double res1 = 0.0,res2 = 0.0;
		while(it.hasNext()){
			Entry<String, int[]> en = it.next();
			int [] arr = en.getValue();
			res1 += arr[0]*arr[0]; 
			res2 += arr[1]*arr[1];
		}
		double res = Math.sqrt(res1*res2);
		return res;
	}

	/**
	 * 点乘求和
	 * @param map
	 * @return 
	 */
	private int pointMulti(HashMap<String, int[]> map) {
		Iterator<Entry<String, int[]>> it = map.entrySet().iterator();
		int sum = 0;
		while(it.hasNext()){
			Entry<String, int[]> en = it.next();
			int [] arr = en.getValue();
			sum += arr[0]*arr[1];
		}
		return sum;
	}

	/**
	 * 获取词频
	 * @return
	 */
	private HashMap<String,int []> getWordFrequency(ArrayList<String> curList,ArrayList<String> nextList){
		HashMap<String,int []> set = new HashMap<String,int []>();
		for(int k=0;k<curList.size();k++){
			if(!set.containsKey(curList.get(k))){
				set.put(curList.get(k), new int[]{1,0});
			}else{
				set.get(curList.get(k))[0]++;
			}
		}
		for(int k=0;k<nextList.size();k++){
			if(!set.containsKey(nextList.get(k))){
				set.put(nextList.get(k), new int[]{0,1});
			}else{
				set.get(nextList.get(k))[1]++;
			}
		}
		return set;
	}

	
	public Similarity analyse(List<HashMap<String, ArrayList<String>>> list,
			HashMap<String, ArrayList<String>> curMap) {
		System.out.println("余弦相似度："+Thread.currentThread().getName());
		Similarity resOne = new Similarity();//与其他人的重复率比较			
		for(int j=0;j<list.size();j++){
			double similar = 1.0;//相似度
			HashMap<String,ArrayList<String>> nextMap = list.get(j);
			if(!curMap.equals(list.get(j))){					
				//获取两个链表
				ArrayList<String> curList = curMap.values().iterator().next();
				ArrayList<String> nextList = nextMap.values().iterator().next();
				//计算两个链表的词频
				HashMap<String,int []> wordFrequency = getWordFrequency(curList,nextList);
				//求余弦相似度
				similar = cosSimilarityFacade(wordFrequency);
				//计算相似度,保留3位小数
				BigDecimal b = new BigDecimal(similar);
				similar = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			//设置当前学生的姓名
			String currentKey = getFirstKey(curMap);
			resOne.setId(currentKey);
			
			String nextKey = getFirstKey(nextMap);
			//设置比较学生的id
			ArrayList<String> arr = resOne.getsId();
			arr.add(nextKey);
			resOne.setsId(arr);
			//设置重复率
			ArrayList<Double> arrSim = resOne.getSimilarity();
			arrSim.add(similar);
			resOne.setSimilarity(arrSim);
		}
		return resOne;
	}
	
	/**
	 * 获取HashMap的第一个key
	 */
	String getFirstKey(HashMap<String,ArrayList<String>> nextStu){
		Iterator<Entry<String, ArrayList<String>>> it = nextStu.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, ArrayList<String>> en = it.next();
			return en.getKey();
		}
		return null;
	}
	
	@Override
	public Similarity call() throws Exception {
		return analyse(list,currentStu);
	}
}
