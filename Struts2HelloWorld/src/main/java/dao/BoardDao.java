package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.BoardData;
import util.DBConnection;

/**
 * 【Boardクラス】
 * PostgreSQLデータベースを使用した掲示板データ管理クラス
 * 
 * 役割:
 * - データベースへの接続とSQL実行
 * - CRUD操作（作成、読取、更新、削除）の実装
 * - 掲示板データの永続化
 * 
 * 変更点:
 * - 以前: static Vector<BoardData> でメモリに保存
 * - 現在: PostgreSQL データベースに保存
 * - メリット: サーバー再起動してもデータが消えない
 */
public class BoardDao {
    
	private static DBConnection dbConnection = DBConnection.getInstance();

    /**
     * 【addChatData メソッド】
     * 掲示板に新しい投稿を追加する
     * 
     * @param category カテゴリ（自由/お知らせ/質問/設問）
     * @param title タイトル
     * @param content 本文
     * @param writer 投稿者名
     * @param ipAddress 投稿者のIPアドレス
     * @param fileName ファイル名
     * @param filePath サーバーに保存されたファイルパス
     * @param fileSize ファイルサイズ(bytes) 
     * @return 成功時true、失敗時false
     * 
     * 処理の流れ:
     * 1. SQL INSERT文を準備
     * 2. データベースに接続
     * 3. パラメータを設定
     * 4. SQL実行
     * 5. 結果を確認
     * 6. リソースをクローズ
     */
    public static boolean addChatData(String category, String title, String content, 
            String writer, String ipAddress,String fileName, String filePath, long fileSize) {
        // INSERT文の準備
        // public.board_data: publicスキーマのboard_dataテーブル
        String sql = "INSERT INTO board_data (category, title, content, writer, ip_address, file_name, file_path, file_size) " +
                "VALUES (?, ?, ?, ?, ?::inet, ?, ?, ?)";
        /*
         * - inet
         * - PostgreSQL特有の型キャスト
         * - 文字列をINET型（IPアドレス型）に変換
         * 
         * 自動設定されるカラム:
         * - board_id: BIGSERIAL型なので自動採番
         * - created_at: DEFAULT CURRENT_TIMESTAMP（現在時刻）
         * - view_count, like_count DEFAULT 0
         * - is_notice, is_deleted DEFAULT FALSE
         */
        
        // リソース変数の宣言
        Connection conn = null;           // データベース接続
        PreparedStatement pstmt = null;   // SQL実行オブジェクト
        
        try {
            // 1. データベースに接続
            conn = dbConnection.getConnection();
            /*
             * DBConnection.getConnection()の動作:
             * - PostgreSQLドライバをロード
             * - jdbc:postgresql://localhost:5432/struts_board に接続
             * - 接続に成功するとConnectionオブジェクトを返す
             */
            
            // 2. SQL文を準備
            pstmt = conn.prepareStatement(sql);
            /*
             * PreparedStatement:
             * - SQLインジェクション対策
             * - 同じSQL文を複数回実行する場合に高速
             */
            
            // 3. パラメータを設定
            pstmt.setString(1, category);   // 1番目 → category
            pstmt.setString(2, title);      // 2番目 → title
            pstmt.setString(3, content);    // 3番目 → content
            pstmt.setString(4, writer);     // 4番目 → writer
            pstmt.setString(5, ipAddress);  // 5番目 → ipAddress
            pstmt.setString(6, fileName);
            pstmt.setString(7, filePath);
            pstmt.setLong(8, fileSize);
            
            /*
             * setString(index, value):
             * - index: 〇番の位置（1から始まる）
             * - value: 設定する値
             * - 自動的にエスケープ処理される（安全）
             */
            
            // 4. SQL実行
            int result = pstmt.executeUpdate();
            /*
             * executeUpdate():
             * - INSERT, UPDATE, DELETEの実行用
             * - 戻り値: 影響を受けた行数
             * - INSERT成功時は通常1が返る
             */
            
            // 5. 結果を確認して返す
            return result > 0;  // 1行以上挿入されたら成功
            
        } catch (SQLException e) {
            // SQLエラーが発生した場合
            System.out.println("addChatData SQLException エラー:");
            e.printStackTrace();  // エラー内容をコンソールに出力
            return false;         // 失敗を返す
            
        } finally {
            // 必ず実行される部分（成功・失敗に関わらず）
            closeResources(conn, pstmt, null);
            /*
             * リソースのクローズ:
             * - データベース接続は限られたリソース
             * - 使い終わったら必ずクローズする必要がある
             * - finallyブロックで確実にクローズ
             */
        }
    }
    
