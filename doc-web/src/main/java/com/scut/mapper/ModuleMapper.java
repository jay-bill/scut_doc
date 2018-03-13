package com.scut.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.scut.pojo.Module;

@Mapper
public interface ModuleMapper {
	int insertModule(Module module);

	List<Module> selectModules(int pid);
	
	Module selectModule(int id);

	void deleteModuleById(int moduleId);

	void updateModule(Module mo);

	void deleteModuleByPid(int projectId);
}
