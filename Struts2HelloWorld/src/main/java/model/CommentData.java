package model;

import java.sql.Timestamp;

public class CommentData {

    // ========== フィールド宣言 ==========
    // すべてprivateで宣言し、外部から直接アクセスできないようにする
    // これをカプセル化（encapsulation）と呼ぶ
    
    private long commentId;
    // 各コメントを識別するID
    // 編集・削除機能ではどのコメントを操作するか特定する必要がある
    
    private long boardId;
    // どの投稿に対するコメントかを示すID
    // board_dataテーブルのboard_idと紐づく外部キー
    
    private String writer;
    // コメント投稿者の名前を保存するフィールド
    
    private String content;
    // コメントの本文を保存するフィールド
    
    
    private Long parentCommentId;
    /*
     * 【コメ返し機能】
     * - NULL: 通常のコメント（親コメント）
     * - 値あり: 返信コメント（子コメント）
     */
    
    private String ipAddress;
    // コメント投稿者のIPアドレスを保存するフィールド
    // 形式: "192.168.1.1" または "0:0:0:0:0:0:0:1"（IPv6のlocalhost）
    
    private boolean isDeleted;
    /*
     * - true: 削除済み（論理削除）
     * - false: 有効なコメント
     * 
     */
    
    private Timestamp createdAt;
    // コメント投稿日時を保存するフィールド
    // 形式: "yyyy/MM/dd HH:mm:ss" （例: "2025/10/16 14:30:00"）
    
    private Timestamp updatedAt;
    /*
     * - コメントが編集された最後の日時
     * - NULLの場合は未編集
     */
    
    // ========== Getterメソッド ==========
    // フィールドの値を取得するためのメソッド
    // JSPの<s:property value="commentId"/>で呼ばれる
    
    /**
     * コメントIDを取得
     * @return コメントID（long形式）
     */
    public long getCommentId() {
        return commentId;
    }
    
    /**
     * 投稿IDを取得
     * @return 投稿ID（long形式）
     */
    public long getBoardId() {
        return boardId;
    }
    
    /**
     * コメント投稿者名を取得
     * @return 投稿者名
     */
    public String getWriter() {
        return writer;
    }
    
    /**
     * コメント本文を取得
     * @return コメント本文
     */
    public String getContent() {
        return content;
    }

    /**
     * 親コメントIDを取得
     * @return 親コメントID（返信の場合のみ値あり、通常コメントの場合null）
     */
    public Long getParentCommentId() {
        return parentCommentId;
    }
    
    /**
     * コメント投稿者のIPアドレスを取得
     * @return IPアドレス
     */
    public String getIpAddress() {
        return ipAddress;
    }
    
    /**
     * 削除フラグを取得
     * @return 削除済みの場合true
     */
    public boolean isDeleted() {
        return isDeleted;
    }
    
    /**
     * コメント投稿日時を取得
     * @return 投稿日時（Timestamp形式）
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    /**
     * コメント更新日時を取得
     * @return 更新日時（Timestamp形式）
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    // ========== Setterメソッド ==========
    // フィールドに値を設定するためのメソッド
    // Struts2が自動的にフォームの値を設定する際に呼ばれる
    
    /**
     * コメントIDを設定
     * @param commentId 設定するコメントID
     */
    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }
    
    /**
     * 投稿IDを設定
     * @param boardId 設定する投稿ID
     */
    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }
    
    /**
     * コメント投稿者名を設定
     * @param writer 設定する投稿者名
     */
    public void setWriter(String writer) {
        this.writer = writer;
    }
    
    /**
     * コメント本文を設定
     * @param content 設定するコメント本文
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 親コメントIDを設定
     * @param parentCommentId 設定する親コメントID（返信の場合のみ）
     */
    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
    
    /**
     * コメント投稿者のIPアドレスを設定
     * @param ipAddress 設定するIPアドレス
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    /**
     * 削除フラグを設定
     * @param isDeleted 設定する削除フラグ
     */
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }    
    
    /**
     * コメント投稿日時を設定
     * @param createdAt 設定する投稿日時
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * コメント更新日時を設定
     * @param updatedAt 設定する更新日時
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
}