<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>查重结果</title>
<link rel="stylesheet" href="/css/bootstrap.min.css" />
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-xs-2">
				学生
			</div>
			<div>
				重复率
			</div>
		</div>
			<div>${res}</div>
			<c:forEach items="${strList}" var="name" varStatus="status">
				<div class="row">
					<div class="col-xs-2">
						${name}
					</div>
					<div class="col-xs-2">
						${doList[status.index]}
					</div>
				</div>
			</c:forEach>
	</div>
</body>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/bootstrap.min.js"></script>
</html>