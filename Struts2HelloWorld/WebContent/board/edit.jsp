<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@taglib prefix="s" uri="/struts-tags" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>掲示板 - 編集</title>
<style>
    body {
        font-family: Arial, sans-serif;
        margin: 20px;
    }
    table {
        border-collapse: collapse;
    }
    th {
        text-align: right;
        padding: 8px;
        vertical-align: top;
    }
    td {
        padding: 8px;
    }
</style>
</head>
<body>
<h1>掲示板 - 編集</h1>

<!-- 一覧に戻るリンク -->
<p><a href="boardList.action">[一覧に戻る]</a></p>

<hr/>

<!-- エラーメッセージ表示 -->
<s:actionerror/>
<s:form action="boardEdit" method="post">
    <s:hidden name="boardId" value="%{item.boardId}"/>
    <table>
        <tr>
            <th>カテゴリ:</th>
            <td>
                <s:select name="category" 
                          list="#{'自由':'自由','お知らせ':'お知らせ','質問':'質問','設問':'設問'}"
                          value="%{item.category}"/>
            </td>
        </tr>
        
        <!-- タイトル -->
        <tr>
            <th>タイトル:</th>
            <td>
                <s:textfield name="title" size="50" maxlength="90" value="%{item.title}"/>
            </td>
        </tr>
		
		<!-- 投稿者 -->
        <tr>
            <th>名前:</th>
            <td>
                <s:textfield name="writer" size="20" maxlength="50" value="%{item.writer}"/>
            </td>
        </tr>
  
        <!-- 本文 -->
        <tr>
            <th>本文:</th>
            <td>
                <s:textarea name="content" rows="10" cols="60" value="%{item.content}"/>
            </td>
        </tr>
        
        <!-- ボタン -->
        <tr>
            <td colspan="2">
                <s:submit value="更新"/>
                <input type="button" value="キャンセル" 
                       onclick="location.href='boardList.action'"/>
            </td>
        </tr>
    </table>
</s:form>
</body>
</html>