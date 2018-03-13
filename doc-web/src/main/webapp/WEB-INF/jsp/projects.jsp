<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>查重工程列表</title>
</head>
<body>
	<c:forEach items="${projectsList}" var="project">
		<div>
			<div>
				<a href="/projectInfo?pid=${project.id}">${project.name}</a>
			</div>
			<div>
				截止日期：
				<c:set var="flag" value="0"></c:set>
				<c:if test="${project.dateline ne null}">
					<c:set var="flag" value="1"></c:set>
					${project.dateline}
				</c:if>
				<c:if test="${flag eq 0}">
					<span style="color:#666">(详见具体项目)</span>
				</c:if>
			</div>
		</div>
		<br>
	</c:forEach>
</body>
</html>