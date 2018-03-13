package com.scut.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.scut.security.service.LoginService;


@Configuration
//@EnableWebSecurity: 禁用Boot的默认Security配置，配合@Configuration启用自定义配置
//（需要扩展WebSecurityConfigurerAdapter）
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true): 启用Security注解，
//例如最常用的@PreAuthorize
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class LoginSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private LoginService loginService;
	
	@Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(loginService);
    }
	//configure(HttpSecurity): Request层面的配置
    //定义URL路径应该受到保护，哪些不应该
	@Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.authorizeRequests()
    	//允许访问的路径
    	.antMatchers("/**/*.html","/**/*.js","/**/*.css",
    			"/**/*.woff2","/**/*.woff","/**/*.jpg","/**/*.jpeg",
    			"/**/*.png","/**/*.tff")
    	.permitAll()
    	.antMatchers("/admin/**")
    	.access("hasRole('ADMIN')")// 含有admin的路径只有管理员可以访问
        .anyRequest().authenticated() //任何请求,登录后可以访问
        .and()
        .formLogin()
        .usernameParameter("account")
        .passwordParameter("password")
        .loginPage("/login")
        .defaultSuccessUrl("/home",true)//默认跳转页面
        .failureUrl("/login?error")
        .permitAll() //登录页面用户任意访问
        .and()
        .logout()
        .permitAll(); //注销行为任意访问
    }
}
