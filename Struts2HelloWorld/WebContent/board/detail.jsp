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
    
    /* コメントセクションのスタイル */
    .comment-section {
        margin-top: 40px;
        padding-top: 20px;
        border-top: 2px solid #ddd;
    }
    .comment-form {
        background-color: #f9f9f9;
        padding: 20px;
        border-radius: 5px;
        margin-bottom: 30px;
    }
    .comment-form input[type="text"],
    .comment-form textarea {
        width: 100%;
        padding: 8px;
        margin-bottom: 10px;
        border: 1px solid #ddd;
        border-radius: 3px;
        box-sizing: border-box;
    }
    .comment-form textarea {
        resize: vertical;
        min-height: 80px;
    }
    .comment-form button {
        background-color: #4CAF50;
        color: white;
        padding: 10px 20px;
        border: none;
        border-radius: 3px;
        cursor: pointer;
    }
    .comment-form button:hover {
        background-color: #45a049;
    }
    
    /* コメントリストのスタイル */
    .comment-list {
        margin-top: 20px;
    }
    .comment-item {
        background-color: #fff;
        border: 1px solid #ddd;
        padding: 15px;
        margin-bottom: 15px;
        border-radius: 5px;
    }
    .comment-reply {
        margin-left: 40px;
        background-color: #f5f5f5;
        border-left: 3px solid #4CAF50;
    }
    .comment-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 10px;
        padding-bottom: 8px;
        border-bottom: 1px solid #eee;
    }
    .comment-writer {
        font-weight: bold;
        color: #333;
    }
    .comment-date {
        color: #888;
        font-size: 0.9em;
    }
    .comment-content {
        line-height: 1.6;
        color: #333;
        margin-bottom: 10px;
    }
    .comment-actions {
        font-size: 0.9em;
    }
    .comment-actions a {
        color: #666;
        text-decoration: none;
        margin-right: 15px;
    }
    .comment-actions a:hover {
        color: #4CAF50;
        text-decoration: underline;
    }
    .comment-actions .delete-link {
        color: #d32f2f;
    }
    .comment-actions .delete-link:hover {
        color: #b71c1c;
    }
    
    /* 返信フォームのスタイル */
    .reply-form {
        margin-top: 10px;
        padding: 15px;
        background-color: #f0f8ff;
        border-radius: 5px;
        display: none;
    }
    .reply-form.active {
        display: block;
    }
    .reply-form input[type="text"],
    .reply-form textarea {
        width: 100%;
        padding: 8px;
        margin-bottom: 10px;
        border: 1px solid #ddd;
        border-radius: 3px;
        box-sizing: border-box;
    }
    .reply-form button {
        background-color: #2196F3;
        color: white;
        padding: 8px 16px;
        border: none;
        border-radius: 3px;
        cursor: pointer;
        margin-right: 5px;
    }
    .reply-form button:hover {
        background-color: #1976D2;
    }
    .reply-form .cancel-btn {
        background-color: #999;
    }
    .reply-form .cancel-btn:hover {
        background-color: #777;
    }
    
    /* 編集フォームのスタイル */
    .edit-form {
        margin-top: 10px;
        padding: 15px;
        background-color: #fff8e1;
        border-radius: 5px;
        display: none;
    }
    .edit-form.active {
        display: block;
    }
    .edit-form textarea {
        width: 100%;
        padding: 8px;
        margin-bottom: 10px;
        border: 1px solid #ddd;
        border-radius: 3px;
        box-sizing: border-box;
    }
    .edit-form button {
        background-color: #FF9800;
        color: white;
        padding: 8px 16px;
        border: none;
        border-radius: 3px;
        cursor: pointer;
        margin-right: 5px;
    }
    .edit-form button:hover {
        background-color: #F57C00;
    }
    .edit-form .cancel-btn {
        background-color: #999;
    }
    .edit-form .cancel-btn:hover {
        background-color: #777;
    }
</style>
<script>
    // 返信フォーム表示/非表示
    function toggleReplyForm(commentId) {
        var form = document.getElementById('reply-form-' + commentId);
        if (form.classList.contains('active')) {
            form.classList.remove('active');
        } else {
            // 他のフォームを閉じる
            closeAllForms();
            form.classList.add('active');
        }
    }
    
    // 編集フォーム表示/非表示
    function toggleEditForm(commentId) {
        var form = document.getElementById('edit-form-' + commentId);
        var content = document.getElementById('comment-content-' + commentId);
        
        if (form.classList.contains('active')) {
            form.classList.remove('active');
            content.style.display = 'block';
        } else {
            // 他のフォームを閉じる
            closeAllForms();
            form.classList.add('active');
            content.style.display = 'none';
        }
    }
    
    // 全てのフォームを閉じる
    function closeAllForms() {
        var allForms = document.querySelectorAll('.reply-form, .edit-form');
        allForms.forEach(function(f) {
            f.classList.remove('active');
        });
        var allContents = document.querySelectorAll('[id^="comment-content-"]');
        allContents.forEach(function(c) {
            c.style.display = 'block';
        });
    }
    
    // 削除確認
    function confirmDelete(commentId) {
        return confirm('このコメントを削除しますか？');
    }
