<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>创建查重项目</title>
<link rel="stylesheet" href="/css/bootstrap.min.css" />
</head>
<body>
	<div>
		<h3>${projectInfo.name}</h3>
		<span>截止日期：${projectInfo.dateline }</span>
	</div>
	<br>
	<br>
	<hr>
	<form id='createModule' method='post' action="/admin/module">
		<label>项目名：</label><span>${moduleInfo.name}</span><br>
		<label>描述：</label><input type="text" name="description" value="${moduleInfo.description}"/><br>
		<input type="hidden" name="pid" value="${projectInfo.id}"/>
		<input type="hidden" name="_method" value="PUT">
		<label>截止日期：</label><input type="date" name="dateline" value="${moduleInfo.dateline}"/><br>
		<input type="hidden" name="moduleId" value="${moduleInfo.id}">
		<label>查重文件类型：</label>
		<select name="filetype" >
			<option selected = "selected" value="doc,docx">doc,docx</option>
		</select><br>
		<label>附件类型：</label>
		<select name="attachtype">
			<option selected = "selected" value="zip">zip</option>
		</select><br>
		<input type="hidden" name="${_csrf.parameterName }" 
			                        value="${_csrf.token }">
	</form>
	<button class="btn btn-info" type="submit" id="submitButton" onclick="submitForm()">更新</button>
</body>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/bootstrap.min.js"></script>
<script type="text/javascript">

	$("#submitButton").attr("disabled",true);
	
	function submitForm(){
		$("#createModule").submit();
	}
	
	function verify(){
		var name = $("#createModule input[name='name']").val();
		var dateline = $("#createModule input[name='dateline']").val().replace(/-/g,'');
		if(name!=''&&dateline!=''){		
			if(dateline!=''){
				var date = new Date();
				var month = date.getMonth()+1;
				if(month/10<1){
					month = "0"+month;
				}
				var day = date.getDate();
				if(day/10<1){
					day = "0"+day;
				}
				var datetime = date.getFullYear() + '' + month + '' + day;		
				if(dateline<datetime){
					$("#submitButton").attr("disabled", true);
				}else{
					$("#submitButton").attr("disabled",false);
				}
			}
		}
	}
	var counter = setInterval(verify,1000);
</script>
</html>