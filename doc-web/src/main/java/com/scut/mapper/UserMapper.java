package com.scut.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.scut.pojo.User;

@Mapper
public interface UserMapper {
	
	int insertUser(User user);
	
	int insertUserRela(int stuId,int teaId);
	
	Integer selectUserId(String account);
	
	int selectRoleId(String code);
	
	int insertUserRole(int userId,int roleId);
	
	String selectUserPword(String account);
	
	int updatePword(String account,String newPword);
	
	List<User> selectUserByLeaderId(int leaderId);
	
	List<User> selectProjectUsers(int pid);
	
	List<User> selectUsersByLeaderIDNotInProj(int leaderId,int projectId);
	
	String selectUserName(int id);
	
	String selectAccount(int id);
	
	String selectRole(String account);
	
	User selectUserByAccount(String account);
	
	User selectUserById(int id);

	Integer selectRelation(int userId, int leaderId);

	List<User> selectAdmins(int userId);
}
