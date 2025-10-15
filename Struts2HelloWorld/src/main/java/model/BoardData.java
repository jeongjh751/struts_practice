package model;

import java.sql.Timestamp;

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
	
	private long boardId;  // 各投稿を識別するID
	// 編集・削除機能ではどの投稿を操作するか特定する必要がある
	// Listのインデックスではなく、固有のIDで管理する方が安全
    
	private String category;

	private String title; 
	// 各投稿のタイトル
	
    private String content;
    // 投稿メッセージの本文を保存するフィールド
	
    private Timestamp createdAt;
    // 投稿日時を保存するフィールド
    // 形式: "yyyy/MM/dd HH:mm:ss" （例: "2025/10/09 16:46:48"）
    
	private Timestamp updatedAt;
    /*
     * - 投稿が編集された最後の日時
     * - トリガーで自動更新される
     * - NULLの場合は未編集
     */
	
    private String writer;
    // 投稿者の名前を保存するフィールド
    
    private int viewCount;
    // 投稿の閲覧数
    
    private int likeCount;
    // 投稿のいいね数
    
    private int dislikeCount;
    // 投稿のよくない数
    
    private int commentCount;
    // 投稿のコメント数
    
    private String ipAddress;
    // 投稿者のIPアドレスを保存するフィールド
    // 形式: "192.168.1.1" または "0:0:0:0:0:0:0:1"（IPv6のlocalhost）
    private boolean isNotice;
    /*
     * 【お知らせフラグ】
     * - true: お知らせ投稿（上部固定）
     * - false: 通常投稿
     * 
     * 【並び替え】
     * ORDER BY is_notice DESC, created_at DESC
     * → お知らせが最上部に表示される
     */
    
    private boolean isImage;
    /*
     * - true: 画像あり
     * - false: テキストのみ
     * 【使用目的】
     * - 一覧画面で画像アイコン表示
     * - 「画像投稿のみ」フィルタリング
     * - インデックスで高速検索
     */
    
    private boolean isSecret;
    /*
     * - true: 秘密投稿（作成者のみ閲覧可能）
     * - false: 公開投稿
     * 【用途】
     * - 質問カテゴリで個人情報含む質問
     * - パスワード付き投稿機能
     */
    
    private boolean isDeleted;
    /*
     * - true: 削除済み（論理削除）
     * - false: 有効な投稿
     * 【論理削除とは】
     * - 物理削除: DELETE文で完全削除
     * - 論理削除: フラグを立てるだけ（復旧可能）
     * 
     * 【メリット】
     * - データ復旧が可能
     * - 削除履歴の追跡
     * - 統計データの保持
     */
    
    // ========== Getterメソッド ==========
    // フィールドの値を取得するためのメソッド
    // 命名規則: get + フィールド名（先頭大文字）
    // JSPの<s:property value="postDate"/>で呼ばれる
    
    /**
     * 投稿番号を取得
     * @return 投稿番号（long形式）
     */
	public long getBoardId() {
		return boardId;
	}

    /**
     * カテゴリを取得
     * @return カテゴリ
     */
    public String getCategory() {
        return category;
    }
	
    /**
     * タイトルを取得
     * @return タイトル（String形式）
     */
    public String getTitle() {
        return title;
    }

    /**
     * 投稿contentを取得
     * @return content本文
     */
    public String getContent() {
        return content;
    }

    /**
     * 投稿日時を取得
     * @return 投稿日時（String形式）
     */
    public Timestamp getCreatedAt() {
        return createdAt;
        // createdAtフィールドの値をそのまま返す
    }
    
    /**
     * 更新日時を取得
     * @return 更新日時
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * 作成者名を取得
     * @return 作成者名
     */
    public String getWriter() {
        return writer;
    }

    /**
     * 投稿の閲覧数
     * @return 閲覧数
     */
    public int getViewCount() {
        return viewCount;
    }
    
     /**
     * いいね数を取得
     * @return いいね数
     */
    public int getLikeCount() {
        return likeCount;
    }
    
    /**
     * よくないね数を取得
     * @return よくないね数
     */
    public int getDislikeCount() {
        return dislikeCount;
    }
    
    /**
     * コメント数を取得
     * @return コメント数
     */
    public int getCommentCount() {
        return commentCount;
    }
    
    /**
     * 投稿者のIPアドレスを取得
     * @return IPアドレス
     */
    public String getIpAddress() {
        return ipAddress;
    }
    
    /**
     * お知らせフラグを取得
     * @return お知らせの場合true
     */
    public boolean isNotice() {
        return isNotice;
    }
    
    /**
     * 画像有無フラグを取得
     * @return 画像がある場合true
     */
    public boolean isImage() {
        return isImage;
    }
    
    /**
     * 秘密投稿フラグを取得
     * @return 秘密投稿の場合true
     */
    public boolean isSecret() {
        return isSecret;
    }
    
    /**
     * 削除フラグを取得
     * @return 削除済みの場合true
     */
    public boolean isDeleted() {
        return isDeleted;
    }
    
    // ========== Setterメソッド ==========
    // フィールドに値を設定するためのメソッド
    // 命名規則: set + フィールド名（先頭大文字）
    // Struts2が自動的にフォームの値を設定する際に呼ばれる


    /**
     * 投稿番号を設定
     * @param boardId 設定する投稿番号
     *
     */
	public void setBoardId(long boardId) {
		this.boardId = boardId;
	}
	
    /**
     * カテゴリを設定
     * @param category カテゴリ
     */
    public void setCategory(String category) {
        this.category = category;
    }
    
	 /**
    * タイトルを取得
    * @return タイトル（String形式）
    */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * 投稿メッセージを設定
     * @param content 設定するメッセージ本文
     * 
     * 使用箇所:
     * - Board.addChatData()メソッドで呼ばれる
     * - JSPフォームから送信されたメッセージが保存される
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * 投稿日時を設定
     * @param createdAt 設定する投稿日時
     * 
     * 使用箇所:
     * - Board.addChatData()メソッド内で新しい投稿データを作成する際に呼ばれる
     * - data.setCreatedAt(sdformat.format(new Date()));
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        // this.createdAt: このクラスのフィールド
        // createdAt: メソッドの引数
        // this.を付けることで、フィールドと引数を区別する
    }
    
    /**
     * 更新日時を設定
     * @param updatedAt 更新日時
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * 作成者名を設定
     * @param writer 作成者名
     * 
     * 使用箇所:
     * - Board.addChatData()メソッドで呼ばれる
     * - JSPフォームから送信された名前が自動的にBoardActionのnameフィールドに
     *   設定され、それがこのメソッドで最終的にBoardDataオブジェクトに保存される
     */
    public void setWriter(String writer) {
        this.writer = writer;
    }

    /**
     * 閲覧数を設定
     * @param viewCount 設定する投稿の閲覧数
     * 
     */
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
    
    /**
     * いいね数を設定
     * @param likeCount いいね数
     */
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    
    /**
     * よくない数を設定
     * @param dislikeCount よくないね数
     */
    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }
    
    /**
     * コメント数を設定
     * @param commentCount コメント数
     */
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    
    /**
     * 投稿者のIPアドレスを設定
     * @param ipAddress 設定するIPアドレス
     * 
     * 使用箇所:
     * - Board.addChatData()メソッドで呼ばれる
     * - JSPでrequest.getRemoteAddr()から取得したIPアドレスが保存される
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    /**
     * お知らせフラグを設定
     * @param isNotice お知らせフラグ
     */
    public void setNotice(boolean isNotice) {
        this.isNotice = isNotice;
    }
    
    /**
     * 画像有無フラグを設定
     * @param isImage 画像有無フラグ
     */
    public void setImage(boolean isImage) {
        this.isImage = isImage;
    }
    
    /**
     * 秘密投稿フラグを設定
     * @param isSecret 秘密投稿フラグ
     */
    public void setSecret(boolean isSecret) {
        this.isSecret = isSecret;
    }
    
    /**
     * 削除フラグを設定
     * @param isDeleted 削除フラグ
     */
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
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