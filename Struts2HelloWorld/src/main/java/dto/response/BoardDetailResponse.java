package dto.response;

import java.sql.Timestamp;

import entity.BoardEntity;

/**
 * 【BoardDetailResponseクラス】
 * 掲示板詳細画面で必要なすべてのデータを含む
 * 
 * 役割:
 * - Service → Action → JSP詳細画面へ渡される
 * - 掲示板の全情報提供
 * - ファイルダウンロード情報含む
 * 
 * 含まれるフィールド:
 * - 全掲示板情報（本文含む）
 * - ファイル詳細情報（ダウンロード用）
 * - 統計情報（閲覧数、いいねなど）
 * 
 * 除外されるフィールド:
 * - ipAddress: セキュリティ上一般ユーザーに公開不可
 * - isDeleted: システム内部情報
 * - isSecret: 権限チェックはServiceで処理
 */
public class BoardDetailResponse {
    
    // ========== 掲示板基本情報 ==========
    private Long boardId;
    private String category;
    private String title;
    private String content;        // 一覧と異なり本文含む！
    private String writer;
    
    // ========== 統計情報 ==========
    private Integer viewCount;
    private Integer likeCount;
    private Integer dislikeCount;
    
    // ========== ファイル情報（ダウンロード用） ==========
    private String fileName;       // 元のファイル名
    private String filePath;       // サーバー保存パス（ダウンロード用）
    private Long fileSize;         // ファイルサイズ
    
    // ========== タイムスタンプ ==========
    private Timestamp createdAt;   // 作成日
    private Timestamp updatedAt;   // 修正日
    
    // ========== デフォルトコンストラクタ ==========
    public BoardDetailResponse() {
    }
    
    // ========== 全フィールドコンストラクタ ==========
    public BoardDetailResponse(Long boardId, String category, String title,
                              String content, String writer, Integer viewCount,
                              Integer likeCount, Integer dislikeCount,
                              String fileName, String filePath, Long fileSize,
                              Timestamp createdAt, Timestamp updatedAt) {
        this.boardId = boardId;
        this.category = category;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // ========== Entity → DTO変換（Factory Method） ==========
    
    /**
     * Entityを詳細用DTOに変換
     * @param entity DBから取得したEntity
     * @return 詳細用Response DTO
     */
    public static BoardDetailResponse from(BoardEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new BoardDetailResponse(
            entity.getBoardId(),
            entity.getCategory(),
            entity.getTitle(),
            entity.getContent(),
            entity.getWriter(),
            entity.getViewCount(),
            entity.getLikeCount(),
            entity.getDislikeCount(),
            entity.getFileName(),
            entity.getFilePath(),
            entity.getFileSize(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
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
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
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
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public Integer getDislikeCount() {
        return dislikeCount;
    }
    
    public void setDislikeCount(Integer dislikeCount) {
        this.dislikeCount = dislikeCount;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
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
     * ファイルサイズを読みやすい形式に変換
     * @return フォーマットされたファイルサイズ（例: "1.5 MB"）
     */
    public String getFormattedFileSize() {
        if (fileSize == null || fileSize == 0) {
            return "";
        }
        if (fileSize < 1024) {
            return fileSize + " B";
        }
        if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        }
        return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
    }
    
    /**
     * 修正有無を確認
     * @return 修正された掲示板ならtrue
     */
    public boolean isModified() {
        return updatedAt != null && !updatedAt.equals(createdAt);
    }
    
    /**
     * 本文をHTMLで表示する時に改行処理
     * @return 改行が<br>タグに変換された本文
     */
    public String getContentWithLineBreaks() {
        if (content == null) {
            return "";
        }
        return content.replace("\n", "<br>");
    }
    
    @Override
    public String toString() {
        return "BoardDetailResponse{" +
                "boardId=" + boardId +
                ", category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", writer='" + writer + '\'' +
                ", viewCount=" + viewCount +
                ", hasFile=" + hasFile() +
                ", createdAt=" + createdAt +
                ", isModified=" + isModified() +
                '}';
    }
}