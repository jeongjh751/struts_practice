<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="model.Board"%>
<%@page import="com.opensymphony.xwork2.util.ValueStack"%>
<%@page import="org.apache.struts2.ServletActionContext"%>
<!DOCTYPE html>
<html>
<%@taglib prefix="s" uri="/struts-tags" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>掲示板 - 一覧</title>
</head>
<body>
<h1>掲示板</h1>

<p><a href="boardInput.action">[新規投稿]</a></p>

<hr/>

<s:actionerror/>

<table border="1">
<%
ValueStack stack = (ValueStack)request.getAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY);
if(stack!=null && stack.findValue("data")==null){
    stack.set("data", Board.getChatData());
}
%>
<s:iterator value="data" status="stat">
<s:if test="#stat.first">
<tr>
<th>No</th>
<th>タイトル</th>
<th>投稿者</th>
<th>日時</th>
<th>閲覧</th>
<th>操作</th>
</tr>
</s:if>
<tr>
<td><s:property value="id"/></td>
<td>
    <a href="boardDetail.action?id=<s:property value='id'/>">
        <s:property value="title"/>
    </a>
</td>
<td><s:property value="name"/></td>
<td><s:property value="postDate"/></td>
<td><s:property value="viewCount"/></td>
<td>
    <a href="boardDetail.action?id=<s:property value='id'/>">[詳細]</a>
    <a href="boardEditForm.action?id=<s:property value='id'/>">[編集]</a>
    <a href="boardDelete.action?id=<s:property value='id'/>" 
       onclick="return confirm('削除しますか？')">[削除]</a>
</td>
</tr>
</s:iterator>
</table>

</body>
</html>