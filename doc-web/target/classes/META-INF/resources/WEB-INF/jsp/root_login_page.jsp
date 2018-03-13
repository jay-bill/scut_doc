<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>root密码</title>
<link rel="stylesheet" href="/css/bootstrap.min.css" />
</head>
<body>
	<div>请输入root密码：</div>
	<form action="/admin/root" method="post">
		<input type="password" name="rootPassword">
		<input type="hidden" name="${_csrf.parameterName }" 
			                        value="${_csrf.token }">
		<input type="submit">
	</form>
</body>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/bootstrap.min.js"></script>
</html>