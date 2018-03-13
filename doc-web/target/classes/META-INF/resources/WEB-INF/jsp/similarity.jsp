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
			<label>最高重复率</label>
		</div>
		<div class="row">
			<div class="col-xs-2">
				学生A
			</div>
			<div class="col-xs-2">
				学生B
			</div>
			<div>
				最高重复率
			</div>
		</div>
			<c:forEach items="${bestSimilarityList}" var="similarity">
				<div class="row">
					<div class="col-xs-2">
						<a href="/admin/downloadDoc?mid=${moduleId}&&account=${similarity.account}">${similarity.id}</a>
					</div>
					<div class="col-xs-2">
						<c:forEach items="${similarity.sId}" var="names"  varStatus="status">
							<a href="/admin/downloadDoc?mid=${moduleId}&&account=${similarity.accountList[status.index]}">${names}、</a>
						</c:forEach>
					</div>
					<div class="col-xs-2">
						<c:forEach items="${similarity.similarity}" var="bestSimilarity">
							${bestSimilarity}
						</c:forEach>
					</div>
				</div>
			</c:forEach>
	</div>
</body>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/bootstrap.min.js"></script>
</html>