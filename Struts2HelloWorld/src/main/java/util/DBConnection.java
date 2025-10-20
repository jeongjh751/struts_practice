package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 【DBConnectionクラス】
 * データベース接続を管理するクラス
 * 
 * 設計パターン: Singleton Pattern
 * - 接続情報を1つのインスタンスで管理
 * - メモリ効率化
 * - 設定変更時の管理が容易
 * 
 * 注意:
 * - このクラスはSingleton、Connectionは毎回新規作成
 * - Connection自体のプーリングはTomcatのDataSourceで行う方が良い
 */
public class DBConnection {
    
    private static final Logger logger = LogManager.getLogger(DBConnection.class);
    
    // ========== Singleton Pattern 実装 ==========
    
    /**
     * Singletonインスタンス
     * - クラスロード時に1回だけ生成
     */
    private static DBConnection instance = new DBConnection();
    
    /**
     * インスタンス取得メソッド
     * 
     * @return DBConnectionの唯一のインスタンス
     */
    public static DBConnection getInstance() {
        return instance;
    }
    
    /**
     * privateコンストラクタ
     * - 外部からnew DBConnection()を防ぐ
     * - JDBCドライバのロード
     */
    private DBConnection() {
        try {
            // PostgreSQL JDBCドライバをロード
            Class.forName("org.postgresql.Driver");
            logger.info("【DBConnection】PostgreSQL Driverロード成功");
        } catch (ClassNotFoundException e) {
            logger.error("【DBConnection】PostgreSQL Driver not found", e);
            throw new RuntimeException("PostgreSQL Driver not found", e);
        }
    }
    
    // ========== データベース接続情報 ==========
    
    /**
     * データベースURL
     */
    private static final String URL = "jdbc:postgresql://localhost:5432/struts_board";
    
    /**
     * データベースユーザー名
     */
    private static final String USER = "postgres";
    
    /**
     * データベースパスワード
     */
    private static final String PASSWORD = "1234";
    
    // ========== 接続メソッド ==========
    
    /**
     * データベース接続を取得（インスタンスメソッド）
     * 
     * @return Connection
     * @throws SQLException 接続エラー
     * 
     * 【使用例】
     * DBConnection dbConn = DBConnection.getInstance();
     * Connection conn = dbConn.getConnection();
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.debug("【DBConnection】接続成功");
            return conn;
        } catch (SQLException e) {
            logger.error("【DBConnection】接続失敗: " + e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * データベース接続を取得（staticメソッド - 既存コード互換性維持）
     * 
     * @return Connection
     * @throws SQLException 接続エラー
     * 
     * 【使用例】（既存コードと同じ）
     * Connection conn = DBConnection.getConnection();
     * 
     * 注意: このメソッドは下位互換性のために残す
     */
    public static Connection getConnectionStatic() throws SQLException {
        return instance.getConnection();
    }
    
    /**
     * 接続をクローズ
     * 
     * @param conn クローズするConnection
     */
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                logger.debug("【DBConnection】接続クローズ成功");
            } catch (SQLException e) {
                logger.error("【DBConnection】接続クローズ失敗: " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * 接続をクローズ（staticメソッド - 既存コード互換性維持）
     * 
     * @param conn クローズするConnection
     */
    public static void closeConnectionStatic(Connection conn) {
        instance.closeConnection(conn);
    }
    
    // ========== 設定情報取得メソッド ==========
    
    /**
     * データベースURL取得
     * 
     * @return データベースURL
     */
    public String getUrl() {
        return URL;
    }
    
    /**
     * データベースユーザー名取得
     * 
     * @return ユーザー名
     */
    public String getUser() {
        return USER;
    }
    
    /**
     * 接続テスト
     * 
     * @return 接続成功時true
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            logger.info("【DBConnection】接続テスト成功");
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            logger.error("【DBConnection】接続テスト失敗", e);
            return false;
        }
    }
}