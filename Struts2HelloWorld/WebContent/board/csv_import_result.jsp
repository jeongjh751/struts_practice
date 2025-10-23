<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>CSV import 結果</title>
    <style>
        .container {
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
        }
        .error {
            color: red;
            background: #ffe6e6;
            padding: 10px;
            margin: 10px 0;
            border-radius: 5px;
        }
        .message {
            color: green;
            font-size: 18px;
        }
        button {
            padding: 10px 20px;
            margin: 5px;
            border-radius: 5px;
            border: none;
            cursor: pointer;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>CSV import 完了</h2>
        
        <!-- 成功メッセージ -->
        <s:if test="hasActionMessages()">
            <div class="message">
                <s:actionmessage/>
            </div>
        </s:if>
        
        <!-- 結果詳細 -->
        <p>総 <strong><s:property value="importResult.successCount + importResult.failCount"/></strong>件</p>
        <p>成功: <strong style="color: green;"><s:property value="importResult.successCount"/></strong>件</p>
        <p>失敗: <strong style="color: red;"><s:property value="importResult.failCount"/></strong>件</p>
        
        <!-- エラー詳細 -->
        <s:if test="hasActionErrors()">
            <div class="error">
                <h3>エラー詳細:</h3>
                <s:actionerror/>
            </div>
        </s:if>
        
        <div style="text-align: center;">
            <a href="boardList.action">
                <button>掲示板のリストへ</button>
            </a>
            <a href="boardImportForm.action">
                <button>追加import</button>
            </a>
        </div>
    </div>
</body>
</html>