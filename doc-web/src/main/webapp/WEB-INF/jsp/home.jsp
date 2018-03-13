<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" href="/css/bootstrap.min.css" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>首页</title>
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
	            <a class="navbar-brand">欢迎,${userInfo.name}！</a> 
	        </div> 
	        <ul class="nav navbar-nav navbar-right"> 
	            <li><a href="javascript:;" onclick="logout()"><span class="glyphicon glyphicon-log-in"></span> 注销</a></li> 
	        </ul>
	        <div style="display:none;">
	        	<form action="/logout" method="post" id="logoutForm">
	        		<input type="hidden" name="${_csrf.parameterName }" 
			                        value="${_csrf.token }">
	        	</form>
	        </div> 
	    </div> 
	</nav>
	<nav class="navbar navbar-default" role="navigation" style="background:#44cef6"> 
   		<div style="position:fixed;width:100%;">
   			<div id="tag1" style="line-height:50px;color:white;font-size:16px;">
	   			各位同学注意，此页面的截止时间仅供参考，具体截止时间详见对应项目
	   		</div>
	   		<div id="tag2" style="line-height:50px;color:white;font-size:16px;">
	   			各位同学注意，此页面的截止时间仅供参考，具体截止时间详见对应项目
	   		</div>
   		</div>
	</nav>
	<div class="container">
		<div class="row" >
			<div class="col-lg-12" style="margin-bottom:20px;">
				<h4>加入的工程列表</h4>
			</div>
		</div>
		<div class="row">
			<div class="col-lg-3">
				<label>工程名</label>
			</div>
			<div class="col-lg-3">
				<label>截止时间</label>
			</div>
			<div class="col-lg-3">
				<label>指导老师</label>
			</div>
			<div class="col-lg-3">
				<label>所属单位</label>
			</div>
		</div>
		<c:forEach items="${projects}" var="project" varStatus="status">
			<div class="row projectClass">
				<div class="col-lg-3">
					<a href="/projectInfo/${project.id }">${project.name }</a>
				</div>
				<div class="col-lg-3">
					${project.dateline}
				</div>
				<div class="col-lg-3">
					${teacherList[status.index].name}
				</div>
				<div class="col-lg-3">
					${teacherList[status.index].unit}
				</div>
			</div>
		</c:forEach>
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
<script type="text/javascript">
	$(document).ready(function(){
		var screenWidth = $(window).width();
		var left = screenWidth/2;
		$("#tag1").css({
			"position":"absolute",
			"left":left
		});
		left = left + screenWidth;
		$("#tag2").css({
			"position":"absolute",
			"left": left
		});
		setTimeout(function(){
			//开始滑动
			setInterval(function(){
				$("#tag1").css("position","absolute");
				var tmp = $("#tag1").css("left");
				if(parseInt(tmp)<-500){
					var screenWidth = $(window).width();
					screenWidth = 2*screenWidth-500;
					$("#tag1").css("left",screenWidth);
				}else{
					var tmpLeft = parseInt(tmp)-1;
					$("#tag1").css("left",tmpLeft);
				}				
			},20);
			
			//开始滑动
			setInterval(function(){
				$("#tag2").css("position","absolute");
				var tmp = $("#tag2").css("left");
				if(parseInt(tmp)<-500){
					var screenWidth = $(window).width();
					screenWidth = 2*screenWidth-500;
					$("#tag2").css("left",screenWidth);
				}else{
					var tmpLeft = parseInt(tmp)-1;
					$("#tag2").css("left",tmpLeft);
				}				
			},20);
		},1000);
		
	});
</script>
</html>