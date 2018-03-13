<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>欢迎登录！</title>
<link rel="stylesheet" href="/css/bootstrap.min.css" />
</head>
<body>
	<form action="/admin/addAdmin" method="post">
		<label>账号：</label><input type="text" name="account"><br>
		<label>姓名：</label><input type="text" name="name"><br>
		<label>单位：</label><input type="text" name="unit"><br>
		<input type="hidden" name="${_csrf.parameterName }" value="${_csrf.token }">
		<input type="submit">
	</form>
</body>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/bootstrap.min.js"></script>
</html>