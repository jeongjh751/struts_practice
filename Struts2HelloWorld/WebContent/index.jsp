<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- Struts2のタグライブラリを使用可能にする -->
<%@ taglib prefix="s" uri="/struts-tags"%>
<!-- タイプ宣言はHTML5のものを使用する -->
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Hello掲示板</title>
	</head>
	<body>
	<%
	// アクセスしたら即座にboardList.actionへリダイレクト
	response.sendRedirect("boardList.action");
	%>
	</body>
</html>
