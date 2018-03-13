package com.scut.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.scut.pojo.Module;
import com.scut.pojo.Project;
import com.scut.pojo.User;

/**
 * 提供给管理员使用的接口
 * @author jaybill
 *
 */
public interface AdminService {
	/**
	 * 批量添加用户
	 * @param users
	 * @return
	 */
	 int addUsers(List<User> users,HttpSession session);
	 
	 /**
	  * 添加用户
	  * @param user
	  * @return
	  */
	 int addUser(User user);
	 
	 /**
	  * 创建查重工程
	  * @param proj
	  * @return
	  */
	 int addProject(Project proj);
	 
	 /**
	  * 添加模块
	  * @param module
	  * @return
	  */
	 int addModule(Module module);
	 
	 /**
	  * 将用户和工程相关联
	  * @param id
	  * @param pid
	  * @return
	  */
	 int addUserToProject(int id,int pid);
	 
	 /**
	  * 获取成员
	  * @param id
	  * @return
	  */
	 List<User> getMembers(int id);

	 /**
	  * 获取工程成员
	  * @param pid
	  * @return
	  */
	 List<User> getProjectMembers(int pid);
	 
	 /**
	  * 获取不存在项目中的用户
	  * @param leaderId
	  * @param projectId
	  * @return
	  */
	 List<User> getUsersByLeaderIDNotInProj(int leaderId,int projectId);
	 
	 /**
	  * 删除工程
	  * @param projectId
	  */
	 void deleteProject(int projectId);

	 /**
	  * 更新工程信息
	  * @param pro
	  */
	 void updateProject(Project pro);

	 /**
	  * 删除模块
	  * @param moduleId
	  */
	 void deleteModule(int moduleId);

	 /**
	  * 更新模块
	  * @param mo
	  */
	 void updateModule(Module mo);

	 /**
	  * 获取完全提交的用户，即目录下包含两个文件
	  * @param userId
	  * @param valueOf
	  * @return
	  */
	 List<User> getFinishModuleMembers(Integer userId,Integer projectId, Integer moduleId);

	 /**
	  * 没完全提交的学生列表
	  * @param userId
	  * @param valueOf
	  * @return
	  */
	 List<User> getPartModuleMembers(Integer userId, Integer projectId, Integer moduleId);

	 /**
	  * 删除用户
	  * @param users
	  * @param projectId
	  */
	 void deleteUsers(int[] users, int projectId);

	String addAdmin(User user);

	/**
	 * 获取管理员
	 * @param userId
	 * @return
	 */
	List<User> getAdmin(int userId);

}
