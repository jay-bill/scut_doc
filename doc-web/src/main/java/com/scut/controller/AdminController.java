package com.scut.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.scut.exception.NameExisted;
import com.scut.pojo.Module;
import com.scut.pojo.Project;
import com.scut.pojo.Similarity;
import com.scut.pojo.User;
import com.scut.service.AdminService;
import com.scut.service.UserService;
import com.scut.utils.Consts;
import com.scut.utils.FileUtil;
import com.scut.utils.RandomUtil;
import com.scut.utils.ZipUtil;
import com.scut.utils.excel.ExcelFactory;
import com.scut.utils.excel.ExcelParser;

@Controller
@RequestMapping("admin")
public class AdminController {
	
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	@Autowired
	private AdminService ads;
	@Autowired
	private UserService userService;
	@Autowired
	private RestTemplate restTemplate;
	//root密码
	private static final String root = "1234";
	@Value("${compute.similarity.deleteRedisUrl}")
	private String deleteRedisUrl;
	
	@Value("${compute.similarity.start}")
	private Integer start;
	
	@Value("${compute.similarity.middle}")
	private Integer middle;
	
	@Value("${compute.similarity.end}")
	private Integer end;
	
	@Value("${compute.similarity.similarityUrlAdmin}")
	private String similarityUrlAdmin;
	/**
	 * 注册excel表格里的用户
	 * @param request
	 * @param response
	 * @param excelFile
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("deprecation")
	@PostMapping("excelUsers")
	@ResponseBody
	public List<User> excelUsers(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("excelFile") MultipartFile excelFile) throws Exception{
		HttpSession session = request.getSession();
		session.setAttribute(Consts.EXCEL_USERS_PROC, 0.0);
		FileUtil fut = new FileUtil();
		String filePath = fut.transfer(excelFile);//保存到本地
		logger.info("将上传excel暂存本地："+filePath);
		//解析excel表格的内容
		logger.info("开始解析excel内容。");
		ExcelParser excelParser = ExcelFactory.getExcelInstance(filePath);
		List<User> users = excelParser.parseExcelContent(session);//获取excel内容
		logger.info("excel解析完成，将获取到的用户添加到数据库，并和添加者关联起来。");
		//为用户添加leader
		String account = UserController.getAccount(request);
		Integer leaderId = userService.getUserId(account);
		for(User user : users){
			user.setLeaderId(leaderId);
		}
		//将数据保存到数据库
		ads.addUsers(users,session);
		logger.info("删除excel文件");
		//删除excel文件
		fut.deleteFile(filePath);
		return users;
	}
	
	/**
	 * 添加用户
	 * @param user
	 * @return
	 */
	@PostMapping("user")
	@ResponseBody
	public User addUser(User user,HttpServletRequest request){
		if(user==null){
			throw new RuntimeException("请输入完整的用户信息");
		}
		if(user.getAccount()==null||user.getAccount().equalsIgnoreCase("null")){
			throw new RuntimeException("用户的账号不允许为null");
		}
		if(user.getAccount().length()<6){
			throw new RuntimeException("账号长度需大于6位");
		}
		if(user.getPassword()==null){
			user.setPassword(user.getAccount().substring(user.getAccount().length()-6));
		}	
		logger.info("添加用户");
		String account = UserController.getAccount(request);
		Integer leaderId = userService.getUserId(account);
		//处理名字的特殊字符
		String name = user.getName();
		name = name.replaceAll("-", "");
		name = name.replaceAll("_", "");
		name = name.replaceAll("\\+", "");
		user.setName(name);
		user.setLeaderId(leaderId);
		ads.addUser(user);
		return user;
	}
	
