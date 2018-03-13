package com.scut.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scut.exception.NameExisted;
import com.scut.mapper.ProjectMapper;
import com.scut.mapper.ModuleMapper;
import com.scut.mapper.UserMapper;
import com.scut.pojo.Module;
import com.scut.pojo.Project;
import com.scut.pojo.User;
import com.scut.service.AdminService;
import com.scut.utils.Consts;
import com.scut.utils.FileUtil;
import com.scut.utils.ProcessUtil;

@Service
public class AdminServiceImpl implements AdminService{

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private ModuleMapper moduleMapper;
	
	@Override
	public int addUsers(List<User> users,HttpSession session) {
		int sum=0;
		for(User user: users){		
			String account = user.getAccount();
			//密码为账号后六位
			user.setPassword(account.substring(account.length()-6));
			addUser(user);//添加
			sum++;
			//修改完成进度
			ProcessUtil putl = new ProcessUtil();
			putl.modifyProcess(session, Consts.EXCEL_USERS_PROC, users.size()*2);
		}
		return sum;
	}

	private boolean checkUserRelationExist(int userId,int leaderId){
		Integer id = userMapper.selectRelation(userId,leaderId);
		if(id!=null)
			return true;
		return false;
	}
	@Override
	@Transactional
	public int addUser(User user) {
		Integer userId = userMapper.selectUserId(user.getAccount());;
		//有可能添加失败，因为可能账号重复，先检查账号是否已经存在
		if(userId==null){
			userMapper.insertUser(user);
			//检查用户-角色表中，用户id是否存在。因为在添加的时候，每个用户分配一个权限，所以不用再检查
			userId = userMapper.selectUserId(user.getAccount());
			int roleId = userMapper.selectRoleId(Consts.ROLE_STUDENT);
			userMapper.insertUserRole(userId, roleId);		
		}	
		//无论账号是否已经存在，都需要和主动添加这些用户的管理员，关联起来。
		//判断user_relation中的关系是否已经建立
		if(!checkUserRelationExist(userId,user.getLeaderId())){
			userMapper.insertUserRela(userId, user.getLeaderId());
		}
		return 1;
	}

	@Override
	public int addProject(Project proj) {
		String path = proj.getPath();
		path = path.replaceAll(Consts.SEPARATOR, "/");
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}else{
			throw new NameExisted("名称已经存在，请修改！");
		}
		return projectMapper.insertProject(proj);
	}

	@Override
	public int addModule(Module module) {
		int pid = module.getPid();
		String path = projectMapper.selectProject(pid).getPath();
		String modulePath = path+Consts.SEPARATOR+module.getName();
		modulePath = modulePath.replaceAll(Consts.SEPARATOR, "/");
		File file = new File(modulePath);
		if(!file.exists()){
			file.mkdirs();
		}else{
			throw new NameExisted("项目名称已经存在，同一个查重工程不允许有重复名称，请修改！");
		}
		modulePath = modulePath.replaceAll("/", Consts.SEPARATOR);
		module.setPath(modulePath);
		return moduleMapper.insertModule(module);
	}

	@Override
	public int addUserToProject(int uid, int pid) {
		return projectMapper.insertProjectUser(pid, uid);
	}

	@Override
	public List<User> getMembers(int id) {
		return userMapper.selectUserByLeaderId(id);
	}

	@Override
	public List<User> getProjectMembers(int pid) {
		return userMapper.selectProjectUsers(pid);
	}

	@Override
	public List<User> getUsersByLeaderIDNotInProj(int leaderId, int projectId) {
		return userMapper.selectUsersByLeaderIDNotInProj(leaderId, projectId);
	}

	@Override
	@Transactional
	public void deleteProject(int projectId) {
		Project proj = projectMapper.selectProject(projectId);
		String path = proj.getPath();
		path = path.replaceAll(Consts.SEPARATOR, "/");
		File f = new File(path);
		FileUtil.delete(f);//删除文件夹
		projectMapper.deleteProjectUser(projectId);//先删除关联
		moduleMapper.deleteModuleByPid(projectId);
		projectMapper.deleteProject(projectId);//再是project
	}

	@Override
	public void updateProject(Project pro) {	
		projectMapper.updateProject(pro);
	}

	@Override
	public void deleteModule(int moduleId) {
		//1.先删除文件夹
		//获取模块路径
		Module module = moduleMapper.selectModule(moduleId);
		String path = module.getPath();
		path = path.replaceAll(Consts.SEPARATOR, "/");
		File f = new File(path);
		FileUtil.delete(f);
		//2.再删除module数据库
		moduleMapper.deleteModuleById(moduleId);
	}

	@Override
	public void updateModule(Module mo) {
		moduleMapper.updateModule(mo);
	}

	@Override
	public List<User> getFinishModuleMembers(Integer userId, Integer projectId, Integer moduleId) {
		return doFilter(userId, projectId, moduleId, 2);
	}

	@Override
	public List<User> getPartModuleMembers(Integer userId, Integer projectId, Integer moduleId) {
		return doFilter(userId, projectId, moduleId, 1);
	}

	
	/**
	 * type=2:getFinishModuleMembers;
	 * type=1:getPartModuleMembers;
	 * @param userId
	 * @param projectId
	 * @param moduleId
	 * @param type
	 * @return
	 */
	private List<User> doFilter(Integer userId, Integer projectId, Integer moduleId,int type){
		//根据projectId获取用户id，对比两个用户id
		Project pro = projectMapper.selectProject(projectId);
		List<User> list = new ArrayList<User>();
		if(pro!=null&&pro.getUid()==userId){
			//获取module
			Module mod = moduleMapper.selectModule(moduleId);
			if(mod!=null&&mod.getPid()==projectId){
				//获取module对应的路径
				String path = mod.getPath();
				path = path.replaceAll(Consts.SEPARATOR, "/");
				File f = new File(path);
				File [] files = f.listFiles();
				if(files==null){
					return list;
				}
				//计算文件夹下面有几个文件
				for(int i=0;i<files.length;i++){
					File curDir = files[i];
					//如果长度为2，说明已经提交
					File curFiles [] = curDir.listFiles();
					if(curFiles.length==type){
						String dirName = curDir.getName();
						String [] partInfo = dirName.split("-");
						User user = userMapper.selectUserByAccount(partInfo[0]);
						list.add(user);
					}
				}
			}
		}
		return list;
	}

	@Override
	public void deleteUsers(int[] users, int projectId) {
		for(int i=0;users!=null&&i<users.length;i++){
			projectMapper.deleteProjectUserRelation(users[i],projectId);
		}
	}

	@Override
	@Transactional
	public String addAdmin(User user) {
		try{
			userMapper.insertUser(user);
		}catch(Exception e){
			return "添加失败！账号已经存在";
		}
		int roleId = userMapper.selectRoleId(Consts.ROLE_ADMIN);
		int userId = userMapper.selectUserId(user.getAccount());
		userMapper.insertUserRole(userId, roleId);
		return null;
	}

	@Override
	public List<User> getAdmin(int userId) {
		return userMapper.selectAdmins(userId);
	}
}
