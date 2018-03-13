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
			<div class="col-xs-3" style="border-right:1px solid #F4F5E3;">
				<label>工程名</label>
			</div>
			<div class="col-xs-3" style="border-right:1px solid #F4F5E3;">
				<label>截止日期</label>
			</div>
			<div class="col-xs-3" style="border-right:1px solid #F4F5E3;">
				<label>编辑</label>
			</div>
			<div class="col-xs-3">
				<label>查重项</label>
			</div>
		</div>
		<div class="row projectClass">
			<div class="col-xs-3" style="border-right:1px solid #F4F5E3;">
				${projectInfo.name }
			</div>
			<div class="col-xs-3" style="border-right:1px solid #F4F5E3;">
				<c:set var="flag" value="0"></c:set>
				<c:if test="${projectInfo.dateline eq null}">
					<c:set var="flag" value="1"></c:set>
					见具体项目
				</c:if>
				<c:if test="${flag eq 0}">
					${projectInfo.dateline}
				</c:if>
			</div>
			<div class="col-xs-3" style="border-right:1px solid #F4F5E3;">
				<a href="/forward/edit_project_page?pid=${projectInfo.id }">编辑</a>
			</div>
			<div class="col-xs-3">
				<a href="/forward/create_module_page?pid=${projectInfo.id}">新增查重项</a>
			</div>
		</div>
	</div>
	
	<div class="container">
		<div class="row memberDiv">
			<div class="col-xs-6" style="text-align:center">
				<a href="javascript:;" onclick="getProjectMembers()">
					<button type="button" class="btn btn-default btn-info btn-lg btn-block" style="font-size:14px">
						查看已加入本工程成员
					</button>
				</a>
			</div>
			<div class="col-xs-6" style="text-align:center">
				<a href="javascript:;" onclick="addUsers()">
					<button type="button" class="btn btn-default btn-info btn-lg btn-block" style="font-size:14px">
						添加未加入本工程成员
					</button>
				</a>
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
				操作
			</div>
		</div>
		<c:forEach items="${modulesList}" var="module">
			<div class="row projectClass">
				<div class="col-lg-2">
					<!-- 查看提交列表 -->
					<a href="/admin/moduleResult/pid/${projectInfo.id}/mid/${module.id}">${module.name }</a>
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
					<a href="/admin/computeSimilarity?path=${module.path}&&id=${module.id}">计算重复率</a>
				</div>
				<div class="col-lg-2">
					<a href="javascript:if(confirm('确定要删除吗？')) location='/admin/deleteModule/pid/${projectInfo.id}/mid/${module.id}'">删除</a>
				</div>
			</div>
		</c:forEach>
	</div>
	
	<div class="container" style="margin-top:20px;margin-bottom:50px;">
		<div class="row" >
			<div class="col-lg-12" style="margin:20px 0px 20px 0px;">
				<label>打包下载</label>
			</div>
		</div>
		<div class="row">
			<form class="col-xs-6" style="text-align:center" id="download1" action="/admin/download" method="get">
				<input type="hidden" name="type" value="module">
				<input type="hidden" name="hostId" value="${projectInfo.uid}">
				<input type="hidden" name="path" value="${projectInfo.path}">
				<button type="button" class="btn btn-default btn-info btn-lg btn-block" style="font-size:14px"
					onclick="download('1')">
					按项目分类打包下载
				</button>
			</form>
			<form class="col-xs-6" style="text-align:center" id="download2" action="/admin/download" method="get">
				<input type="hidden" name="type" value="user">
				<input type="hidden" name="hostId" value="${projectInfo.uid}">
				<input type="hidden" name="dirName" value="${projectInfo.name}">
				<button type="button" class="btn btn-default btn-info btn-lg btn-block" style="font-size:14px"
					onclick="download('2')" disabled="disabled">
					按用户分类打包下载
				</button>
			</form>
		</div>
	</div>
	<div id="addUsersList" 
		style="border:3px #999 solid;border-radius:8px;left:0px;top:50px;display:none;position:absolute;z-index:100;background:#666;width:500px;height:550px;overflow:auto;">
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
<script type="text/javascript">
	function addUsers(){
		//获取该老师的用户信息
		getUsers();
	}
	
	function getUsers(){
		var teacherId = ${projectInfo.uid};
		var projectId = ${projectInfo.id};
		$.ajax({
			url:"/admin/getUsersByLeaderIDNotInProj",
			type:"get",
			data:{lid:teacherId,pid:projectId},
			dataType:"json",
			error:function(err){
				alert("无权限查看！");
			},
			success:function(users){
			 	var part = "<div style='position:fixed;'><a href='javascript:;' onclick='deleteDiv()'>关闭</a>&nbsp;&nbsp;<a href='javascript:;' onclick='selectAll()'>全选</a>&nbsp;&nbsp;<a href='javascript:;' onclick='avoidSelectAll()'>取消全选</a></div>"+
					"<table border='8'>"+
						"<caption align='top' style='color:white;'>选择用户添加到查重工程</caption>"+
						"<tr>"+
							"<td>学号</td><td>姓名</td><td>单位</td><td>选中</td>"+
						"</tr>"+
					"</table>";
					$("#addUsersList").append(part); 	
				var i=0;
				for(;i<users.length;i++){
					var tr = "<tr><td>"+users[i].account+"</td><td>"+users[i].name+"</td><td>"+users[i].unit+"</td><td>是：<input class='selected' type='radio' name='"+users[i].id+"'>否：<input class='noselected' type='radio' name='"+users[i].id+"' checked='checked'/><input type='hidden' value='"+users[i].id+"'/></td></tr>";
					$("#addUsersList table").append(tr);
				}
				$("#addUsersList table").append("<tr><td><button onclick='addUser(this)'>确认添加</button></td></tr>");
				$("#addUsersList").css("display","block");
				$("#addUsersList table").css("background","white");
			}
		});
	}
	
	function getProjectMembers(){
		var projectId = ${projectInfo.id};
		$.ajax({
			url:"/admin/project/{pid}/members",
			type:"get",
			data:{pid:projectId},
			dataType:"json",
			error:function(err){
				alert("无权限查看！");
			},
			success:function(users){
				console.log(users);
				var part = "<div style='position:fixed;'><a href='javascript:;' onclick='deleteDiv()'>关闭</a>&nbsp;&nbsp;<a href='javascript:;' onclick='deleteAllUsers()'>全选</a>&nbsp;&nbsp;<a href='javascript:;' onclick='noDeleteAllUsers()'>取消全选</a></div>"+
				"<table border='8'>"+
					"<caption align='top' style='color:white;'>查重工程的用户</caption>"+
					"<tr>"+
						"<td>学号</td><td>姓名</td><td>单位</td><td>删除</td>"+
					"</tr>"+
				"</table>";
				$("#addUsersList").append(part); 	
				for(var i=0;i<users.length;i++){
					var tr = "<tr><td>"+users[i].account+"</td><td>"+users[i].name+"</td><td>"+users[i].unit+"</td><td><input class='usersRadio' type='radio' name='"+users[i].id+"' value='"+users[i].id+"'></td></tr>";
					$("#addUsersList table").append(tr);
				}
				$("#addUsersList table").append("<tr><td></td><td></td><td></td><td><a href='javascript:;' onclick='deleteUsers(this)'>删除</a></td></tr>");
				$("#addUsersList").css("display","block");
				$("#addUsersList table").css("background","white");
			}
		});
	}
	
	function deleteDiv(){
		$("#addUsersList").css("display","none");
		$("#addUsersList").html("");
	}
	
	function deleteAllUsers(){
		$(".usersRadio").prop("checked",true);
	}
	
	function noDeleteAllUsers(){
		$(".usersRadio").prop("checked",false);
	}
	
	function selectAll(){
		$(".selected").prop("checked",true);
	}
	
	function avoidSelectAll(){
		$(".noselected").prop("checked",true);
	}
	
	function addUser(id){
		var $this = $(id);
		$this.attr("disabled",true);
		var projectId = ${projectInfo.id};
		//获取选中的用户
		$(".selected:checked").each(function(){
			var $this = $(this);
			var id = $this.parent().children("input:last-child").val();
			$.ajax({
				url:"/admin/project/user",
				type:"post",
				data:{uid:id,pid:projectId,${_csrf.parameterName}:'${_csrf.token}'},
				dataType:"json",
				error:function(err){
					
				},
				success:function(msg){
					console.log(msg);
				}
			});
		});
	}
	
	
	function download(type){
		if(type=='1')
			$("#download1").submit();
		else if(type=='2')
			$("#download2").submit();
	}
	
	function logout(){
		$("#logoutForm").submit();
	}
	
	function getSimilarity(path){
		$.ajax({
			url:"/computeSimilarity",
			data:{path:path},
			dataType:"json",
			error:function(){
				alert("错误了！");
			},
			success:function(){
				alert("成功了！");
			}
		});
	}
	
	function deleteUsers(thisA){
		var a = $(thisA);
		a.removeAttr("onclick");
		var delusers = [];
		$(".usersRadio:checked").each(function(){
			var $this = $(this);
			var val = $this.val();
			delusers.push(val);
		});
		$.ajax({
			url:"/admin/users",
			type:"post",
			data:{users:delusers,projectId:'${projectInfo.id}',${_csrf.parameterName }:'${_csrf.token }'},
			dataType:"json",
			error:function(err){
				var json = JSON.parse(err.responseText);
				alert("删除用户出错："+json.message);
				window.location.reload();
			},
			success:function(data){
				alert("删除成功"+data);
				window.location.reload();
			}
		});
	}
</script>
</html>