	/**
	 * 创建查重工程
	 * @param proj
	 * @return
	 */
	@PostMapping("project")
	public String createProject(Project proj,HttpServletRequest request,HttpServletResponse
			response){
		String account = UserController.getAccount(request);
		int id = userService.getUserId(account);
		//工程路径=DEFAULT_PATH/用户id/用户账号/工程名*6位随机数
		String path = Consts.DEFAULT_PATH+Consts.SEPARATOR+id+Consts.SEPARATOR+account+Consts.SEPARATOR
					+proj.getName()+RandomUtil.generateIdentify();
		logger.info("创建查重工程，拼接路径："+path);
		proj.setPath(path);
		proj.setUid(id);
		//去掉工程名的特殊字符
		String name = proj.getName();
		name = name.replaceAll("_", "");
		name = name.replaceAll("-", "");
		name = name.replaceAll("\\+", "");
		proj.setName(name);
		
		logger.info("查重工程信息：",proj);
		try{
			logger.info("添加查重工程到数据库");
			ads.addProject(proj);
		}catch(NameExisted e){
			logger.warn("工程名不允许相同。创建的时候会在工程名后面加上6位随机数，一般情况下不会相同");
			return "redirect:forward/exception";
		}
		return "redirect:/home";
	}

	/**
	 * 创建模块
	 * @param module
	 * @return
	 */
	@PostMapping("module")
	public String createModule(String name,String description,String pid,String filetype,String
			attachtype,Date dateline,Map<String,Object> map){
		name = name.replaceAll("-", "");
		name = name.replaceAll("_", "");
		name = name.replaceAll("\\+", "");
		Module module = new Module(name,description,Integer.parseInt(pid),filetype,attachtype,dateline);
		logger.info("创建模块信息：",module);
		try{
			logger.info("添加模块到数据库");
			ads.addModule(module);
		}catch(NameExisted e){
			logger.info("同一工程下不允许有同名模块");
			map.put("error", e);
			return "403";
		}
		return "redirect:projectInfo?pid="+pid;
	}
	
	/**
	 * 添加用户到查重工程
	 * @param id
	 * @return
	 */
	@PostMapping("project/user")
	@ResponseBody
	public String addUserToProject(int uid,int pid,HttpServletRequest request){
		logger.info("将用户和查重工程关联");
		ads.addUserToProject(uid,pid);
		return "{'res':'ok'}";
	}
	
	/**
	 * 获取该用户的所有成员（学生）
	 * @param leaderId
	 * @return
	 */
	@GetMapping("members/{leaderId}")
	@ResponseBody
	public List<User> getMembers(@PathVariable("leaderId")int leaderId){
		return ads.getMembers(leaderId);
	}
	
	/**
	 * 获取项目中的所有学生
	 * @param pid
	 * @return
	 */
	@GetMapping("project/{pid}/members")
	@ResponseBody
	public List<User> getProjectMembers(int pid){
		List<User> users = ads.getProjectMembers(pid);//获取工程的成员
		return users;
	}
	
	/**
	 * 获取不存在项目中的但属于该用户的所有用户
	 * @param lid
	 * @param pid
	 * @return
	 */
	@GetMapping("getUsersByLeaderIDNotInProj")
	@ResponseBody
	public List<User> getUsersByLeaderIDNotInProj(int lid,int pid){
		return ads.getUsersByLeaderIDNotInProj(lid, pid);
	}
	
	/**
	 * 获取用户的所有查重工程
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 */
	@GetMapping("projectsList")
	public String projectsList(HttpServletRequest request,HttpServletResponse
			response,Map<String,List<Project>> map){
		String account = UserController.getAccount(request);
		int id = userService.getUserId(account);
		//获取用户创建的所有工程
		List<Project> lists = userService.getUserAllProjects(id);
		map.put("projectsList", lists);
		return "projects";
	}
	
