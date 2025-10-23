package dto.response;

import java.util.ArrayList;
import java.util.List;

/**
 * CSV インポート結果を保持するDTO
 * 
 * 役割:
 * - インポート成功件数
 * - インポート失敗件数
 * - エラーメッセージリスト
 */
public class CsvImportResponse {
    
    private int successCount = 0;
    private int failCount = 0;
    private List<String> errorMessages = new ArrayList<>();
    
    public void incrementSuccessCount() {
        this.successCount++;
    }
    
    public void incrementFailCount() {
        this.failCount++;
    }
    
    public void addError(String errorMessage) {
        this.errorMessages.add(errorMessage);
    }
    
    public int getSuccessCount() {
        return successCount;
    }
    
    public int getFailCount() {
        return failCount;
    }
    
    public List<String> getErrorMessages() {
        return errorMessages;
    }
    
    public boolean hasErrors() {
        return failCount > 0;
    }
    
    public int getTotalCount() {
        return successCount + failCount;
    }
}