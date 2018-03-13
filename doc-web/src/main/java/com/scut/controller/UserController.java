package com.scut.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.scut.exception.PageNotFound;
import com.scut.pojo.Module;
import com.scut.pojo.Project;
import com.scut.pojo.RequestResult;
import com.scut.pojo.Similarity;
import com.scut.pojo.User;
import com.scut.service.UserService;
import com.scut.utils.Consts;
import com.scut.utils.Http;
import com.scut.utils.ListUtil;

@Controller
public class UserController {
	
	@Value("${compute.similarity.start}")
	private Integer start;
	
	@Value("${compute.similarity.middle}")
	private Integer middle;
	
	@Value("${compute.similarity.end}")
	private Integer end;
	
	@Value("${compute.similarity.similarityUrl}")
	private String similarityUrl;
	
	@Value("${word.segmenter.wordUrl}")
	private String wordUrl;
			
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private RestTemplate restTemplate;
	//返回session的账号
	static String getAccount(HttpServletRequest request){
		SecurityContextImpl securityContextImpl = (SecurityContextImpl) request  
				 .getSession().getAttribute("SPRING_SECURITY_CONTEXT");  
		String account = securityContextImpl.getAuthentication().getName();
		return account;
	}
	//返回session的id
	int getId(HttpServletRequest request){
		String account = getAccount(request);
		return userService.getUserId(account);
	}
	
	/**
	 * 首页
	 * 如果用户是首次登录，那么会强制要求修改密码；
	 * ADMIN用户和普通用户展示的内容有差别。
	 * @param request
	 * @param map
	 * @return
	 */
	@GetMapping(value={"","/","/home"})
	public String home(HttpServletRequest request,Map<String,Object> map){
		String account = getAccount(request);
		if(account==null||account.trim().equals("")){
			throw new RuntimeException("账号不允许为空");
		}
		if(account.length()<6){
			throw new RuntimeException("账号长度不足6位");
		}
		if(account.substring(account.length()-6).equals(userService.getPassword(account))){
			return "redirect:password";
		}		
		//获取用户基本信息
		logger.info("获取用户基本信息");
		User user = userService.getUser(account);
		map.put("userInfo", user);
		//获取用户权限
		logger.info("获取用户权限信息，根据权限进行转发");
		String role = userService.getRole(account);
		if(Consts.ROLE_ADMIN.equalsIgnoreCase(role)){
			int id = getId(request);
			//获取用户创建的所有工程
			List<Project> lists = userService.getUserAllProjects(id);
			map.put("projectsList", lists);
			return "admin";
		}else if(Consts.ROLE_STUDENT.equals(role)){
			//显示当前用户加入的查重工程的信息
			int userId = userService.getUserId(account);
			List<Project> lists = userService.getJoinProjects(userId);
			map.put("projects", lists);
			List<User> users = new ArrayList<User>();
			//获取指导老师信息
			for(int i=0;i<lists.size();i++){
				Project project = lists.get(i);
				users.add(userService.getUser(project.getUid()));
			}
			map.put("teacherList", users);
			return "home";
		}else{
			throw new RuntimeException("该用户没被赋予任何权限！");
		}
	}
	
	/**
	 * 修改密码
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/password")
	public String defaultModifyPwordPage(HttpServletRequest request,HttpServletResponse response){
		return "modify_pword_page";
	}
	
	/**
	 * 验证密码，用于修改密码时，前端的判断原密码输入是否正确
	 * @param oldPword
	 * @param request
	 * @return
	 */
	@GetMapping("/verification/password")
	@ResponseBody
	public RequestResult validatePword(@RequestParam("oldPword") String oldPword,
			HttpServletRequest request){
		if(!oldPword.equals(userService.getPassword(getAccount(request)))){
			return null;
		}
		return new RequestResult(Http.HTTP_200,Consts.VALIDATE_PASSWORD);
	}
	
	/**
	 * 修改密码
	 * @param oldPword
	 * @param newPwrod
	 * @return
	 */
	@PutMapping("/password")
	public String modifyPword(@RequestParam("oldPword") String oldPword,
			@RequestParam("newPword") String newPwrod,
			HttpServletRequest request,Map<String,Object> map){
		int res = userService.modifyPword(getAccount(request), oldPword, newPwrod);
		if(res==0){
			map.put("error", new RequestResult(Http.HTTP_500, Consts.PASSWORD_MSG));
			return "500";
		}
		return "redirect:/home";
	}

	/**
	 * 查看某个查重工程的信息，查看工程下面的所有项目
	 * @param pid
	 * @param map
	 * @return
	 */
	@GetMapping("/projectInfo/{pid}")
	public String projectInfo(@PathVariable("pid")int pid,Map<String,Object> map,
			HttpServletRequest request){
		//验证请求参数的正确性
		Project proj = userService.getProject(pid);
		if(proj==null){
			map.put("error", new PageNotFound("请检查参数是否正确哦^~^"));
			return "404";
		}
		List<Module> lists = userService.getModules(pid);
		//获取用户基本信息
		String account = getAccount(request);
		User user = userService.getUser(account);
		map.put("userInfo", user);
		map.put("modulesList", lists);
		map.put("projectInfo",proj);
		
		List<String> submitResult = new ArrayList<String>();
		//获取状态
		logger.info("获取用户的提交状态");
		for(int i=0;i<lists.size();i++){
			Module module = lists.get(i);
			String path = module.getPath();
			path = path.replaceAll(Consts.SEPARATOR, "/");
			String res = userService.getSubmitStatu(path, getAccount(request));
			submitResult.add(res);
		}
		map.put("submitResult", submitResult);
		return "project_user";
	}
	
