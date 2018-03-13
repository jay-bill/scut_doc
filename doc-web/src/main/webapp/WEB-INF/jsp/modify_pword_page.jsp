<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE>
<html xmlns="http://www.w3.org/1999/xhtml" >
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"> 
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0"> 
	<title>登录界面</title>
    <link href="/css/default.css" rel="stylesheet" type="text/css" />
	<!--必要样式-->
    <link href="/css/styles.css" rel="stylesheet" type="text/css" />
    <link href="/css/demo.css" rel="stylesheet" type="text/css" />
    <link href="/css/loaders.css" rel="stylesheet" type="text/css" />
    <link href="/layui/css/layui.css" rel="stylesheet" type="text/css" />
</head>
<body>
	<form class='login' action="password" id="form" method="post" enctype="application/x-www-form-urlencoded">
	  <input type="hidden" name="_method" value="PUT">
	  <div class='login_title'>
	    <span>首次登陆修改密码</span>
	  </div>
	  <div class='login_fields'>
	    <div class='login_fields__user'>
	      <div class='icon'>
	        <img alt="" src='img/user_icon_copy.png'>
	      </div>
	      <input name="oldPword" id="oldPword" placeholder='原密码' type='password' autocomplete="off" style="width:320px;"/>
	        <div class='validation'>
	          <img alt="" src='img/tick.png'>
	        </div>
	    </div>
	    <div class='login_fields__password'>
	      <div class='icon'>
	        <img alt="" src='img/lock_icon_copy.png'>
	      </div>
	      <input name="newPword" id="newPword" placeholder='新密码' type='password' autocomplete="off" style="width:320px;">
	      <div class='validation'>
	        <img alt="" src='img/tick.png'>
	      </div>
	    </div>
	    <div class='login_fields__password'>
	    	<div class='icon'>
		      <img alt="" src='img/key.png'>
		    </div>
	      <input name="code" id="confirmPword" placeholder='确认密码'  type='password' name="ValidateNum" autocomplete="off"
	      	style="width:320px;">
	    </div>
	    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
	    <div class='login_fields__submit'>
	      <input id='formSubmit' type="button" value='修改密码'>
	    </div>
	  </div>
	  <div class='disclaimer'>
	    <p>修改密码</p>
	  </div>
	</form>
</body>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script>

	var flag1 = false;
	var flag2 = false;
	
	$("#formSubmit").attr("disabled", true); 
	$("#formSubmit").css("cursor", "not-allowed"); 
	function getOldPword(value){
		$.ajax({
			url:"/verification/password",
			type:"get",
			data:{oldPword:value},
			dataType:"json",
			error:function(res){
				flag1 = false;
				flag2 = false;
				$("#newPwrdSpan").remove();
				$("#newSpan").remove();
				if($("#oldSpan").length<=0)
					$("#oldPword").after("<span id='oldSpan' style='color:red;padding-left:60px;'>请输入正确的原密码！</span>");
			},
			success:function(res){
				$("#oldSpan").remove();
				flag1 = true;
			}
		});
	}
	
	var validateOld = setInterval(function(){
		var oldPword = $("#oldPword").val();
		getOldPword(oldPword);
	},100);
	
	function validateNewPword(){
		if(!flag1)
			return;
		if($("#newPword").val().length>=6 && $("#newPword").val().length<=16){
			$("#newPwrdSpan").remove();
			//判断是否相等
			if($("#newPword").val()==$("#oldPword").val()){
				$("#newSpan").remove();
				flag2 = false;
				if($("#newPwrdSpan").length<=0)
					$("#newPword").after("<span id='newPwrdSpan' style='color:red; padding-left:60px;'>新密码不能和旧密码相同!</span>");
			}else{
				flag2 = true;
			}
		}else{
			$("#newSpan").remove();
			flag2 = false;
			if($("#newPwrdSpan").length<=0)
				$("#newPword").after("<span id='newPwrdSpan' style='color:red; padding-left:60px;'>密码长度在6~16之间</span>");	
		}
	}
	
	var validateNew = setInterval(function(){
		validateNewPword();
	},100);
	
	function validateSame(){
		if(!flag2){
			$("#formSubmit").attr("disabled", true); 
			$("#formSubmit").css("cursor", "not-allowed"); 
			return;
		}
		if($("#newPword").val()!=$("#confirmPword").val()){
			if($("#newSpan").length<=0)
				$("#confirmPword").after("<span id='newSpan' style='color:red; padding-left:60px;'>新密码不一致！</span>");	
			$("#formSubmit").attr("disabled", true); 
			$("#formSubmit").css("cursor", "not-allowed"); 
		}else{
			$("#newSpan").remove();
			$("#formSubmit").attr("disabled", false); 
			$("#formSubmit").css("cursor", "pointer"); 
		}
	}
	
	setInterval(function(){
		validateSame();
	},100);
	
	$("#formSubmit").click(function(){
		$("#formSubmit").attr("disabled",true);
		$("#formSubmit").css("cursor", "pointer"); 
		$("#form").submit();
		clearInterval(validateOld);
		setTimeout(function(){
			$("#formSubmit").attr("disabled",false);
			$("#formSubmit").css("cursor", "pointer");
		},1000);
	});
</script>
</html>