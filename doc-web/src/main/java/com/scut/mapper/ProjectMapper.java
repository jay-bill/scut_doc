package com.scut.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.scut.pojo.Project;

@Mapper
public interface ProjectMapper {
	
	int insertProject(Project proj);

	List<Project> selectUserProjects(int id);
	
	Project selectProject(int id); 
	
	int insertProjectUser(int pid,int uid);

	List<Project> selectUserJoinedProjects(int userId);

	void deleteProjectUser(int projectId);

	void deleteProject(int projectId);

	void updateProject(Project pro);
	
	Integer checkProjectIdandUserId(int userId,int pid);

	void deleteProjectUserRelation(int i, int projectId);
}
