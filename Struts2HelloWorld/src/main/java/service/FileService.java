package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import model.FileInfo;

/**
 * 【FileServiceクラス】
 * ファイルアップロード・ダウンロード・検証を担当するサービス層
 * 
 * 役割:
 * - ファイルのバリデーション（サイズ、形式チェック）
 * - ファイルのアップロード処理
 * - ファイルのダウンロード処理
 * - ファイル関連の共通ロジック管理
 * 
 * 設計パターン: Singleton Pattern
 * - インスタンスを1つだけ生成
 * - メモリ効率化
 * - グローバルアクセスポイント提供
 */
public class FileService {
    
    private static final Logger logger = LogManager.getLogger(FileService.class);
    
    // ========== Singleton Pattern 実装 ==========
    
    /**
     * Singletonインスタンス（クラス変数）
     * - staticなので、クラスロード時に1回だけ生成される
     * - privateなので外部から直接アクセス不可
     */
    private static FileService instance = new FileService();
    
    /**
     * インスタンス取得メソッド
     * 
     * @return FileServiceの唯一のインスタンス
     * 
     * 【使用例】
     * FileService fileService = FileService.getInstance();
     */
    public static FileService getInstance() {
        return instance;
    }
    
    /**
     * privateコンストラクタ
     * - 外部からnew FileService()を防ぐ
     * - getInstance()経由でのみインスタンス取得可能
     */
    private FileService() {
        logger.info("【FileService】Singletonインスタンス生成");
    }
    
    // ========== 定数定義 ==========
    
    /**
     * アップロードディレクトリパス
     */
    private static final String UPLOAD_DIR = "/uploads";
    
    /**
     * 最大ファイルサイズ（10MB）
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    /**
     * 許可するファイル形式
     */
    private static final String[] ALLOWED_TYPES = {
        "image/jpeg",           // JPEG画像
        "image/png",            // PNG画像
        "image/gif",            // GIF画像
        "application/pdf",      // PDF文書
        "application/msword",   // Word文書（.doc）
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // Word（.docx）
        "application/vnd.ms-excel", // Excel（.xls）
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // Excel（.xlsx）
        "text/plain"            // テキストファイル
    };
    
    // ========== ファイルアップロード処理 ==========
    
    /**
     * ファイルをアップロードする
     * 
     * @param file アップロードファイル
     * @param contentType ファイルのContent-Type
     * @param fileName 元のファイル名
     * @return FileInfo アップロード情報（ファイル名、パス、サイズ）
     * @throws Exception バリデーションエラーまたは保存エラー
     * 
     * 【処理フロー】
     * 1. ファイルバリデーション（サイズ・形式チェック）
     * 2. アップロードディレクトリパス取得
     * 3. ファイル保存
     * 4. FileInfo返却
     */
    public FileInfo uploadFile(File file, String contentType, String fileName) throws Exception {
        logger.info("【FileService】ファイルアップロード開始: " + fileName);
        
        // 1. バリデーション
        validateFile(file, contentType);
        
        // 2. アップロードパス取得
        String uploadPath = getUploadPath();
        
        // 3. ファイル保存
        String savedFilePath = saveFile(file, fileName, uploadPath);
        
        // 4. FileInfo生成
        FileInfo fileInfo = new FileInfo(fileName, savedFilePath, file.length());
        
        logger.info("【FileService】アップロード成功: " + fileName + " → " + savedFilePath);
        
        return fileInfo;
    }
    
    /**
     * ファイルをバリデーションする
     * 
     * @param file アップロードファイル
     * @param contentType ファイルのContent-Type
     * @throws Exception バリデーションエラー
     * 
     * 【検証項目】
     * 1. ファイルサイズチェック（10MB以下）
     * 2. ファイル形式チェック（許可リストに含まれるか）
     */
    public void validateFile(File file, String contentType) throws Exception {
        // 1. ファイルサイズチェック
        if (file.length() > MAX_FILE_SIZE) {
            String errorMsg = "ファイルサイズが大きすぎます（最大10MB）";
            logger.warn("【FileService】" + errorMsg + ": " + file.length() + " bytes");
            throw new Exception(errorMsg);
        }
        
        // 2. ファイル形式チェック
        boolean isAllowed = false;
        for (String allowedType : ALLOWED_TYPES) {
            if (contentType.equals(allowedType)) {
                isAllowed = true;
                break;
            }
        }
        
        if (!isAllowed) {
            String errorMsg = "許可されていないファイル形式です: " + contentType;
            logger.warn("【FileService】" + errorMsg);
            throw new Exception(errorMsg);
        }
        
        logger.debug("【FileService】バリデーション成功: " + contentType + ", " + file.length() + " bytes");
    }
    
