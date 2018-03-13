package com.scut.security.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.scut.pojo.User;

@Mapper
public interface LoginMapper {
	User findUser(String account);
}
