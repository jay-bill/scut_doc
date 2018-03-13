package com.scut.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.scut.pojo.Module;
import com.scut.pojo.Project;
import com.scut.pojo.User;

public interface UserService {
	
	String getPassword(String account);
	
	int modifyPword(String account,String oldPword,String newPwrod);
	
	int getUserId(String account);
	
	/**
	  * 获取工程下面的项目
	  * @param pid
	  * @return
	  */
	 List<Module> getModules(int pid);
	 
	 /**
	  * 获取项查重工程信息
	  * @param id
	  * @return
	  */
	 Project getProject(int id);

	 /**
	  * 获取用户的所有工程
	  * @param id
	  * @return
	  */
	 List<Project> getUserAllProjects(int id);
	 
	 /**
	  * 获取用户加入的所有项目
	  * @param userId
	  * @return
	  */
	 List<Project> getJoinProjects(int userId);
	 
	 /**
	  * 获取模块信息
	  * @param id
	  * @return
	  */
	 Module getModule(int id);
	 
	 /**
	  * 上传文件
	  * @param mFile
	  * @param moduleId
	  * @param account
	  * @return
	  */
	 String uploadFile(MultipartFile mFile,int moduleId,String account,int userId);
	 
	 /**
	  * 获取文件名
	  * @param path
	  * @return
	  */
	 String[] getFiles(String path);
	 
	 /**
	  * 获取用户名
	  * @param id
	  * @return
	  */
	 String getUserName(int id);

	 /**
	  * 获取账号
	  * @param hostId
	  * @return
	  */
	 String getAccount(int hostId);
	 
	 /**
	  * 获取角色
	  * @param account
	  * @return
	  */
	 String getRole(String account);
	 
	 /**
	  * 获取用户信息
	  * @param account
	  * @return
	  */
	 User getUser(String account);
	 
	 /**
	  * 获取用户信息
	  * @param id
	  * @return
	  */
	 User getUser(int id);

	 /**
	  * 获取提交状态
	  * @return
	  */
	String getSubmitStatu(String path, String id);

	/**
	 * 判断用户是否和工程相关联
	 * @param userId
	 * @param pid
	 * @return
	 */
	boolean checkProjectByUidAndPid(int userId, int pid);

}
