<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" href="/css/bootstrap.min.css" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>提交情况</title>
<style type="text/css">
	.projectClass{
		padding:20px 0px 20px 0px;
		border-top:1px solid #F4F5E3;
		border-left:1px solid #F4F5E3;
		border-right:1px solid #F4F5E3;
	}
	.projectClass:last-child{
		border-bottom:1px solid #F4F5E3;
		margin-bottom:50px;
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
		<div class="row" >
			<div class="col-lg-12">
				<label>已提交的用户：${finishModuleCount }人</label>
			</div>
		</div>
		<c:forEach items="${finishModule}" var="user">
			<div class="row projectClass">
				<div class="col-lg-4">
					${user.account }
				</div>
				<div class="col-lg-4">
					${user.name}
				</div>
				<div class="col-lg-4">
					${user.unit}
				</div>
			</div>
		</c:forEach>
	</div>
	
	<div class="container">
		<div class="row" >
			<div class="col-lg-12">
				<label>提交部分的用户：${partModuleCount }人</label>
			</div>
		</div>
		<c:forEach items="${partModule}" var="user">
			<div class="row projectClass">
				<div class="col-lg-4">
					${user.account }
				</div>
				<div class="col-lg-4">
					${user.name}
				</div>
				<div class="col-lg-4">
					${user.unit}
				</div>
			</div>
		</c:forEach>
	</div>
	
	<div class="container">
		<div class="row" >
			<div class="col-lg-12">
				<label>未提交的用户：${notModuleCount }人</label>
			</div>
		</div>
		<c:forEach items="${notModule}" var="user">
			<div class="row projectClass">
				<div class="col-lg-4">
					${user.account }
				</div>
				<div class="col-lg-4">
					${user.name}
				</div>
				<div class="col-lg-4">
					${user.unit}
				</div>
			</div>
		</c:forEach>
	</div>
	<div class="container">
		<button class="btn btn-block btn-info" onclick="window.location='/forward/edit_module_page?id=${moduleId}'">编辑项目</button>
	</div>
	<div class="bottomDiv">
		<div class="container">
			<p style="margin-top:40px;color:#66666;">五山校区地址：广州市天河区五山路381号 邮编：510641 大学城校区地址：广州市番禺区广州大学城 邮编：510006</p>
			<p style="margin-top:20px;color:#66666;">联系人，利同学。QQ：2399599130；电话：18813296645</p>
			<p style="margin-top:20px;margin-bottom:20px;color:#66666;">地址：华南理工大学五山校区北三学生宿舍</p>
		</div>
	</div>
</body>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/bootstrap.min.js"></script>
<script>
	function logout(){
		$("#logoutForm").submit();
	}
</script>
<script>
	$('input[id=excelFile]').change(function() {  
		$('#photoCover').val($(this).val());  
	});
</script>
<script>
//获取进度
function getProccess(){
	$.ajax({
		url:"/process/excelUsers",
		type:"get",
		dataType:"json",
		error:function(data){
			console.log("获取进度失败..."+data);
			$("#processExcel").text(data[0]);
		},
		success:function(data){
			$("#processExcel").html(data);
		}
	});
}

//上传excel并注册用户
$("#excelSubmit").click(function(){
	$("#excelSubmit").attr("disabled",true);
	upload();
});

//上传
var data;//回调的内容，即用户信息
function upload() {
	var formData = new FormData();
	formData.append('excelFile', $('#excelFile')[0].files[0]);
	formData.append("${_csrf.parameterName}","${_csrf.token}");//防止csrf攻击
	var xhr_provider = function() {
		var xhr = jQuery.ajaxSettings.xhr();
		return xhr;
	}; 
	var counter= setInterval(function(){
		getProccess();
	},100);
	$.ajax({
		url :"/admin/excelUsers",
		type : 'POST',
		cache : false,
		data : formData,
		dataType:"json",
		processData: false,
		contentType : false,
		xhr : xhr_provider,
		success : function(result) {
		  	//清空计时器
            clearInterval(counter);
            alert("上传完成!");
            $("#processExcel").html("<a href='javascript:void(0)' onclick='showExcelUsers()'>显示所有新增用户</a>")
			data = result;
		},
		error : function(data) {
			alert("上传文件出错了！");
			clearInterval(counter);//清空计时器
			$("#processExcel").html("");
		}
	})
}

//显示所有用户
function showExcelUsers(){
	$("#excelUsers").remove();
	$("body").append("<table style='position=fixed;background:#fef' border='1' cellspacing='0' id='excelUsers'></table>");
	$("#excelUsers").append("<tr><td>账号</td><td>姓名</td><td>单位</tr>");
	for(var i=0;i<data.length;i++){
		$("#excelUsers").append("<tr><td>"+data[i].account+"</td><td>"+data[i].name+"</td><td>"+data[i].unit+"</td></tr>")
	}
	$("#excelUsers").append("<tr><td><button onclick='closePan()'>关闭</button></td></tr>");
}
function closePan(){
	$("#excelUsers").remove();
}
</script>
</html>