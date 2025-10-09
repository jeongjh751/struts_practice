<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSPページのエンコーディング設定。UTF-8を指定して日本語を正しく表示 --%>

<%@page import="model.Board"%>
<%-- 掲示板データを管理するBoardクラスをインポート --%>

<%@page import="com.opensymphony.xwork2.util.ValueStack"%>
<%-- Struts2のValueStack（値を保存するスタック構造）をインポート --%>

<%@page import="org.apache.struts2.ServletActionContext"%>
<%-- ServletActionContext（Struts2のコンテキスト情報）をインポート --%>

<!DOCTYPE html>
<html>

<%@taglib prefix="s" uri="/struts-tags" %>
<%-- 
【Struts2タグライブラリの宣言】
- prefix="s": Struts2タグの接頭辞を"s"に設定
- これにより<s:form>、<s:textfield>などのタグが使用可能になる
- 昔のStruts1では<html:form>、<bean:write>などを使用していた
- Struts2からは統一して<s:～>形式になった
--%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>シンプル掲示板</title>
</head>
<body>
<h1>シンプル掲示板</h1>
<h2>struts2で作成するサンプルアプリケーション</h2>
<hr/>

<s:form action="board.action" theme="simple">
<%--
【<s:form>タグ】
- Struts2のフォームタグ。HTMLの<form>タグを生成する
- action="board.action": フォーム送信先のアクション名を指定
  → BoardActionクラスのexecute()メソッドが呼ばれる
- theme="simple": フォームのテーマを指定
  → デフォルトではStruts2が自動でラベルやエラー表示用のHTMLを追加するが、
    "simple"を指定すると最小限のHTMLのみ生成される
- 通常のHTMLとの違い:
  <form action="board.action" method="post"> ← 昔のHTML
  <s:form action="board.action"> ← Struts2（自動でmethod="post"になる）
--%>

<input type="hidden" name="remoteAddress" value="<%= request.getRemoteAddr() %>"/>
<%-- 
【hiddenフィールド】
- クライアントのIPアドレスを取得してフォームに埋め込む
- request.getRemoteAddr(): クライアントのIPアドレスを取得するJSPメソッド
- このデータはBoardActionクラスのremoteAddressフィールドに自動でバインドされる
--%>

<%-- 
■ 元のコード（動作しない）:
<s:text name="名前"/> <s:textfield name="name" size="10"/>
<s:text name="メッセージ"/> <s:textfield name="message" size="60" value=""/>

【<s:text>タグとは】
- Struts2の国際化（i18n）機能を使用するタグ
- name属性に指定したキーをプロパティファイルから検索して表示する
- 例: name="名前" → BoardAction.propertiesファイルで「名前=Name」と定義すれば
  ロケールに応じて表示が変わる

【問題点】
1. <s:text>タグはname属性のキー（"名前"、"メッセージ"）をプロパティファイルから検索
2. プロパティファイル（BoardAction.properties）が存在しない場合、
   キー自体がユニコードエスケープ形式で出力される
   例: \u30E1\u30C3\u30BB\u30FC\u30B8 = メッセージ
3. Struts2は内部的にJavaのプロパティファイル形式でキーを管理するため、
   日本語がエスケープされてしまう

【解決方法】
方法1: プロパティファイル（BoardAction.properties）を作成
  名前=名前
  メッセージ=メッセージ
方法2: <s:text>タグを削除して直接テキストを入力（今回採用）
--%>

<%-- 修正されたコード：直接テキスト入力 --%>
名前 <s:textfield name="name" size="10"/>
<%--
【<s:textfield>タグ】
- Struts2のテキスト入力フィールドタグ
- HTMLの<input type="text">を生成する
- name="name": フィールド名。BoardActionクラスのnameフィールドと自動でバインド
- size="10": 表示幅を10文字分に設定
- Struts2の自動バインディング:
  ユーザーが入力 → フォーム送信 → BoardAction.setName(値)が自動で呼ばれる
--%>

メッセージ <s:textfield name="message" size="60" value=""/>
<%--
【<s:textfield>タグ - メッセージ用】
- name="message": BoardActionクラスのmessageフィールドとバインド
- size="60": 表示幅を60文字分に設定
- value="": 初期値を空に設定（省略可能）
--%>