	@RequestMapping("download")
	public void download(String type,int hostId,String path,
			HttpServletRequest request,HttpServletResponse res){		
		//设置下载名称
		String [] arr = path.split(Consts.SEPARATOR);
		String downloadName = arr[arr.length-1]+".zip";
		try {
			downloadName = URLEncoder.encode(downloadName,"utf-8");
		} catch (UnsupportedEncodingException e1) {
			downloadName = arr[arr.length-1];
			e1.printStackTrace();
		}
		//拼装路径				
		String filename = path.replaceAll(Consts.SEPARATOR, "/");
		logger.info("下载的路径："+filename+".zip");
		File f = new File(filename+".zip");
		if(!f.exists()){
			//打包成zip文件
			logger.info("将文件夹打成zip包");
			ZipUtil.zipFileWithTier(filename, filename+".zip");
		}		
		filename = filename+".zip";
		logger.info("设置下载请求头");
		res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename="+downloadName);
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
          os = res.getOutputStream();
          bis = new BufferedInputStream(new FileInputStream(new File(filename)));
          int i = bis.read(buff);
          while (i != -1) {
            os.write(buff, 0, buff.length);
            os.flush();
            i = bis.read(buff);
          }
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (bis != null) {
            try {
              bis.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
        logger.info("下载成功");
        //删除zip缓存
        File zipf = new File(filename);
        zipf.delete();
        logger.info("删除zip成功");
	}
	
	/**
	 * 删除项目
	 * @param pid
	 * @param request
	 * @return
	 */
	@GetMapping("deleteProject/{pid}")
	public String deleteProject(@PathVariable("pid")int pid,HttpServletRequest request){
		logger.info("删除项目");
		ads.deleteProject(pid);
		return "redirect:/home";
	}
	
	/**
	 * 编辑工程
	 * @param pid
	 * @return
	 */
	@PutMapping("project")
	public String editProject(int pid,Date dateline){
		Project pro = new Project();
		pro.setDateline(dateline);
		pro.setId(pid);
		logger.info("更新工程信息");
		ads.updateProject(pro);//更新工程信息
		return "redirect:/home";
	}
	
	@PutMapping("module")
	public String editModule(String description,int pid,Date dateline,
			int moduleId){
		Module mo = new Module();
		mo.setId(moduleId);
		mo.setDescription(description);
		mo.setDateline(dateline);
		logger.info("更新模块信息");
		ads.updateModule(mo);
		return "redirect:projectInfo/"+pid;
	}
	
	/**
	 * 删除模块
	 * @param moduleId
	 * @param pid
	 * @return
	 */
	@GetMapping("deleteModule/pid/{pid}/mid/{moduleId}")
	public String deleteModule(@PathVariable("pid")int pid,@PathVariable("moduleId")int moduleId){
		ads.deleteModule(moduleId);
		return "redirect:/admin/projectInfo/"+pid;
	}
	
	/**
	 * 获取已经提交作业的学生列表、未提交作业的学生列表、统计数据
	 * @param moduleId
	 * @param map
	 * @return
	 */
	@GetMapping("moduleResult/pid/{projetId}/mid/{moduleId}")
	public String moduleResult(@PathVariable("projetId")int projetId,
			@PathVariable("moduleId")int moduleId,Map<String,Object> map,
			HttpServletRequest request){
		logger.info("管理员开始获取模块信息");
		//已经提交作业的学生列表
		logger.info("1.获取完全提交的学生列表");
		Integer userId = userService.getUserId(UserController.getAccount(request));
		List<User> finishModule = ads.getFinishModuleMembers(userId,Integer.valueOf(projetId),Integer.valueOf(moduleId));
		//部分提交的学生列表
		logger.info("2.获取提交部分的学生列表");
		List<User> partModule = ads.getPartModuleMembers(userId,Integer.valueOf(projetId),Integer.valueOf(moduleId));
		//完全没提交的学生列表
		logger.info("3.获取未提交的学生列表");
		List<User> projectList = ads.getProjectMembers(projetId);
		List<User> notModule = new ArrayList<User>();
		for(int i=0;i<projectList.size();i++){
			User user = projectList.get(i);
			if(!finishModule.contains(user)&&!partModule.contains(user)){
				notModule.add(user);
			}
		}
		map.put("finishModule", finishModule);
		map.put("partModule", partModule);
		map.put("notModule", notModule);
		map.put("finishModuleCount", finishModule.size());
		map.put("partModuleCount", partModule.size());
		map.put("notModuleCount", notModule.size());
		map.put("moduleId", moduleId);
		return "module_result";
	}

	/**
	 * 查看某个查重工程的信息，查看工程下面的所有项目
	 * @param pid
	 * @param map
	 * @return
	 */
	@GetMapping("projectInfo/{pid}")
	public String projectInfo(@PathVariable("pid")int pid,Map<String,Object> map,
			HttpServletRequest request){
		logger.info("获取项目下的所有模块");
		List<Module> lists = userService.getModules(pid);
		Project proj = userService.getProject(pid);
		//获取用户基本信息
		String account = UserController.getAccount(request);
		User user = userService.getUser(account);
		map.put("userInfo", user);
		map.put("modulesList", lists);
		map.put("projectInfo",proj);
		return "project";
	}
	
	/**
	 * 同步删除用户
	 * @param users
	 * @param projectId
	 * @return
	 */
	@PostMapping("users")
	@ResponseBody
	public String[] deleteUsers(@RequestParam(value = "users[]") int [] users,int projectId,
			HttpServletRequest request){
		Project proj = userService.getProject(projectId);
		if(proj==null){
			throw new RuntimeException("工程不存在");
		}
		//获取该工程下的所有项目
		List<Module> modules = userService.getModules(projectId);
		//删除硬盘数据和redis数据
		logger.info("step 1:删除硬盘数据和redis数据");
		for(int i=0;i<users.length;i++){
			String account = userService.getAccount(users[i]);
			String name = userService.getUserName(users[i]);
			String fileName = account+"-"+name;
			for(int j=0;j<modules.size();j++){
				Module module = modules.get(j);
				String path = module.getPath();
				String [] arr = path.split(Consts.SEPARATOR);
				String key = arr[start]+"-"+arr[middle]+"-"+arr[end];
				String value=arr[start]+"+"+arr[middle]+"+"+arr[end]+"+"+fileName;
				path = path.replaceAll(Consts.SEPARATOR, "/");
				File file = new File(path+"/"+fileName);
				//删除硬盘数据
				FileUtil.delete(file);
				//删除redis数据
				try{
					restTemplate.delete(deleteRedisUrl+key+"/value/"+value);
				}catch(Exception e){
					throw new RuntimeException("重复率数据删除失败，请稍后重试");
				}
			}
		}		
		//删除数据库数据
		logger.info("step 2:删除数据库数据");
		ads.deleteUsers(users,projectId);
		return new String[]{"ok"};
	}
	
	/**
	 * 获取相似度
	 * @param moduleId
	 * @param map
	 * @return
	 */
	@GetMapping("computeSimilarity")
	public String computeSimilarity(String path,int id,Map<String,Object> map){
		map.put("moduleId", id);
		String [] arr = path.split(Consts.SEPARATOR);
		String key = arr[start]+"-"+arr[middle]+"-"+arr[end];
//		String key = arr[5]+"-"+arr[7]+"-"+arr[8];
		logger.info("获取相似度，key=",key);
		ResponseEntity<Similarity []> redponseBody = null;
		try{
			redponseBody = restTemplate.getForEntity(similarityUrlAdmin+key, Similarity[].class);
		}catch(Exception e){
			map.put("error", new Exception("计算服务器暂时不可用，请以后再试。"));
			return "403";
		}
		logger.info("获取完成，开始计算最高相似度");
		Similarity[] lists = redponseBody.getBody();
		map.put("similarityList", lists);
		List<Similarity> bestSimilarityList = new ArrayList<Similarity>();
		//计算最高相似度
		for(int i=0;i<lists.length;i++){
			Similarity simil = lists[i];
			Similarity bestSimi = new Similarity();
			ArrayList<Double> doubleList = simil.getSimilarity();
			ArrayList<String> stringList = simil.getsId();
			//选出最高相似度
			ArrayList<Integer> index = new ArrayList<Integer>();
			ArrayList<Double> bestDoubleList = new ArrayList<Double>();
			//得到不是自己本人的最大值
			double max = 0.0;
			for(int j=0;j<doubleList.size();j++){
				if(doubleList.get(j)>max&&!stringList.get(j).equals(simil.getId())){
					max = doubleList.get(j);
				}
			}
			bestDoubleList.add(max);
			//根据最大值记录下标
			for(int j=0;j<doubleList.size();j++){
				if(doubleList.get(j)==max){
					index.add(j);
				}
			}
			//保存用户账号，为了下载文件
			ArrayList<String> accountList = new ArrayList<String>();
			//根据下标记录名称
			ArrayList<String> bestStringList = new ArrayList<String>();
			for(int j=0;j<index.size();j++){
				String name = stringList.get(index.get(j));
				accountList.add(name.split("-")[0].split("\\+")[3]);
				bestStringList.add(name.split("-")[1]);				
			}
			bestSimi.setAccount(simil.getId().split("-")[0].split("\\+")[3]);
			bestSimi.setId(simil.getId().split("-")[1]);
			bestSimi.setsId(bestStringList);
			bestSimi.setSimilarity(bestDoubleList);
			bestSimi.setAccountList(accountList);
			bestSimilarityList.add(bestSimi);
		}
		logger.info("最高相似度计算完成");
		map.put("bestSimilarityList", bestSimilarityList);
		return "similarity";
	}
	
	@PostMapping("root")
	public String rootPassword(String rootPassword){
		if(rootPassword.equals(root)){
			return "redirect:add_admin_page";
		}
		return "redirect:root_login_error";
	}
	
	@GetMapping("add_admin_page")
	private String add_admin_page(){
		return "add_admin_page";
	}
	@GetMapping("root_login_error")
	private String root_login_error(){
		return "root_login_error";
	}
	
	/**
	 * 添加管理员
	 * @param user
	 * @param map
	 * @param request
	 * @return
	 */
	@PostMapping("addAdmin")
	public String addAdmin(User user,Map<String,Object> map,HttpServletRequest request){
		user.setPassword(user.getAccount().substring(user.getAccount().length()-6, user.getAccount().length()));
		String res = ads.addAdmin(user);
		if(res!=null){
			map.put("error", res);
		}
		getAdmin(request,map);
		return "admin_user";
	}
	
	/**
	 * 获取管理员
	 * @param request
	 * @param map
	 * @return
	 */
	@GetMapping("getAdmin")
	public String getAdmin(HttpServletRequest request,Map<String,Object> map){
		int userId = userService.getUserId(UserController.getAccount(request));
		List<User> list = ads.getAdmin(userId);
		map.put("list", list);
		return "admin_user";
	}
	
	/**
	 * 下载doc文件
	 * @param mid
	 * @param account
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("downloadDoc")
	public void downloadDoc(int mid,String account,Map<String,Object> map,
			HttpServletRequest request,HttpServletResponse res){
		//拼装路径
		Module module = userService.getModule(mid);
		String path = module.getPath();
		path = path.replaceAll(Consts.SEPARATOR, "/");//module的路径
		String name = userService.getUserName(userService.getUserId(account));
		String filedir = path+"/"+account+"-"+name;
		//获取filedir下面的doc文件
		File file = new File(filedir);
		File [] files = file.listFiles();
		String filename = "";
		File targetFile = null;
		for(int i=0;i<files.length;i++){
			File f = files[i];
			if(f.getName().endsWith(".doc")||f.getName().endsWith(".docx")){
				filename = f.getName();
				targetFile = f;
				break;
			}
		}
		//设置下载名称
		logger.info("设置下载请求头");
		res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        try {
        	String end = filename.substring(filename.lastIndexOf("."), filename.length());
			filename = URLEncoder.encode(account+"-"+name+end,"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        res.setHeader("Content-Disposition", "attachment;filename="+filename);
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
          os = res.getOutputStream();
          bis = new BufferedInputStream(new FileInputStream(targetFile));
          int i = bis.read(buff);
          while (i != -1) {
            os.write(buff, 0, buff.length);
            os.flush();
            i = bis.read(buff);
          }
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (bis != null) {
            try {
              bis.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
        logger.info("下载成功");
        //删除zip缓存
        File zipf = new File(filename);
        zipf.delete();
        logger.info("删除zip成功");
	}
}
