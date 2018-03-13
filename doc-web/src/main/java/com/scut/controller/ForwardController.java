package com.scut.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scut.pojo.Module;
import com.scut.service.UserService;

@Controller
@RequestMapping("forward")
public class ForwardController {

	@Autowired
	private UserService userService;
	
	@GetMapping("add_user_page")
	public String addUserPage(){
		return "add_user_page";
	}
	
	@GetMapping("create_project_page")
	public String createProjectPage(){
		return "create_project_page";
	}
	
	@GetMapping("create_module_page")
	public String createModulePage(@RequestParam("pid") int pid,
			Map<String,Object> map){
		map.put("projectInfo", userService.getProject(pid));
		return "create_module_page";
	}
	
	@GetMapping("edit_project_page")
	public String editProjectPage(@RequestParam("pid") int pid,
			Map<String,Object> map){
		map.put("projectInfo", userService.getProject(pid));
		return "edit_project_page";
	}
	
	@GetMapping("edit_module_page")
	public String editModulePage(int id,
			Map<String,Object> map){
		Module module = userService.getModule(id);
		map.put("moduleInfo",module);
		map.put("projectInfo", userService.getProject(module.getPid()));
		return "edit_module_page";
	}
	
	@GetMapping("root_login_page")
	public String rootLoginPage(){
		return "root_login_page";
	}
}
