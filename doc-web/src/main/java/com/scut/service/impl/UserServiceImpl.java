package com.scut.service.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.scut.mapper.ModuleMapper;
import com.scut.mapper.ProjectMapper;
import com.scut.mapper.UserMapper;
import com.scut.pojo.Module;
import com.scut.pojo.Project;
import com.scut.pojo.User;
import com.scut.service.UserService;
import com.scut.utils.Consts;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private ModuleMapper moduleMapper;
	@Autowired
	private ProjectMapper projectMapper;
	
	@Override
	public String getPassword(String account) {
		String pword = userMapper.selectUserPword(account);		
		return pword;
	}
	
	@Override
	public int modifyPword(String account, String oldPword, String newPwrod) {
		try{
			userMapper.updatePword(account, newPwrod);
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
		return 1;
	}

	@Override
	public int getUserId(String account) {
		return userMapper.selectUserId(account);
	}
	
	@Override
	public List<Module> getModules(int pid) {
		return moduleMapper.selectModules(pid);
	}

	@Override
	public Project getProject(int id) {
		return projectMapper.selectProject(id);
	}
	
	@Override
	public List<Project> getUserAllProjects(int id) {
		List<Project> lists = projectMapper.selectUserProjects(id);
		return lists;
	}

	@Override
	public List<Project> getJoinProjects(int userId) {
		return projectMapper.selectUserJoinedProjects(userId);
	}

	@Override
	public Module getModule(int id) {
		return moduleMapper.selectModule(id);
	}

	@Override
	public String uploadFile(MultipartFile mFile, int moduleId, String userAccount,
			int userId) {
		Module module = moduleMapper.selectModule(moduleId);
		Date dateline = module.getDateline();
		java.sql.Date now = getDate();//获取当前日期
		if(dateline.compareTo(now)<0){
			throw new RuntimeException("截止日期已过！");
		}
		String path = module.getPath();
		String zip = path.substring(0, path.lastIndexOf(Consts.SEPARATOR));
		zip=zip.replaceAll(Consts.SEPARATOR, "/");
		System.out.println("zip的路径："+zip+".zip");
		File zipFile = new File(zip+".zip");
		zipFile.delete();//删除zip文件
		path = path+Consts.SEPARATOR+userAccount+"-"+userMapper.selectUserName(userId);
		path = path.replaceAll(Consts.SEPARATOR, "/");
		File dirFile = new File(path);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		File [] files = dirFile.listFiles();
		//替换名字
		String oriName = mFile.getOriginalFilename().replaceAll("\\+", "");
		oriName = oriName.replaceAll("_", "");
		oriName = oriName.replaceAll("-", "");
		//处理文件名中含有点的情况
		if(oriName.endsWith(".doc")||oriName.endsWith(".zip")){			
			oriName = rename(oriName,4);
		}else if(oriName.endsWith(".docx")){
			oriName = rename(oriName,5);
		}
		boolean flag = false;
		if(oriName.endsWith(".doc")||oriName.endsWith(".docx")){
			flag = true;
		}else if(oriName.endsWith(".zip")){
			flag = false;
		}
		for(int i=0;i<files.length;i++){
			String name = files[i].getName();
			boolean res = name.endsWith(".doc")||name.endsWith(".docx");
			if(flag&&res){
				files[i].delete();//删除自身
				break;
			}
			if(!flag&&!res){
				files[i].delete();//删除自身
				break;
			}
		}
		path = path+Consts.SEPARATOR+oriName;
		path = path.replaceAll(Consts.SEPARATOR, "/");
		File file = new File(path);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {				
				e.printStackTrace();
				return null;
			}
		}
		try {
			mFile.transferTo(file);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return path;
	}
	
	private String rename(String oriName,int len){
		String end = oriName.substring(oriName.length()-len, oriName.length());
		String prefix = oriName.substring(0,oriName.length()-len);
		prefix = prefix.replaceAll("\\.", "");//替换所有的点
		return prefix+end;
	}
	
	private java.sql.Date getDate(){
		java.util.Date nDate = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = sdf.format(nDate);
        java.sql.Date now = java.sql.Date.valueOf(sDate);  //转型成java.sql.Date类型
        return now;
	}

	@Override
	public String[] getFiles(String path) {
		File dir = new File(path);
		File [] files = dir.listFiles();
		if(files==null||files.length<=0)
			return null;
		else if(files.length>2){
			for(int i=0;i<files.length;i++){
				files[i].delete();
			}
			throw new RuntimeException("文件出错，请重新上传！");
		}
		String [] res = new String[2];
		for(int i=0;i<files.length;i++){
			res[i] = files[i].getName();
		}
		return res;
	}

	@Override
	public String getUserName(int id) {
		return userMapper.selectUserName(id);
	}

	@Override
	public String getAccount(int hostId) {
		return userMapper.selectAccount(hostId);
	}

	@Override
	public String getRole(String account) {
		return userMapper.selectRole(account);
	}

	@Override
	public User getUser(String account) {
		return userMapper.selectUserByAccount(account);
	}

	@Override
	public User getUser(int id) {
		return userMapper.selectUserById(id);
	}
	
	@Override
	public String getSubmitStatu(String path,final String account){
		File dir = new File(path);
		File [] dirs = dir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				String [] arr = name.split("-");
				if(arr.length>0&&(arr[0].equalsIgnoreCase(account))){
					return true;
				}
				return false;
			}			
		});
		
		for(int i=0;i<dirs.length;i++){
			File file = dirs[i];
			if(file.listFiles().length==2){
				return "已完全提交";
			}else if(file.listFiles().length==1){
				return "已提交部分";
			}
		}
		return "未提交";
	}

	@Override
	public boolean checkProjectByUidAndPid(int userId, int pid) {
		Integer flag = projectMapper.checkProjectIdandUserId(userId, pid);
		if(flag!=null)
			return true;
		return false;
	}
}
