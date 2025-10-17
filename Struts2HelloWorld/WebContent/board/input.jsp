<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@taglib prefix="s" uri="/struts-tags" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>掲示板 - 新規投稿</title>
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
    .file-info {
    font-size: 0.9em;
    color: #666;
    margin-top: 5px;
	}
	input[type="file"] {
	    padding: 5px;
	}
</style>
</head>
<body>
<h1>掲示板 - 新規投稿</h1>

<p><a href="boardList.action">[一覧に戻る]</a></p>

<hr/>

<s:actionerror/>

<s:form action="boardCreate" method="post" enctype="multipart/form-data">
    <%
    // IPアドレス取得
    String ipAddress = request.getRemoteAddr();
    
    // IPv6 localhost を IPv4 に変換
    if ("::1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
        ipAddress = "127.0.0.1";
    }
    %>
    
    <input type="hidden" name="ipAddress" value="<%= ipAddress %>"/>
    
    <table>
        <tr>
            <th>カテゴリ:</th>
            <td>
				<s:select name="category" 
				          list="#{'自由':'自由','お知らせ':'お知らせ','質問':'質問','設問':'設問'}"
				          value="'自由'"/>
            </td>
        </tr>
        <tr>
            <th>タイトル:</th>
            <td><s:textfield name="title" size="50" maxlength="90"/></td>
        </tr>
        <tr>
            <th>名前:</th>
            <td><s:textfield name="writer" size="20" maxlength="50"/></td>
        </tr>
        <tr>
            <th>本文:</th>
            <td><s:textarea name="content" rows="10" cols="60"/></td>
        </tr>
         <tr>
        <th>ファイル:</th>
        <td>
            <s:file name="upload" 
                    accept="image/*,.pdf,.doc,.docx,.xls,.xlsx,.txt"/>
            <div class="file-info">
                ※ 最大10MB、画像・PDF・Word・Excel・テキストファイル対応
            </div>
        </td>
        </tr>
        <tr>
            <td colspan="2">
                <s:submit value="投稿"/>
                <input type="button" value="キャンセル" onclick="location.href='boardList.action'"/>
            </td>
        </tr>
    </table>
</s:form>
<hr/>
</body>
</html>