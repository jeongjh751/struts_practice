package util;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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
    private static DBConnection instance;
    
    private HikariDataSource dataSource; // pool管理者
    
    /**
     * privateコンストラクタ
     * - 外部からnew DBConnection()を防ぐ
     * - JDBCドライバのロード
     */
    private DBConnection() {
        try {
            // PostgreSQL JDBCドライバをロード
            Class.forName("org.postgresql.Driver");
            logger.debug("【DBConnection】PostgreSQL Driverロード成功");	
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/struts_board");
            config.setUsername("postgres");
            config.setPassword("1234");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            
            // Pool設定
            config.setMaximumPoolSize(10);        // 最大10個のConnection
            config.setMinimumIdle(5);             // 最小5つ維持
            config.setConnectionTimeout(30000);   // Connction待機30秒
            config.setIdleTimeout(600000);        // 未使用場合10分後除去
            
            this.dataSource = new HikariDataSource(config);
            logger.debug("【ConnecionPool】HikariCP 初期化 成功");	
        } catch (ClassNotFoundException e) {
            logger.error("【DBConnection】PostgreSQL Driver not found", e);
            throw new RuntimeException("PostgreSQL Driver not found", e);
        }
    }
    
    // getInstance: DBConnection Singletion返還
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
    
    // getConnection: PoolからConnectionを借りてくる
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    // closeConnection: PoolにConnection返却
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();  // 実際はPoolに返却
                logger.debug("【ConnecionPool】HikariCP Connection返却");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public HikariDataSource getDataSource() {
        return this.dataSource;
    }
    
    // shutdown: アプリケーション終了時にPoolを整理
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.debug("【ConnecionPool】HikariCP Connection終了");
        }
    }
}