	/**
	 * 获取模块信息
	 * @param id
	 * @return
	 */
	@GetMapping("/moduleInfo/{id}")
	public String moduleInfo(@PathVariable("id")int id,Map<String,Object> map,HttpServletRequest request){
		Module module = userService.getModule(id);
		//判断id是否存在
		if(module==null){
			map.put("error", new PageNotFound("页面丢失了，请检测参数是否有误"));
			return "404";
		}
		//判断用户是否添加进模块所属的项目
		int userId = getId(request);
		int pid = module.getPid();
		boolean flag = userService.checkProjectByUidAndPid(userId,pid);
		if(!flag){
			map.put("error", new RuntimeException("您不属于该工程的成员，没有权限访问。"));
			return "403";
		}
		map.put("module", module);
		String path = module.getPath()+Consts.SEPARATOR+getAccount(request)+"-"+userService.getUserName(userId);
		path = path.replaceAll(Consts.SEPARATOR, "/");
		logger.info("用户拼接的模块路径："+path);
		String [] files = userService.getFiles(path);
		map.put("files", files);
		return "module";
	}
	
	/**
	 * 上传文件，尽最大努力上传
	 * @return
	 */
	@PostMapping("/file")
	@ResponseBody
	public RequestResult uploadFile(@RequestParam("file") MultipartFile file,
			int moduleId,int type,HttpServletRequest request){
		logger.info("用户上传文件");
		//路径替换，防止出错
		String name = file.getOriginalFilename().replaceAll("\\+", "");
		name = name.replaceAll("_", "");
		name = name.replaceAll("-", "");
		name = name.replaceAll(" ", "");
		if(type==0){			
			if(name==null||!(name.endsWith(".doc")||name.endsWith(".docx"))){
				throw new RuntimeException("文件格式错误，应该为doc或docx文件");
			}
		}else if(type==1){
			if(name==null||!name.endsWith(".zip")){
				throw new RuntimeException("文件格式错误，应该为zip文件");
			}
		}else{
			throw new RuntimeException("上传格式错误");
		}
		String account = getAccount(request);
		int userId = userService.getUserId(account);	
		RequestResult result = new RequestResult("200","no send data");
		String path = userService.uploadFile(file, moduleId,account, userId);//文件路径
		if(type==0){
			path = path.replaceAll("/", "_");
			path = path.replace(".", "+");
			logger.info("发到分词微服务中执行");
			result = restTemplate.getForObject(wordUrl+path, RequestResult.class);
		}
		return result;
	}
	
	/**
	 * 获取相似度，只显示前五个
	 * @param path： module的路径
	 * @param map
	 * @return
	 */
	@GetMapping("/similarity/{path}")
	public String computeSimilarity(@PathVariable("path")String path,Map<String,Object> map,
			HttpServletRequest request){
		String [] arr = path.split(Consts.SEPARATOR);
		String account = getAccount(request);
		String name = userService.getUserName(userService.getUserId(account));
		String fileName = account+"-"+name;
		String value = arr[start]+"+"+arr[middle]+"+"+arr[end]+"+"+fileName;
		String key = arr[start]+"-"+arr[middle]+"-"+arr[end];
//		String key = arr[5]+"-"+arr[7]+"-"+arr[8];
		logger.info("获取相似度，key=",key);
		ResponseEntity<Similarity> redponseBody = null;
		try{
			redponseBody = restTemplate.getForEntity(similarityUrl+key+"/value/"+value, Similarity.class);
		}catch(Exception e){
			map.put("error", new Exception("计算服务器暂时不可用，请以后再试。"));
			return "403";
		}
		logger.info("获取完成，开始计算前五位最高相似度");
		Similarity simil = redponseBody.getBody();
		if(simil==null){
			map.put("res","您还没有提交文本");
			return "similarity_user";
		}
		//计算最高相似度
		List<Double> doubleList = simil.getSimilarity();
		ArrayList<String> stringList = simil.getsId();
		//获取最大的六个值，在原来list的前6位
		ListUtil.getMaxSix(doubleList,stringList);
		//分别截取前6个返回
		List<Double> doList = new ArrayList<Double>();
		List<String> strList = new ArrayList<String>();
		for(int i=0;i<doubleList.size()&&i<6;i++){
			String arr1 [] = stringList.get(i).split("-");
			if(name.equals(arr1[1]))
				continue;
			doList.add(doubleList.get(i));
			strList.add(arr1[1]);
		}
		map.put("doList",doList);
		map.put("strList",strList);
		//根据前5个相似度值获取
		return "similarity_user";
	}
}
