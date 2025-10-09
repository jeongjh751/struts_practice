package model;

/**
 * 【BoardDataクラス】
 * 掲示板に投稿される1件のデータを表すモデルクラス
 * 
 * 役割:
 * - 投稿データの保持（データコンテナ）
 * - JavaBeansの規約に従ったPOJO（Plain Old Java Object）
 * 
 * JavaBeansの条件:
 * 1. publicクラスであること
 * 2. デフォルトコンストラクタ（引数なし）を持つこと
 * 3. privateフィールドを持つこと
 * 4. getter/setterメソッドでフィールドにアクセスすること
 * 5. Serializableインターフェースを実装すること（推奨）
 * 
 * このクラスはStruts2のフレームワークによって自動的に使用される:
 * - フォームからの入力値が自動的にsetterメソッドで設定される
 * - JSPでの表示時にgetterメソッドが自動的に呼ばれる
 */
public class BoardData {

    // ========== フィールド宣言 ==========
    // すべてprivateで宣言し、外部から直接アクセスできないようにする
    // これをカプセル化（encapsulation）と呼ぶ
    
    private String postDate;
    // 投稿日時を保存するフィールド
    // 形式: "yyyy/MM/dd HH:mm:ss" （例: "2025/10/09 16:46:48"）
    
    private String name;
    // 投稿者の名前を保存するフィールド
    
    private String message;
    // 投稿メッセージの本文を保存するフィールド
    
    private String remoteAddress;
    // 投稿者のIPアドレスを保存するフィールド
    // 形式: "192.168.1.1" または "0:0:0:0:0:0:0:1"（IPv6のlocalhost）

    // ========== Getterメソッド ==========
    // フィールドの値を取得するためのメソッド
    // 命名規則: get + フィールド名（先頭大文字）
    // JSPの<s:property value="postDate"/>で呼ばれる
    
    /**
     * 投稿日時を取得
     * @return 投稿日時（String形式）
     */
    public String getPostDate() {
        return postDate;
        // postDateフィールドの値をそのまま返す
    }

    /**
     * 投稿者名を取得
     * @return 投稿者名
     */
    public String getName() {
        return name;
    }

    /**
     * 投稿メッセージを取得
     * @return メッセージ本文
     */
    public String getMessage() {
        return message;
    }

    /**
     * 投稿者のIPアドレスを取得
     * @return IPアドレス
     */
    public String getRemoteAddress() {
        return remoteAddress;
    }

    // ========== Setterメソッド ==========
    // フィールドに値を設定するためのメソッド
    // 命名規則: set + フィールド名（先頭大文字）
    // Struts2が自動的にフォームの値を設定する際に呼ばれる
    
    /**
     * 投稿日時を設定
     * @param postDate 設定する投稿日時
     * 
     * 使用箇所:
     * - Board.addChatData()メソッド内で新しい投稿データを作成する際に呼ばれる
     * - data.setPostDate(sdformat.format(new Date()));
     */
    public void setPostDate(String postDate) {
        this.postDate = postDate;
        // this.postDate: このクラスのフィールド
        // postDate: メソッドの引数
        // this.を付けることで、フィールドと引数を区別する
    }

    /**
     * 投稿者名を設定
     * @param name 設定する投稿者名
     * 
     * 使用箇所:
     * - Board.addChatData()メソッドで呼ばれる
     * - JSPフォームから送信された名前が自動的にBoardActionのnameフィールドに
     *   設定され、それがこのメソッドで最終的にBoardDataオブジェクトに保存される
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 投稿メッセージを設定
     * @param message 設定するメッセージ本文
     * 
     * 使用箇所:
     * - Board.addChatData()メソッドで呼ばれる
     * - JSPフォームから送信されたメッセージが保存される
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 投稿者のIPアドレスを設定
     * @param remoteAddress 設定するIPアドレス
     * 
     * 使用箇所:
     * - Board.addChatData()メソッドで呼ばれる
     * - JSPでrequest.getRemoteAddr()から取得したIPアドレスが保存される
     */
    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    // ========== その他の情報 ==========
    
    /*
     * 【このクラスの使用の流れ】
     * 
     * 1. ユーザーがJSPフォームに名前とメッセージを入力
     *    ↓
     * 2. 「投稿」ボタンをクリック
     *    ↓
     * 3. Struts2が自動的にBoardActionクラスのsetName()、setMessage()、
     *    setRemoteAddress()を呼び出してデータを設定
     *    ↓
     * 4. BoardAction.execute()メソッドが実行される
     *    ↓
     * 5. Board.addChatData()が呼ばれる
     *    ↓
     * 6. 新しいBoardDataオブジェクトが作成される
     *    BoardData data = new BoardData();
     *    ↓
     * 7. setterメソッドでデータが設定される
     *    data.setName(name);
     *    data.setMessage(message);
     *    data.setRemoteAddress(remoteAddress);
     *    data.setPostDate(sdformat.format(new Date()));
     *    ↓
     * 8. Listに追加される
     *    board.add(0, data);
     *    ↓
     * 9. JSPで表示される際にgetterメソッドが呼ばれる
     *    <s:property value="name"/> → getName()が呼ばれる
     */
    
    /*
     * 【なぜgetter/setterを使うのか？】
     * 
     * 直接フィールドにアクセスすることもできるが、getter/setterを使う理由:
     * 
     * 1. カプセル化: データの隠蔽により、クラス内部の実装を変更しやすい
     * 2. バリデーション: setterメソッド内で値のチェックができる
     *    例: public void setName(String name) {
     *           if(name == null || name.isEmpty()) {
     *               throw new IllegalArgumentException("名前は必須です");
     *           }
     *           this.name = name;
     *        }
     * 3. フレームワーク対応: Struts2、Spring等のフレームワークは
     *    JavaBeansの規約に従ったクラスを前提としている
     * 4. デバッグ: getter/setter内にログ出力やブレークポイントを設定できる
     */
}