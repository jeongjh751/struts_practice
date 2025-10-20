package model;

/**
 * 【FileInfoクラス】
 * ファイル情報を保持するデータ転送オブジェクト（DTO）
 * 
 * 役割:
 * - アップロードされたファイルの情報を保持
 * - ServiceとActionの間でファイル情報を受け渡し
 * - データベースに保存するファイル情報を管理
 */
public class FileInfo {
    
    /**
     * 元のファイル名
     * 例: "report.pdf"
     */
    private String fileName;
    
    /**
     * サーバーに保存されたファイルパス
     * 例: "/uploads/550e8400-e29b-41d4-a716-446655440000_report.pdf"
     */
    private String filePath;
    
    /**
     * ファイルサイズ（bytes）
     * 例: 1048576 (1MB)
     */
    private long fileSize;
    
    // ========== コンストラクタ ==========
    
    /**
     * デフォルトコンストラクタ
     */
    public FileInfo() {
    }
    
    /**
     * 全フィールドを初期化するコンストラクタ
     * 
     * @param fileName 元のファイル名
     * @param filePath 保存先パス
     * @param fileSize ファイルサイズ
     */
    public FileInfo(String fileName, String filePath, long fileSize) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }
    
    // ========== Getter/Setter ==========
    
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
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    // ========== ユーティリティメソッド ==========
    
    /**
     * ファイルが存在するかチェック
     * 
     * @return ファイル名が設定されていればtrue
     */
    public boolean hasFile() {
        return fileName != null && !fileName.isEmpty();
    }
    
    /**
     * ファイルサイズを読みやすい形式で返す
     * 
     * @return フォーマットされたファイルサイズ
     */
    public String getFormattedFileSize() {
        if (fileSize == 0) 
            return "";
        if (fileSize < 1024) 
            return fileSize + " B";
        if (fileSize < 1024 * 1024) 
            return String.format("%.1f KB", fileSize / 1024.0);
        return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
    }
    
    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }
}