<s:submit value="投稿"/>
<%--
【<s:submit>タグ - 投稿ボタン】
- Struts2の送信ボタンタグ。HTMLの<input type="submit">を生成
- value="投稿": ボタンに表示されるテキスト
- このボタンを押すと:
  1. フォームデータが送信される
  2. board.actionが呼ばれる
  3. BoardActionクラスのexecute()メソッドが実行される
--%>

<s:submit value="更新" method="update"/>
<%--
【<s:submit>タグ - 更新ボタン】
- method="update": 特定のメソッドを指定
- このボタンを押すと:
  1. フォームデータが送信される
  2. board.actionが呼ばれる
  3. BoardActionクラスのupdate()メソッドが実行される
     （execute()ではなくupdate()が呼ばれる）
- これはStruts2の「Dynamic Method Invocation」機能
  （動的メソッド呼び出し）を利用している
--%>

</s:form>
<hr/>

<s:actionerror/>
<%--
【<s:actionerror>タグ】
- Actionクラスで設定されたエラーメッセージを表示するタグ
- BoardActionクラスのaddActionError()メソッドで追加されたエラーを表示
- 例: addActionError("名前を入力してください");
  → このタグの位置に赤色でエラーメッセージが表示される
- 複数のエラーがある場合は箇条書きで表示される
--%>

<table border="1">
<%
// 【ValueStackの処理】
// ValueStack: Struts2がデータを保存・管理するスタック構造
// 通常はActionクラスで設定されたデータが自動で格納される

ValueStack stack = (ValueStack)request.getAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY);
// リクエストからValueStackオブジェクトを取得

if(stack!=null && stack.findValue("data")==null){
    // ValueStackに"data"という名前のデータが存在しない場合
    // （board.jspを直接URLで開いた場合など、Actionを経由していない時）
    stack.set("data", Board.getChatData());
    // 直接Boardクラスから掲示板データを取得してValueStackに設定
    // これにより、初期表示時もデータが表示される
}
%>

<s:iterator value="data" status="stat">
<%--
【<s:iterator>タグ】
- Struts2の繰り返し処理タグ。JavaのforEachループに相当
- value="data": 繰り返し対象のコレクション名
  → ValueStackから"data"という名前のList<BoardData>を取得
  → BoardActionクラスのgetData()メソッドの戻り値
- status="stat": ループの状態を保持する変数名
  → stat.first: 最初のループかどうか（true/false）
  → stat.last: 最後のループかどうか
  → stat.index: 現在のインデックス番号（0から開始）
- 各ループで、List内のBoardDataオブジェクトが1つずつ処理される
--%>

<s:if test="#stat.first">
<%--
【<s:if>タグ】
- Struts2の条件分岐タグ。Javaのif文に相当
- test="#stat.first": 条件式
  → #stat.first: statオブジェクトのfirstプロパティを参照
  → 最初のループの時のみtrueになる
  → つまり、テーブルのヘッダー行は1回だけ出力される
- #記号: OGNL（Object-Graph Navigation Language）の変数参照記号
  通常の変数はActionのプロパティを参照するが、
  #を付けるとスタックコンテキストの変数を参照する
--%>
<th>日時</th>
<th>名前</th>
<th>テキスト</th>
<th>IPアドレス</th>
</s:if>
<tr>
<td><s:property value="postDate"/></td>
<%--
【<s:property>タグ】
- Struts2の値出力タグ。データを画面に表示する
- value="postDate": 表示する値のプロパティ名
  → 現在のループで処理中のBoardDataオブジェクトのpostDateフィールドを取得
  → BoardData.getPostDate()が呼ばれる
- HTMLエスケープ処理が自動で行われる（XSS対策）
- 昔のJSP/Servletでは: <%= boardData.getPostDate() %>
- Struts1では: <bean:write name="boardData" property="postDate"/>
- Struts2では: <s:property value="postDate"/>
--%>

<td><s:property value="name"/></td>
<%-- 投稿者名を表示。BoardData.getName()の値 --%>

<td><s:property value="message"/></td>
<%-- メッセージ本文を表示。BoardData.getMessage()の値 --%>

<td><s:property value="remoteAddress"/></td>
<%-- 投稿者のIPアドレスを表示。BoardData.getRemoteAddress()の値 --%>

</tr>
</s:iterator>
<%-- iteratorタグの終了。全てのBoardDataオブジェクトを処理するまでループ --%>

</table>
</body>
</html>