    /**
     * 既存のメソッド (ファイルなしで投稿) - 下位互換性を維持
     */
    public static boolean addChatData(String category, String title, String content,
                                     String writer, String ipAddress) {
        return addChatData(category, title, content, writer, ipAddress, null, null, 0);
    }
    
    /**
     * 【getChatData メソッド】
     * 掲示板データ全体を取得（新しい順）
     * 
     * @return 投稿データのリスト（List<BoardData>）
     * 
     * 処理の流れ:
     * 1. SQL SELECT文を準備
     * 2. データベースに接続
     * 3. SQL実行
     * 4. 結果セットからデータを取得
     * 5. BoardDataオブジェクトに変換してリストに追加
     * 6. リソースをクローズ
     * 7. リストを返す
     */
    public static List<BoardData> getChatData() {

    	String sql = "SELECT * " +
                "FROM board_data " +
                "WHERE is_deleted = FALSE " +
                "ORDER BY CASE WHEN category = 'お知らせ' THEN 0 ELSE 1 END, " +
                "created_at DESC";
  
        
        // リソース変数の宣言
        Connection conn = null;      // データベース接続
        Statement stmt = null;       // SQL実行オブジェクト
        ResultSet rs = null;         // 検索結果
        List<BoardData> dataList = new ArrayList<>();  // 結果を格納するリスト
        /*
         * ArrayList<BoardData>:
         * - BoardDataオブジェクトを格納する可変長配列
         * - 初期状態は空
         * - データを取得するたびにadd()で追加
         */
        
        try {
            // 1. データベースに接続
            conn = dbConnection.getConnection();
            
            // 2. Statementオブジェクト作成
            stmt = conn.createStatement();
            /*
             * Statement vs PreparedStatement:
             * - Statement: パラメータなしのSQL用
             * - PreparedStatement: パラメータありのSQL用
             * 今回はないのでStatementを使用
             */
            
            // 3. SQL実行
            rs = stmt.executeQuery(sql);
            /*
             * executeQuery():
             * - SELECT文の実行用
             * - 戻り値: ResultSet（検索結果）
             * - ResultSetは表形式のデータ
             */
            
            // 4. 結果セットからデータを取得（ループ）
            while (rs.next()) {
                /*
                 * rs.next():
                 * - 次の行に移動
                 * - データがあればtrue、なければfalse
                 * - 最初の呼び出しで1行目に移動
                 */
                
                // 新しいBoardDataオブジェクトを作成
                BoardData data = new BoardData();
                
                // ResultSetから各カラムの値を取得してセット
                data.setBoardId(rs.getLong("board_id"));                
                /*
                 * rs.getLong("カラム名"):
                 * - 指定されたカラムの値をlong型で取得
                 * - カラム名で指定（インデックスでも可）
                 */
                
                data.setCategory(rs.getString("category"));
                data.setTitle(rs.getString("title"));
                data.setContent(rs.getString("content"));
                data.setWriter(rs.getString("writer"));
                /*
                 * rs.getString("カラム名"):
                 * - 指定されたカラムの値をString型で取得
                 */

                // カウント
                data.setViewCount(rs.getInt("view_count"));
                data.setLikeCount(rs.getInt("like_count"));
                data.setDislikeCount(rs.getInt("dislike_count"));
                /*
                 * rs.getInt("カラム名"):
                 * - INTEGER型の値を取得
                 */
                
                // IPアドレス
                data.setIpAddress(rs.getString("ip_address"));
                
                // Boolean型のフラグ
                data.setSecret(rs.getBoolean("is_secret"));
                data.setDeleted(rs.getBoolean("is_deleted"));
                
                // File関連
                data.setFileName(rs.getString("file_name"));
                data.setFilePath(rs.getString("file_path"));
                data.setFileSize(rs.getLong("file_size"));
                
                // Timestamp型で取得（日付時刻）
                data.setCreatedAt(rs.getTimestamp("created_at"));
                data.setUpdatedAt(rs.getTimestamp("updated_at"));
                /*
                 * Timestamp型:
                 * - データベースの日時型
                 * - toString()で文字列に変換
                 */
                
                // リストに追加
                dataList.add(data);
            }
            
        } catch (SQLException e) {
            // SQLエラーが発生した場合
            System.out.println("addChatData SQLException エラー");
            e.printStackTrace();
            
        } finally {
            // リソースをクローズ
            closeResources(conn, stmt, rs);
        }
        
        // 取得したリストを返す（空の場合もあり）
        return dataList;
    }
    
