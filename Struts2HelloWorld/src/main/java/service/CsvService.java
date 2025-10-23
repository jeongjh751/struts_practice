package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * CSV関連の共通処理を担当するサービス層
 * 
 * 役割:
 * - CSVファイルの読み込み・解析
 * - CSVファイルの生成・出力
 * - CSV形式のバリデーション
 */
public class CsvService {
    
    private static final Logger logger = LogManager.getLogger(CsvService.class);
    
    /**
     * CSVファイルをMap形式のリストに変換
     * 
     * @param csvFile CSVファイル
     * @return List<Map<String, String>> - 各行がMapになったリスト
     * @throws Exception 解析エラー
     */
    public List<Map<String, String>> parseCsvToMapList(File csvFile) throws Exception {
        logger.info("【CsvService】CSV解析開始: " + csvFile.getName());
        
        List<Map<String, String>> dataList = new ArrayList<>();
        Reader reader = null;
        CSVParser csvParser = null;
        
        try {
            // UTF-8でCSVファイルを読み込み
            reader = new InputStreamReader(new FileInputStream(csvFile), "UTF-8");
            
            // CSVフォーマット設定
            CSVFormat csvFormat = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()   // 1行目をヘッダーとして扱う
                .withIgnoreEmptyLines()      // 空行を無視
                .withTrim();                 // 前後の空白を削除
            
            csvParser = new CSVParser(reader, csvFormat);
            
            // 各レコードをMapに変換
            for (CSVRecord record : csvParser) {
                Map<String, String> rowMap = new HashMap<>();
                
                // ヘッダー名をキーとしてMapに格納
                for (String headerName : csvParser.getHeaderNames()) {
                    rowMap.put(headerName, record.get(headerName));
                }
                
                dataList.add(rowMap);
            }
            
            logger.info("【CsvService】CSV解析完了: " + dataList.size() + "件");
            
        } finally {
            if (csvParser != null) csvParser.close();
            if (reader != null) reader.close();
        }
        
        return dataList;
    }
    
    /**
     * 必須ヘッダーが存在するか検証
     * 
     * @param csvFile CSVファイル
     * @param requiredHeaders 必須ヘッダーリスト
     * @return true: 全て存在, false: 不足あり
     * @throws Exception ファイル読み込みエラー
     */
    public boolean validateCsvHeaders(File csvFile, List<String> requiredHeaders) 
            throws Exception {
        logger.info("【CsvService】CSVヘッダー検証開始");
        
        Reader reader = null;
        CSVParser csvParser = null;
        
        try {
            reader = new InputStreamReader(new FileInputStream(csvFile), "UTF-8");
            CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();
            csvParser = new CSVParser(reader, csvFormat);
            
            // ヘッダー名取得
            List<String> actualHeaders = csvParser.getHeaderNames();
            
            // 必須ヘッダーチェック
            for (String required : requiredHeaders) {
                if (!actualHeaders.contains(required)) {
                    logger.error("【CsvService】必須ヘッダー不足: " + required);
                    return false;
                }
            }
            
            logger.info("【CsvService】CSVヘッダー検証OK");
            return true;
            
        } finally {
            if (csvParser != null) csvParser.close();
            if (reader != null) reader.close();
        }
    }
    
    /**
     * データをCSV形式でOutputStreamに出力（汎用）
     * 
     * @param outputStream 出力先
     * @param headers CSVヘッダー
     * @param dataList データリスト（各要素はString配列）
     * @throws Exception 出力エラー
     */
    public void exportToCsv(OutputStream outputStream, String[] headers, 
                           List<String[]> dataList) throws Exception {
        logger.info("【CsvService】CSV出力開始: " + dataList.size() + "件");
        
        Writer writer = null;
        CSVPrinter csvPrinter = null;
        
        try {
            // UTF-8 BOM追加（Excel対応）
            outputStream.write(0xEF);
            outputStream.write(0xBB);
            outputStream.write(0xBF);
            
            // CSVWriter初期化
            writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headers);
            csvPrinter = new CSVPrinter(writer, csvFormat);
            
            // データ出力
            for (String[] row : dataList) {
                csvPrinter.printRecord((Object[]) row);
            }
            
            csvPrinter.flush();
            logger.info("【CsvService】CSV出力完了");
            
        } finally {
            if (csvPrinter != null) csvPrinter.close();
            if (writer != null) writer.close();
        }
    }
    
    /**
     * タイムスタンプ付きファイル名生成
     * 
     * @param prefix ファイル名のプレフィックス（例: "board_data"）
     * @return タイムスタンプ付きファイル名
     */
    public String generateFileName(String prefix) {
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss")
            .format(new java.util.Date());
        return prefix + "_" + timestamp + ".csv";
    }
}