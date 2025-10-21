package dto.request;

/**
 * 【BoardUpdateRequestクラス】
 * 掲示板修正時にクライアントから受け取るデータ
 * 
 * 役割:
 * - JSP修正フォームから入力されたデータを格納するコンテナ
 * - Action → Serviceへ渡される時に使用
 * - 修正可能なフィールドのみ含む
 * 
 * CreateRequestとの違い:
 * - boardId必須（どの掲示板を修正するか識別）
 * - ipAddress不要（修正時は変更されない）
 * - ファイルは選択的に新規添付可能
 */
public class BoardUpdateRequest {
    
    // ========== 必須フィールド ==========
    private Long boardId;        // 修正対象掲示板ID（必須！）
    
    // ========== 修正可能フィールド ==========
    private String category;     // カテゴリ
    private String title;        // タイトル
    private String content;      // 本文
    private String writer;       // 作成者（実務では通常修正不可）
    
    // ========== ファイル関連フィールド（オプション） ==========
    private String fileName;     // 新規アップロードファイル名
    private String filePath;     // 新規ファイル保存パス
    private Long fileSize;       // 新規ファイルサイズ
    
    // ========== デフォルトコンストラクタ ==========
    public BoardUpdateRequest() {
    }
    
    // ========== 便利コンストラクタ（ファイルなし） ==========
    public BoardUpdateRequest(Long boardId, String category, String title,
                             String content, String writer) {
        this.boardId = boardId;
        this.category = category;
        this.title = title;
        this.content = content;
        this.writer = writer;
    }
    
    // ========== 便利コンストラクタ（ファイル含む） ==========
    public BoardUpdateRequest(Long boardId, String category, String title,
                             String content, String writer,
                             String fileName, String filePath, Long fileSize) {
        this.boardId = boardId;
        this.category = category;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
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
    
    // ========== 検証メソッド ==========
    
    /**
     * 必須入力値を検証
     * @return すべての必須値が入力されていればtrue
     */
    public boolean isValid() {
        return boardId != null && boardId > 0
            && title != null && !title.trim().isEmpty()
            && content != null && !content.trim().isEmpty()
            && writer != null && !writer.trim().isEmpty();
    }
    
    /**
     * ファイル添付有無
     * @return 新規ファイルが添付されていればtrue
     */
    public boolean hasFile() {
        return fileName != null && !fileName.isEmpty();
    }
    
    @Override
    public String toString() {
        return "BoardUpdateRequest{" +
                "boardId=" + boardId +
                ", category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", writer='" + writer + '\'' +
                ", hasFile=" + hasFile() +
                '}';
    }
}