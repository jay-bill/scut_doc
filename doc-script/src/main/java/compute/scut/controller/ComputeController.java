package compute.scut.controller;

import java.io.File;
import java.io.FilenameFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.scut.pojo.RequestResult;

@RestController
public class ComputeController {
	String wordUrl = "http://localhost:23081/word/";
	static int x=1;
	@Autowired
	private RestTemplate restTemplate;
	@ResponseBody
	@GetMapping("/test")
	public String []  refreshRedis(){
		
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				String path = "/home/jbli/project/data/1/123456/高性能计算与云计算201481/";
				File file = new File(path);
				File [] files = file.listFiles();
				System.out.println("长度是："+files.length);
				for(int i=0;i<files.length;i++){
					System.out.println("第"+i+"次进来");
					File shiyan = files[i];
					if(shiyan.isDirectory()){
						File [] xuesheng = shiyan.listFiles();
						for(int j=0;j<xuesheng.length;j++){
							File [] docs = xuesheng[j].listFiles(new FilenameFilter() {
								
								@Override
								public boolean accept(File dir, String name) {
									if(name.endsWith(".doc")||name.endsWith(".docx")){
										return true;
									}
									return false;
								}
							});
							if(docs!=null&&docs.length>0){
								String url = docs[0].getAbsolutePath();
								System.out.println("文件路径是："+url);
								try{
									url = url.replaceAll("/", "_");
									url = url.replace(".", "+");
									restTemplate.getForObject(wordUrl+url, RequestResult.class);
								}catch(Exception e){
									System.out.println("出错了！");
									return;
								}
								System.out.println("完成"+(x++)+"个");
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}		
		});
		t.start();
		return new String[]{"ok"};
	}
}
