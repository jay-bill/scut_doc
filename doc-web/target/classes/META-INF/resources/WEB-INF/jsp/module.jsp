<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>项目信息</title>
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
</style>
</head>
<body>

	<nav class="navbar navbar-default" role="navigation"> 
	    <div class="container-fluid"> 
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
		<div class="row" style="margin-top:50px;">
			<div class="col-xs-6" style="border-right:1px solid #F4F5E3;">
				<label>项目信息</label>
			</div>
		</div>
	</div>
	<div class="container">
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
		</div>
	</div>
	<div class="container">
		<div class="row projectClass">
			<div class="col-lg-2">
				<!-- 查看提交列表 -->
				<a>${module.name }</a>
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
		</div>
	</div>
	
	<div class="container">
		<div class="row" style="margin-top:50px;">
			<label>您已经提交了这些文件：</label>
		</div>	
		<c:forEach items="${files}" var="file" varStatus="status">
			<div class="row" style="color:#666">${status.index+1}、${file}</div>
		</c:forEach>
	</div>
	<div class="container" style="margin-top:50px;">
		<div class="row">
			<label>文档（doc、docx）：</label>
			<input type="file" id="wordFile" name='wordFile'/>
			<a href='javascript:;' onclick="uploadFile('wordFile',0)">确认</a>
		</div>
		<br>
		<div class="row">
			<label>附件（zip）：</label>
			<input type="file" id="attachFile" name='attachFile'/>
			<a href='javascript:;' onclick="uploadFile('attachFile',1)">确认</a>
		</div>
	</div>
</body>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/bootstrap.min.js"></script>
<script>
	var data;
	function uploadFile(val,type){
		var $this = $(this);
		$this.attr("disabled",true);
		var id = "#"+val;
		var formData = new FormData();
		formData.append("file", $(id)[0].files[0]);
		formData.append("type", type);
		formData.append("moduleId",${module.id});
		formData.append("${_csrf.parameterName}","${_csrf.token}");//防止csrf攻击
		var xhr_provider = function() {
			var xhr = jQuery.ajaxSettings.xhr();
			return xhr;
		}; 
		$.ajax({
			url :"/file",
			type : 'POST',
			cache : false,
			data : formData,
			dataType:"json",
			processData: false,
			contentType : false,
			xhr : xhr_provider,
			success : function(result) {
			  	//清空计时器
	            alert("上传完成!");
				data = result;
				//重新刷新页面
				window.location.reload();
			},
			error : function(data) {
				var json = JSON.parse(data.responseText);
				alert(json.message);
				//重新刷新页面
				window.location.reload();
			}
		})
	}
	
	function logout(){
		$("#logoutForm").submit();
	}
</script>
</html>