    /**
     * 【getDataById メソッド】
     * ID指定で1件の投稿データを取得
     * 
     * @param id 取得したい投稿のID
     * @return 見つかった投稿データ、見つからない場合はnull
     */
    public static BoardData getDataById(long id) {
        // WHERE句でID指定
    	String sql = "SELECT * FROM board_data WHERE board_id = ? AND is_deleted = FALSE";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // 1. データベースに接続
            conn = dbConnection.getConnection();
            
            // 2. SQL準備
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);  // ?にIDを設定
            /*
             * setInt(index, value):
             * - int型の値を設定
             * - WHERE id = 123 のようになる
             */
            
            // 3. SQL実行
            rs = pstmt.executeQuery();
            
            // 4. 結果を確認（1件のみ取得）
            if (rs.next()) {
                // データが見つかった場合
                BoardData data = new BoardData();
                data.setBoardId(rs.getLong("board_id"));
                data.setCategory(rs.getString("category"));
                data.setTitle(rs.getString("title"));
                data.setContent(rs.getString("content"));
                data.setWriter(rs.getString("writer"));
                data.setViewCount(rs.getInt("view_count"));
                data.setLikeCount(rs.getInt("like_count"));
                data.setDislikeCount(rs.getInt("dislike_count"));
                data.setIpAddress(rs.getString("ip_address"));
                data.setSecret(rs.getBoolean("is_secret"));
                data.setDeleted(rs.getBoolean("is_deleted"));
                data.setFileName(rs.getString("file_name"));
                data.setFilePath(rs.getString("file_path"));
                data.setFileSize(rs.getLong("file_size"));
                data.setCreatedAt(rs.getTimestamp("created_at"));
                data.setUpdatedAt(rs.getTimestamp("updated_at"));
                
                return data;  // 見つかったデータを返す
            }
            // rs.next()がfalseの場合、データなし
            
        } catch (SQLException e) {
            System.out.println("getDataById SQLException エラー");
            e.printStackTrace();
            
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        // データが見つからなかった場合
        return null;
    }
    
    /**
     * 【updateData メソッド】
     * 投稿を更新する
     * 
     * @param id 更新対象の投稿ID
     * @param category カテゴリ
     * @param title タイトル
     * @param content 本文
     * @param writer 作成者名
     * @param fileName ファイル名
     * @param filePath サーバーに保存されたファイルパス
     * @param fileSize ファイルサイズ(bytes)
     * @return 成功時true、失敗時false
     * 
     * 処理内容:
     * - 指定されたIDの投稿を更新
     * - post_dateも現在時刻に更新される
     */
    public static boolean updateData(long boardId, String category, String title, 
            String content, String writer, String fileName, String filePath, long fileSize) {
        // UPDATE文の準備
        // WHERE id = ?: 指定されたIDの行のみ更新
    	
    	String sql;
    	
    	if (fileName != null && !fileName.isEmpty()) {
    		sql = "UPDATE board_data SET category = ?, title = ?, content = ?, " +
                    "writer = ?, file_name = ?, file_path = ?, file_size = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE board_id = ? AND is_deleted = FALSE";
    	} else {
    		sql = "UPDATE board_data SET category = ?, title = ?, content = ?, " +
                    "writer = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE board_id = ? AND is_deleted = FALSE";
    	}
    	
        /*
         * UPDATE文の構造:
         * UPDATE テーブル名 SET カラム1 = 値1, カラム2 = 値2 WHERE 条件
         * WHERE句がないと全データが更新されるので注意！
         */
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // 1. データベースに接続
            conn = dbConnection.getConnection();
            
            // 2. SQL準備
            pstmt = conn.prepareStatement(sql);
            
            // 3. パラメータ設定
            pstmt.setString(1, category);  // SET category = ?
            pstmt.setString(2, title);     // SET title = ?
            pstmt.setString(3, writer);    // SET writer = ?
            pstmt.setString(4, content);   // SET content = ?
            
            if (fileName != null && !fileName.isEmpty()) {
                pstmt.setString(5, fileName);
                pstmt.setString(6, filePath);
                pstmt.setLong(7, fileSize);
                pstmt.setLong(8, boardId);
            } else {
                pstmt.setLong(5, boardId);
            }
            
            /*
             * パラメータの順序に注意:
             * SQL文の順番通りに設定する必要がある
             */
            
            // 4. SQL実行
            int result = pstmt.executeUpdate();
            /*
             * executeUpdate()の戻り値:
             * - 更新された行数
             * - IDが存在する場合: 1
             * - IDが存在しない場合: 0
             */
            
            // 5. 結果を返す
            return result > 0;  // 1行以上更新されたら成功
            
        } catch (SQLException e) {
            System.out.println("updateData SQLException エラー");
            e.printStackTrace();
            return false;
            
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * 既存のメソッド (ファイルなしで更新)
     */
    public static boolean updateData(long boardId, String category, String title,
                                    String content, String writer) {
        return updateData(boardId, category, title, content, writer, null, null, 0);
    }
    
    /**
     * 【deleteData メソッド】
     * 投稿を削除する
     * 
     * @param id 削除対象の投稿ID
     * @return 成功時true、失敗時false
     */
    public static boolean deleteData(long id) {
        // DELETE文の準備
        String sql = "UPDATE board_data SET is_deleted = TRUE " +
                "WHERE board_id = ? AND is_deleted = FALSE";
        /*
         * DELETE文の構造:
         * DELETE FROM テーブル名 WHERE 条件
         * WHERE句がないと全データが削除されるので必須！
         */
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // 1. データベースに接続
            conn = dbConnection.getConnection();
            
            // 2. SQL準備
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);  // WHERE id = ?
            
            // 3. SQL実行
            int result = pstmt.executeUpdate();
            /*
             * executeUpdate()の戻り値:
             * - 削除された行数
             * - 削除成功: 1
             * - IDが存在しない: 0
             */
            
            // 4. 結果を返す
            return result > 0;
            
        } catch (SQLException e) {
            System.out.println("deleteData SQLException エラー");
            e.printStackTrace();
            return false;
            
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * 【incrementViewCount メソッド】
     * 閲覧数を1増やす
     * 
     * @param id 対象の投稿ID
     * 
     * 使用箇所:
     * - 詳細画面を表示した時に呼ばれる
     * - 閲覧数をカウントアップ
     */
    public static void incrementViewCount(long id) {
        // view_count を +1 するSQL
        String sql = "UPDATE board_data SET view_count = view_count + 1 " +
                "WHERE board_id = ? AND is_deleted = FALSE";
        /*
         * view_count = view_count + 1:
         * - 現在の値に1を加算
         * - 例: 5 → 6
         */
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            
            pstmt.executeUpdate();
            // 戻り値をチェックしない（失敗してもエラー表示のみ）
            
        } catch (SQLException e) {
            System.out.println("incrementViewCount SQLException エラー");
            e.printStackTrace();
            
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * 【closeResources メソッド】
     * データベース関連のリソースをクローズする
     * @param conn データベース接続
     * @param stmt Statement または PreparedStatement
     * @param rs ResultSet
     * クローズの順序:
     * 1. ResultSet (検索結果)
     * 2. Statement (SQL実行オブジェクト)
     * 3. Connection (データベース接続)
     * → 開いた順と逆にクローズ
     */
    private static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        // ResultSetをクローズ
        if (rs != null) {
            /*
             * null チェック:
             * - SELECT文以外ではrsはnull
             * - nullの場合にclose()を呼ぶとエラーになる
             */
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        // Statementをクローズ
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        // Connectionをクローズ
        dbConnection.closeConnection(conn);
        /*
         * DBConnection.closeConnection():
         * - null チェックと例外処理を含む
         * - 確実にクローズしてくれる
         */
    }
}