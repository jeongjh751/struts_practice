<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@taglib prefix="s" uri="/struts-tags" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>掲示板 - 編集</title>
</head>
<body>
<h1>掲示板 - 編集</h1>

<p><a href="boardList.action">[一覧に戻る]</a></p>

<hr/>

<s:actionerror/>

<s:form action="boardEdit" method="post">
<!-- IDをhiddenで送信 -->
<input type="hidden" name="id" value="<s:property value='item.id'/>"/>

<table>
<tr>
    <th>タイトル:</th>
    <td><s:textfield name="title" size="50" maxlength="100"/></td>
</tr>
<tr>
    <th>名前:</th>
    <td><s:textfield name="name" size="20" maxlength="50"/></td>
</tr>
<tr>
    <th>本文:</th>
    <td><s:textarea name="message" rows="10" cols="60"/></td>
</tr>
<tr>
    <td colspan="2">
        <s:submit value="更新"/>
        <input type="button" value="キャンセル" onclick="location.href='boardList.action'"/>
    </td>
</tr>
</table>
</s:form>

</body>
</html>