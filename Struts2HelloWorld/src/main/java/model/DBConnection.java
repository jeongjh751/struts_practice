package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * データベース接続を管理するクラス
 */
public class DBConnection {
    
    // データベース接続情報
    private static final String URL = "jdbc:postgresql://localhost:5432/struts_board";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";  // 自分のパスワードに変更
    
    /**
     * データベース接続を取得
     * @return Connection
     */
    public static Connection getConnection() throws SQLException {
        try {
            // PostgreSQL JDBCドライバをロード
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver not found", e);
        }
        
        // 接続を返す
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    /**
     * 接続をクローズ
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}