package dto.request;

/**
 * 【BoardCreateRequestクラス】
 * 新規掲示板作成時にクライアントから受け取るデータ
 * 
 * 役割:
 * - JSPフォームから入力されたデータを格納するコンテナ
 * - Action → Serviceへ渡される時に使用
 * - 作成に必要なフィールドのみ含む（boardId、統計情報は除外）
 * 
 * 含まれるフィールド:
 * - category: カテゴリ（自由/お知らせ/質問など）
 * - title: タイトル
 * - content: 本文
 * - writer: 作成者名
 * - ipAddress: 作成者IP（システムで自動設定）
 * 
 * 除外されるフィールド:
 * - boardId: 作成時DBで自動生成
 * - viewCount, likeCount: 作成時0に自動設定
 * - createdAt: DBで自動設定
 * - isDeleted: デフォルト値false
 */
public class BoardCreateRequest {
    
    // ========== 必須入力フィールド ==========
    private String category;     // カテゴリ
    private String title;        // タイトル
    private String content;      // 本文
    private String writer;       // 作成者
    
    // ========== システム自動設定フィールド ==========
    private String ipAddress;    // 作成者IP（Controllerで設定）
    
    // ========== ファイル関連フィールド（オプション） ==========
    private String fileName;     // 元のファイル名
    private String filePath;     // サーバー保存パス
    private Long fileSize;       // ファイルサイズ
    
    // ========== デフォルトコンストラクタ ==========
    public BoardCreateRequest() {
    }
    
    // ========== 便利コンストラクタ（ファイルなし） ==========
    public BoardCreateRequest(String category, String title, String content, 
                             String writer, String ipAddress) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.ipAddress = ipAddress;
    }
    
    // ========== 便利コンストラクタ（ファイル含む） ==========
    public BoardCreateRequest(String category, String title, String content,
                             String writer, String ipAddress,
                             String fileName, String filePath, Long fileSize) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.ipAddress = ipAddress;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }
    
    // ========== Getter/Setter ==========
    
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
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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
        return title != null && !title.trim().isEmpty()
            && content != null && !content.trim().isEmpty()
            && writer != null && !writer.trim().isEmpty();
    }
    
    /**
     * ファイル添付有無
     * @return ファイルが添付されていればtrue
     */
    public boolean hasFile() {
        return fileName != null && !fileName.isEmpty();
    }
    
    @Override
    public String toString() {
        return "BoardCreateRequest{" +
                "category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", writer='" + writer + '\'' +
                ", hasFile=" + hasFile() +
                '}';
    }
}