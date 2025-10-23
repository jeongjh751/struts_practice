<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>CSV import</title>
    <style>
        .container {
            max-width: 800px;
            margin: 20px auto;
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
            background: #e6ffe6;
            padding: 10px;
            margin: 10px 0;
            border-radius: 5px;
        }
        .info { 
            background: #f0f0f0; 
            padding: 15px; 
            margin: 20px 0;
            border-radius: 5px;
        }
        button {
            padding: 10px 20px;
            background: #0066cc;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        button:hover {
            background: #0052a3;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>投稿一括登録(CSV import)</h2>
        
        <!-- エラーメッセージ -->
        <s:if test="hasActionErrors()">
            <div class="error">
                <s:actionerror/>
            </div>
        </s:if>
        
        <!-- アップロードフォーム -->
        <s:form action="boardImportCsv" method="post" enctype="multipart/form-data">
            <s:file name="csvFile" label="CSV file" accept=".csv" required="true"/>
            <s:submit value="登録"/>
        </s:form>
        
        <!-- CSV形式案内 -->
        <div class="info">
            <h3>CSV形式</h3>
            <p><strong>ヘッダー:</strong> category,title,content,writer</p>
            <p><strong>例:</strong></p>
            <pre>category,title,content,writer
お知らせ,1番目のお知らせです。,内容です。,管理者
自由,自由掲示板のタイトル,内容,田中</pre>
            <p>※ UTF-8 エンコードで保存してください。</p>
        </div>
        
        <hr/>
        <a href="boardList.action">戻る</a>
    </div>
</body>
</html>