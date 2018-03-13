<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>添加学生</title>
<link rel="stylesheet" href="/css/bootstrap.min.css" />
<style type="text/css">
	#kuang{
		border:2px solid #e7e7e7;
		padding:50px;
		border-radius:6px;
		margin-bottom:50px;
	}
	
	#right{
		border-left:1px solid #e7e7e7;
	}
</style>
</head>
<body>
	<nav class="navbar navbar-default" role="navigation"> 
	    <div class="container-fluid"> 
	        <div class="navbar-header"> 
	            <a class="navbar-brand">文档</a> 
	        </div> 
	        <ul class="nav navbar-nav navbar-right"> 
	            <li><a href="javascript:;" onclick="logout()" ><span class="glyphicon glyphicon-log-in"></span> 注销</a></li> 
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
	<div></div>
	<div class="container" id="kuang">
		<div class="row">
			<div class="col-sm-1"></div>
			<div class="col-sm-5">
				<form class="form-horizontal addUserForm" action="/admin/user" role="form" id="submitform" method="post" style="margin-top:50px;">
					<div class="form-group">
						<label for="firstname" class="col-sm-2 control-label">账号</label>
						<div class="col-sm-10">
							<input type="text" class="form-control" id="account" name="account"
								   placeholder="请输入账号">
						</div>
					</div>
					
					<div class="form-group">
						<label for="lastname" class="col-sm-2 control-label">姓名</label>
						<div class="col-sm-10">
							<input type="text" class="form-control" id="name" name="name"
								   placeholder="请输入姓名">
						</div>
					</div>
					
					<div class="form-group">
						<label for="lastname" class="col-sm-2 control-label">单位</label>
						<div class="col-sm-10">
							<input type="text" class="form-control" id="unit" name="unit"
								   placeholder="请输入单位">
							<input type="hidden" name="${_csrf.parameterName }" value="${_csrf.token }">	   
						</div>
					</div>
				</form>
				<div>
					<button id="submitButton" class="btn btn-info" disabled="disabled" onclick="submitForm()" style="margin-left:18px;;cursor:not-allowed;">提交</button>
				</div>
			</div>
			<div class="col-sm-1"></div>
			<div class="col-sm-5" id="right">
				<p>说明：</p>
				<p>1、账号只能为数字，长度为6到16</p>
				<p>2、姓名只能是汉字或者字母组合数字</p>
				<p>3、单位只能包含字母、数字、汉字、-、_符号</p>
				<p>4、添加的账号如果已经存在系统中，那么不会覆盖该用</p>
				<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;户信息，只会把添加者和该用户信息建立联系</p>
				<p>5、如果需要修改该用户信息，请联系管理员</p>
				<p>6、以上第4第5条限定会在以后的版本中改进</p>
			</div>
		</div>
	</div>
</body>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/bootstrap.min.js"></script>
<script type="text/javascript">
	setInterval(function(){
		validate();
	},100);
	function validate(){
		if($("#account").val().length<6||$("#account").val().length>12){
			$("#submitButton").attr("disabled",true);
			$("#submitButton").css("cursor","not-allowed");
			return;
		}
		if(!$("#account").val().match(/[0-9]{6,12}/g)){
			$("#submitButton").attr("disabled",true);
			$("#submitButton").css("cursor","not-allowed");
			return;
		}
		if($("#name").val().length<2||$("#name").val().length>50){
			$("#submitButton").attr("disabled",true);
			$("#submitButton").css("cursor","not-allowed");
			return;
		}
		var url = new RegExp("^[\\u4e00-\\u9fa5]{2,50}$");
		if(!$("#name").val().match(/[0-9a-zA-Z]{2,50}/g)&&!$("#name").val().match(url)){
			$("#submitButton").attr("disabled",true);
			$("#submitButton").css("cursor","not-allowed");
			return;
		}
		
		var unit = new RegExp("^[\u4E00-\u9FA5A-Za-z0-9_-]{2,50}$");
		if(!$("#unit").val().match(unit)){
			$("#submitButton").attr("disabled",true);
			$("#submitButton").css("cursor","not-allowed");
			return;
		}
		$("#submitButton").attr("disabled",false);
		$("#submitButton").css("cursor","pointer");
	}
	function logout(){
		$("#logoutForm").submit();
	}
	function submitForm(){
		var form = $("#submitform");
		$.ajax({
			url:"/admin/user",
			type:"post",
			data:form.serialize(),
			error:function(data){
				var json = JSON.parse(data.responseText);
				alert("出错了:"+json.message);
			},
			success:function(data){
				alert("添加成功");
				//重新刷新页面
				window.location.reload();
			}
		});
	}
	
	//高度
	var width = parseInt($("#kuang").width());
	$("#kuang").css("height",width*0.55);
	var height = parseInt($("#kuang").height());
	$("#right").css("height",height)
</script>
</html>