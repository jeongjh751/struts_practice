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
<style>
    body {
        font-family: Arial, sans-serif;
        margin: 20px;
    }
    table {
        border-collapse: collapse;
        width: 100%;
        margin-top: 20px;
    }
    th {
        background-color: #f0f0f0;
        padding: 10px;
        text-align: left;
    }
    td {
        padding: 8px;
    }
</style>
</head>
<body>
<h1>掲示板</h1>
<p><a href="boardInput.action">[新規投稿]</a></p>
<p>
    カテゴリ: 
    <a href="boardList.action">[全て]</a>
    <a href="boardList.action?category=自由">[自由]</a>
    <a href="boardList.action?category=お知らせ">[お知らせ]</a>
    <a href="boardList.action?category=質問">[質問]</a>
    <a href="boardList.action?category=設問">[設問]</a>
</p>
<hr/>
<!-- エラーメッセージ表示 -->
<s:actionerror/>
<!-- 投稿データテーブル -->
<table border="1">
<%
/*
 * ValueStackからdataを取得
 * - Actionクラスでdataがセットされていない場合
 * - Board.getChatData()を呼び出してデータを取得
 * - ValueStackにセット
 */
ValueStack stack = (ValueStack)request.getAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY);
if(stack!=null && stack.findValue("data")==null){
    stack.set("data", Board.getChatData());
}
%>

<!-- dataリストをループ処理 -->
<s:iterator value="data" status="stat">
    <!-- 最初の行のみヘッダーを表示 -->
    <s:if test="#stat.first">
    <tr>
        <th>No</th>
        <th>カテゴリ</th>
        <th>タイトル</th>
        <th>投稿者</th>
        <th>日時</th>
        <th>閲覧</th>
        <th>いいね</th>
        <th>よくない</th>
        <th>操作</th>
    </tr>
    </s:if>
    
    <!-- データ行 -->
    <!-- お知らせの場合は背景色を変更 -->
    <tr <s:if test="お知らせ"></s:if>>
        <td><s:property value="boardId"/></td>
        <!-- カテゴリ -->
		<td>
            <s:if test="category == 'お知らせ'">お知らせ
            </s:if>
            <s:else>
                <span class="category category-<s:property value='category'/>">
                    <s:property value="category"/>
                </span>
            </s:else>
        </td>
        <td>
            <!-- タイトルリンク -->
            <a href="boardDetail.action?boardId=<s:property value='boardId'/>">
                <s:property value="title"/>
            </a>
        </td>
        <!-- 投稿者 -->
        <td><s:property value="writer"/></td>
        <!-- 投稿日時 -->
        <td>
            <s:date name="createdAt" format="yyyy-MM-dd HH:mm"/>
        </td>
        <!-- 閲覧数 -->
        <td><s:property value="viewCount"/></td>
        <!-- いいね数 -->
        <td>
            <s:property value="likeCount"/>
        </td>
        <!-- コメント数 -->
        <td>
            <s:property value="commentCount"/>
        </td>
        <!-- 操作ボタン -->
        <td>
            <!-- 詳細ボタン -->
            <a href="boardDetail.action?boardId=<s:property value='boardId'/>">[詳細]</a>
            <!-- 編集ボタン -->
            <a href="boardEditForm.action?boardId=<s:property value='boardId'/>">[編集]</a>            
            <!-- 削除ボタン（確認ダイアログ付き） -->
            <a href="boardDelete.action?boardId=<s:property value='boardId'/>"
               onclick="return confirm('削除しますか？')">[削除]</a>
        </td>
    </tr>
</s:iterator>

<!-- データがない場合の表示 -->
<s:if test="data == null || data.isEmpty()">
    <tr>
        <td colspan="10" style="text-align: center; padding: 20px; color: #999;">
            投稿がありません
        </td>
    </tr>
</s:if>
</table>
</body>
</html>