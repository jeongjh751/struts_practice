package dto.response;

import java.sql.Timestamp;

import entity.BoardEntity;

/**
 * 【BoardListResponseクラス】
 * 掲示板一覧画面で必要なデータのみ含む
 * 
 * 役割:
 * - Service → Action → JSP一覧画面へ渡される
 * - 一覧に不要なデータ除外（パフォーマンス最適化）
 * - 画面表示用フォーマットメソッド含む
 * 
 * 含まれるフィールド:
 * - 掲示板識別: boardId
 * - 基本情報: category, title, writer
 * - 統計: viewCount
 * - 日付: createdAt
 * - ファイル: fileName（添付有無表示用）
 * 
 * 除外されるフィールド（一覧に不要）:
 * - content: 本文全体（一覧ではプレビューもなし）
 * - ipAddress: セキュリティ上公開不要
 * - likeCount, dislikeCount: 一覧で未使用
 * - filePath, fileSize: 詳細情報
 * - isSecret, isDeleted: システム内部情報
 */
public class BoardListResponse {
    
    // ========== 一覧画面必須フィールド ==========
    private Long boardId;           // 掲示板ID（詳細表示リンク用）
    private String category;        // カテゴリ
    private String title;           // タイトル
    private String writer;          // 作成者
    private Integer viewCount;      // 閲覧数
    private Timestamp createdAt;    // 作成日
    
    // ========== ファイル添付表示用 ==========
    private String fileName;        // ファイル名（添付アイコン表示用）
    
    // ========== デフォルトコンストラクタ ==========
    public BoardListResponse() {
    }
    
    // ========== 全フィールドコンストラクタ ==========
    public BoardListResponse(Long boardId, String category, String title,
                            String writer, Integer viewCount, Timestamp createdAt,
                            String fileName) {
        this.boardId = boardId;
        this.category = category;
        this.title = title;
        this.writer = writer;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.fileName = fileName;
    }
    
    // ========== Entity → DTO変換（Factory Method） ==========
    
    /**
     * EntityをList用DTOに変換
     * @param entity DBから取得したEntity
     * @return 一覧用Response DTO
     */
    public static BoardListResponse from(BoardEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new BoardListResponse(
            entity.getBoardId(),
            entity.getCategory(),
            entity.getTitle(),
            entity.getWriter(),
            entity.getViewCount(),
            entity.getCreatedAt(),
            entity.getFileName()
        );
    }
    
    // ========== Getter/Setter ==========
    
    public Long getBoardId() {
        return boardId;
    }
    
    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getWriter() {
        return writer;
    }
    
    public void setWriter(String writer) {
        this.writer = writer;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    // ========== 画面表示用メソッド ==========
    
    /**
     * ファイル添付有無
     * @return ファイルが添付されていればtrue
     */
    public boolean hasFile() {
        return fileName != null && !fileName.isEmpty();
    }
    
    /**
     * タイトル長さ制限（一覧で長すぎるタイトルを省略）
     * @param maxLength 最大長さ
     * @return 省略されたタイトル
     */
    public String getTitleWithLimit(int maxLength) {
        if (title == null) {
            return "";
        }
        if (title.length() <= maxLength) {
            return title;
        }
        return title.substring(0, maxLength) + "...";
    }
    
    @Override
    public String toString() {
        return "BoardListResponse{" +
                "boardId=" + boardId +
                ", category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", writer='" + writer + '\'' +
                ", viewCount=" + viewCount +
                ", createdAt=" + createdAt +
                ", hasFile=" + hasFile() +
                '}';
    }
}