    /**
     * アップロードディレクトリのパスを取得
     * 
     * @return 実際のファイルシステムパス
     * 
     * 【処理内容】
     * 1. ServletContextから実際のパスを取得
     * 2. ディレクトリが存在しない場合は作成
     * 
     * 【例】
     * /uploads → /var/lib/tomcat9/webapps/StrutsHelloWorld/uploads
     */
    private String getUploadPath() {
        // Servlet Contextを通じて実際のパスを取得
        String realPath = ServletActionContext.getServletContext()
            .getRealPath(UPLOAD_DIR);
        
        // ディレクトリが存在しない場合は作成
        File uploadDir = new File(realPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
            logger.info("【FileService】ディレクトリ作成: " + realPath);
        }
        
        return realPath;
    }
    
    /**
     * ファイルを保存する
     * 
     * @param file アップロードファイル
     * @param originalFileName 元のファイル名
     * @param uploadPath 保存先ディレクトリパス
     * @return 保存されたファイルの相対パス
     * @throws Exception ファイル保存エラー
     * 
     * 【処理内容】
     * 1. UUIDで一意なファイル名を生成（重複防止）
     * 2. ファイルをコピー
     * 3. 相対パスを返却
     * 
     * 【ファイル名の例】
     * 元: report.pdf
     * 保存: 550e8400-e29b-41d4-a716-446655440000_report.pdf
     */
    private String saveFile(File file, String originalFileName, String uploadPath) throws Exception {
        // 1. 一意なファイル名生成（UUID + 元のファイル名）
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        
        // 2. 保存先ファイルオブジェクト作成
        File destFile = new File(uploadPath, uniqueFileName);
        
        // 3. ファイルコピー
        FileUtils.copyFile(file, destFile);
        
        logger.info("【FileService】ファイル保存完了: " + destFile.getAbsolutePath());
        
        // 4. 相対パス返却（/uploads/xxx.pdf）
        return UPLOAD_DIR + "/" + uniqueFileName;
    }
    
    // ========== ファイルダウンロード処理 ==========
    
    /**
     * ファイルをダウンロードする
     * 
     * @param filePath サーバー上のファイルパス（相対パス）
     * @param fileName ダウンロード時のファイル名
     * @param response HttpServletResponse
     * @throws Exception ファイル読み込みエラー
     * 
     * 【処理フロー】
     * 1. ファイルの存在確認
     * 2. レスポンスヘッダー設定
     * 3. ファイル転送
     */
    public void downloadFile(String filePath, String fileName, HttpServletResponse response) throws Exception {
        logger.info("【FileService】ファイルダウンロード開始: " + fileName);
        
        // 1. 実際のファイルパス取得
        String realPath = ServletActionContext.getServletContext().getRealPath(filePath);
        File file = new File(realPath);
        
        // 2. ファイル存在チェック
        if (!file.exists()) {
            String errorMsg = "ファイルが存在しません: " + filePath;
            logger.error("【FileService】" + errorMsg);
            throw new Exception(errorMsg);
        }
        
        // 3. レスポンスヘッダー設定
        response.setContentType("application/octet-stream");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", 
            "attachment; filename=\"" + 
            java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20") + 
            "\"");
        
        // 4. ファイル転送
        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            
            os.flush();
        }
        
        logger.info("【FileService】ダウンロード成功: " + fileName);
    }
    
    // ========== ユーティリティメソッド ==========
    
    /**
     * ファイルサイズを読みやすい形式に変換
     * 
     * @param fileSize ファイルサイズ（bytes）
     * @return フォーマットされた文字列
     * 
     * 【変換例】
     * 1024 → "1.0 KB"
     * 1048576 → "1.0 MB"
     * 1073741824 → "1.0 GB"
     */
    public String formatFileSize(long fileSize) {
        if (fileSize == 0) 
            return "0 B";
        if (fileSize < 1024) 
            return fileSize + " B";
        if (fileSize < 1024 * 1024) 
            return String.format("%.1f KB", fileSize / 1024.0);
        if (fileSize < 1024 * 1024 * 1024)
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
    }
    
    /**
     * ファイルの拡張子を取得
     * 
     * @param fileName ファイル名
     * @return 拡張子（ドット含む）
     * 
     * 【例】
     * "report.pdf" → ".pdf"
     * "image.jpeg" → ".jpeg"
     */
    public String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        
        return fileName.substring(lastDotIndex);
    }
    
    /**
     * ファイルが画像かどうかを判定
     * 
     * @param contentType Content-Type
     * @return 画像の場合true
     */
    public boolean isImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }
}