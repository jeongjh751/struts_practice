<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@taglib prefix="s" uri="/struts-tags" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>掲示板 - 詳細</title>
<style>
    body {
        font-family: Arial, sans-serif;
        margin: 20px;
        max-width: 900px;
    }
    table {
        border-collapse: collapse;
        width: 100%;
        margin-top: 20px;
    }
    th {
        background-color: #f0f0f0;
        padding: 12px;
        text-align: left;
        width: 150px;
        vertical-align: top;
    }
    td {
        padding: 12px;
    }
    pre {
        white-space: pre-wrap;
        word-wrap: break-word;
        font-family: inherit;
        margin: 0;
    }
</style>
</head>
<body>
<h1>掲示板 - 投稿詳細</h1>
<p><a href="boardList.action">[一覧に戻る]</a></p>
<hr/>
<!-- エラーメッセージ表示 -->
<s:actionerror/>
<!-- 投稿詳細テーブル -->
<table border="1">
    <!-- ID -->
    <tr>
        <th>投稿番号</th>
        <td>
            <s:property value="item.boardId"/>
        </td>
    </tr>
    <!-- カテゴリ -->
    <tr>
        <th>カテゴリ</th>
        <td>
            <span class="category-badge category-<s:property value='item.category'/>">
                <s:property value="item.category"/>
            </span>
        </td>
    </tr>
    <!-- タイトル -->
    <tr>
        <th>タイトル</th>
        <td>
            <s:property value="item.title"/>
        </td>
    </tr>
    <!-- 投稿者 -->
    <tr>
        <th>投稿者</th>
        <td><s:property value="item.writer"/></td>
    </tr>
    <!-- 投稿日時 -->
    <tr>
        <th>投稿日時</th>
        <td>
            <s:date name="item.createdAt" format="yyyy年MM月dd日 HH:mm:ss"/>
            <!-- 編集情報 -->
            <s:if test="item.updatedAt != null">
                <div class="edited-info">
                    最終編集: <s:date name="item.updatedAt" format="yyyy年MM月dd日 HH:mm:ss"/>
                </div>
            </s:if>
        </td>
    </tr>
    <!-- IPアドレス -->
    <tr>
        <th>IPアドレス</th>
        <td><s:property value="item.ipAddress"/></td>
    </tr>
    <!-- 本文 -->
    <tr>
        <th>本文</th>
        <td>
            <pre><s:property value="item.content"/></pre>
        </td>
    </tr>
</table>
<hr/>
<!-- アクションボタン -->
<div class="action-buttons">
    <a href="boardList.action">一覧に戻る</a>
    <!-- 編集 -->
    <a href="boardEditForm.action?boardId=<s:property value='item.boardId'/>">編集</a>
    <!-- 削除 -->
    <a href="boardDelete.action?boardId=<s:property value='item.boardId'/>" 
       class="delete-btn"
       onclick="return confirm('本当に削除しますか？')">削除</a>
</div>
</body>
</html>