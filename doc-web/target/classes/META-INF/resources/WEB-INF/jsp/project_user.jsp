<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>查重工程信息</title>
<link rel="stylesheet" href="/css/bootstrap.min.css" />
<style type="text/css">
	.projectClass{
		padding:20px 0px 20px 0px;
		border-top:1px solid #F4F5E3;
		border-left:1px solid #F4F5E3;
		border-right:1px solid #F4F5E3;
	}
	.projectClass:last-child{
		border-bottom:1px solid #F4F5E3;
	}
	
	.memberDiv{
		margin-top:20px;
		padding-top:50px;
		padding-bottom:50px;
	}
	
	.bottomDiv{
		height:150px;
		background-color:#f8f8f8;
		border-color:#e7e7e7;
	}
</style>
</head>
<body>
	<nav class="navbar navbar-default" role="navigation"> 
	    <div class="container-fluid"> 
	        <div class="navbar-header"> 
	            <a class="navbar-brand">${userInfo.name}</a> 
	        </div> 
	        <ul class="nav navbar-nav navbar-right"> 
	            <li><a href="javascript:;" onclick="logout()"><span class="glyphicon glyphicon-log-in"></span> 注销</a></li> 
	        </ul>
	        <ul class="nav navbar-nav navbar-right"> 
	            <li><a href="/home"><span class="glyphicon glyphicon-user"></span>首页</a></li> 
	        </ul>
	        <div style="display:none;">
	        	<form action="/logout" method="post" id="logoutForm">
	        		<input type="hidden" name="${_csrf.parameterName }" 
			                        value="${_csrf.token }">
	        	</form>
	        </div> 
	    </div> 
	</nav>
	
	<div class="container">
		<div class="row projectClass">
			<div class="col-xs-6" style="border-right:1px solid #F4F5E3;">
				<label>工程名:</label>${projectInfo.name}
			</div>
		</div>
	</div>
	
	<div class="container">
		<div class="row" >
			<div class="col-lg-12" style="margin:20px 0px 20px 0px;">
				<label>项目列表</label>
			</div>
		</div>
		<div class="row projectClass">
			<div class="col-lg-2">
				项目名
			</div>
			<div class="col-lg-2">
				截止日期
			</div>
			<div class="col-lg-2">
				描述
			</div>
			<div class="col-lg-2">
				查重文件|附件
			</div>
			<div class="col-lg-2">
				查重结果
			</div>
			<div class="col-lg-2">
				提交状态
			</div>
		</div>
		<c:forEach items="${modulesList}" var="module" varStatus="status">
			<div class="row projectClass">
				<div class="col-lg-2">
					<!-- 查看提交列表 -->
					<a href="/moduleInfo/${module.id}">${module.name }</a>
				</div>
				<div class="col-lg-2">
					${module.dateline}
				</div>
				<div class="col-lg-2">
					${module.description}
				</div>
				<div class="col-lg-2">
					${module.filetype}|${module.attachtype}
				</div>
				<div class="col-lg-2">
					<a href="/similarity/${module.path}">计算重复率</a>
				</div>
				<div class="col-lg-2">
					${submitResult[status.index]}
				</div>
			</div>
		</c:forEach>
	</div>
</body>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/bootstrap.min.js"></script>
<script type="text/javascript">
	function logout(){
		$("#logoutForm").submit();
	}
</script>
</html>