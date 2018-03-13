package com.scut.security.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.scut.pojo.User;
import com.scut.security.mapper.LoginMapper;

/**
 * 登录服务，是springsecurity判断的依据
 * @author jaybill
 *
 */
@Service
public class LoginService implements UserDetailsService{
	@Autowired
	private LoginMapper loginMapper;
	@Override
	public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
		User user = loginMapper.findUser(account);
		System.out.println(user);
		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        if( user == null ){
            throw new UsernameNotFoundException(String.format("User with username=%s was not found", account));
        }
        authorities.add(new SimpleGrantedAuthority(user.getRole().getCode()));
		return new org.springframework.security.core.userdetails.User(user.getAccount(), 
				user.getPassword(), authorities);
	}

}
