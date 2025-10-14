<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@taglib prefix="s" uri="/struts-tags" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>掲示板 - 詳細</title>
</head>
<body>
<h1>掲示板 - 投稿詳細</h1>

<p><a href="boardList.action">[一覧に戻る]</a></p>

<hr/>

<table border="1">
<tr>
    <th>タイトル</th>
    <td><s:property value="item.title"/></td>
</tr>
<tr>
    <th>投稿者</th>
    <td><s:property value="item.name"/></td>
</tr>
<tr>
    <th>投稿日時</th>
    <td><s:property value="item.postDate"/></td>
</tr>
<tr>
    <th>IPアドレス</th>
    <td><s:property value="item.remoteAddress"/></td>
</tr>
<tr>
    <th>閲覧数</th>
    <td><s:property value="item.viewCount"/></td>
</tr>
<tr>
    <th>本文</th>
    <td><pre><s:property value="item.message"/></pre></td>
</tr>
</table>

<hr/>

<p>
<a href="boardList.action">[一覧に戻る]</a>
<a href="boardEditForm.action?id=<s:property value='item.id'/>">[編集]</a>
<a href="boardDelete.action?id=<s:property value='item.id'/>" 
   onclick="return confirm('削除しますか？')">[削除]</a>
</p>

</body>
</html>