</script>
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

<!-- ========== コメントセクション ========== -->
<div class="comment-section">
    <h2>コメント (<s:property value="comments.size()"/>)</h2>
    
    <!-- コメント投稿フォーム -->
    <div class="comment-form">
        <h3>コメントを投稿</h3>
        <s:form action="commentAdd" method="post">
            <s:hidden name="boardId" value="%{item.boardId}"/>
            <s:hidden name="ipAddress" value="%{#request.getRemoteAddr()}"/>
            
            <s:textfield name="writer" placeholder="名前" maxlength="50" required="true"/>
            <s:textarea name="content" placeholder="コメント内容を入力してください..." 
                       rows="4" required="true"/>
            <s:submit value="コメント投稿"/>
        </s:form>
    </div>
    
    <!-- コメント一覧 -->
    <div class="comment-list">
        <s:if test="comments == null || comments.isEmpty()">
            <p style="color: #888; text-align: center; padding: 20px;">
            </p>
        </s:if>
        <s:else>
            <s:iterator value="comments" var="comment">
                <!-- 親コメントか返信かで表示を変える -->
                <div class="comment-item <s:if test='#comment.parentCommentId != null'>comment-reply</s:if>">
                    <div class="comment-header">
                        <div>
                            <span class="comment-writer">
                                <s:property value="#comment.writer"/>
                            </span>
                            <s:if test="#comment.parentCommentId != null">
                                <span style="color: #4CAF50; margin-left: 5px;">↳ 返信</span>
                            </s:if>
                        </div>
                        <span class="comment-date">
                            <s:date name="#comment.createdAt" format="yyyy/MM/dd HH:mm"/>
                            <s:if test="#comment.updatedAt != null">
                            </s:if>
                        </span>
                    </div>
                    
                    <div class="comment-content" id="comment-content-<s:property value='#comment.commentId'/>">
                        <s:property value="#comment.content" escapeHtml="false"/>
                    </div>
                    
                    <!-- 編集フォーム -->
                    <div id="edit-form-<s:property value='#comment.commentId'/>" class="edit-form">
                        <h4>コメントを編集</h4>
                        <s:form action="commentEditSubmit" method="post">
                            <s:hidden name="commentId" value="%{#comment.commentId}"/>
                            <s:hidden name="boardId" value="%{item.boardId}"/>
                            
                            <s:textarea name="content" value="%{#comment.content}" rows="4" required="true"/>
                            <button type="submit">更新</button>
                            <button type="button" class="cancel-btn" 
                                    onclick="toggleEditForm(<s:property value='#comment.commentId'/>)">
                                キャンセル
                            </button>
                        </s:form>
                    </div>
                    
                    <!-- コメントアクション -->
                    <div class="comment-actions">
                        <s:if test="#comment.parentCommentId == null">
                            <a href="javascript:void(0)" 
                               onclick="toggleReplyForm(<s:property value='#comment.commentId'/>)">
                                返信
                            </a>
                        </s:if>
                        <a href="javascript:void(0)"
                           onclick="toggleEditForm(<s:property value='#comment.commentId'/>)">
                            編集
                        </a>
                        <a href="commentDelete.action?commentId=<s:property value='#comment.commentId'/>&boardId=<s:property value='item.boardId'/>" 
                           class="delete-link"
                           onclick="return confirmDelete(<s:property value='#comment.commentId'/>)">
                            削除
                        </a>
                    </div>
                    
                    <!-- 返信フォーム -->
                    <s:if test="#comment.parentCommentId == null">
                        <div id="reply-form-<s:property value='#comment.commentId'/>" class="reply-form">
                            <h4>返信を投稿</h4>
                            <s:form action="commentAdd" method="post">
                                <s:hidden name="boardId" value="%{item.boardId}"/>
                                <s:hidden name="parentCommentId" value="%{#comment.commentId}"/>
                                <s:hidden name="ipAddress" value="%{#request.getRemoteAddr()}"/>
                                
                                <s:textfield name="writer" placeholder="名前" maxlength="50" required="true"/>
                                <s:textarea name="content" placeholder="返信内容を入力してください..." 
                                           rows="3" required="true"/>
                                <button type="submit">返信投稿</button>
                                <button type="button" class="cancel-btn" 
                                        onclick="toggleReplyForm(<s:property value='#comment.commentId'/>)">
                                    キャンセル
                                </button>
                            </s:form>
                        </div>
                    </s:if>
                </div>
            </s:iterator>
        </s:else>
    </div>
</div>
</body>
</html>