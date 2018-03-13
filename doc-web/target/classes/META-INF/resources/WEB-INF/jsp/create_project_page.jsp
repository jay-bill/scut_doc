<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>创建查重工程</title>
<link rel="stylesheet" href="/css/bootstrap.min.css" />
</head>
<body>
	<form id='createProject' method='post' action="/admin/project">
		<label>工程名：</label><input type="text" name="name"/><br>
		<label>截止日期：</label><input type="date" name="dateline"/>
		<input type="hidden" name="${_csrf.parameterName }" 
			                        value="${_csrf.token }">
	</form>
	<button class="btn btn-info" type="submit" id="submitButton" onclick="submitForm()">创建</button>
</body>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/bootstrap.min.js"></script>
<script type="text/javascript">

	$("#submitButton").attr("disabled",true);
	
	function submitForm(){
		var dateline = $("#createProject input[name='dateline']").val();
		if(dateline==''){
			$("#createProject input[name='dateline']").attr("disabled",true);
			alert($("#createProject input[name='dateline']").val());
		}
		$("#createProject").submit();
	}
	
	function verify(){
		var name = $("#createProject input[name='name']").val();
		var dateline = $("#createProject input[name='dateline']").val().replace(/-/g,'');
		if(name!=''){
			$("#submitButton").attr("disabled",false);			
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
				console.log(datetime+"----"+dateline);
				if(dateline<datetime){
					$("#submitButton").attr("disabled", true);
				}else{
					$("#submitButton").attr("disabled",false);
				}
			}
		}
	}
	var counter = setInterval(verify,100);
</script>
</html>