package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
public class Board {
    
    /**
     * 【addChatData メソッド】
     * 掲示板に新しい投稿を追加する
     * 
     * @param title タイトル
     * @param name 投稿者名
     * @param message メッセージ本文
     * @param remoteAddress 投稿者のIPアドレス
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
    public static boolean addChatData(String title, String name, String message, String remoteAddress) {
        // INSERT文の準備
        // public.board_data: publicスキーマのboard_dataテーブル
        // ?: プレースホルダー（後で値を設定）
        String sql = "INSERT INTO public.board_data (title, name, message, remote_address, post_date, view_count) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, 0)";
        /*
         * CURRENT_TIMESTAMP: 現在日時を自動設定
         * view_count: 初期値0
         * id: SERIAL型なので自動採番される
         */
        
        // リソース変数の宣言
        Connection conn = null;           // データベース接続
        PreparedStatement pstmt = null;   // SQL実行オブジェクト
        
        try {
            // 1. データベースに接続
            conn = DBConnection.getConnection();
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
             * - ?の部分にパラメータを安全に設定できる
             * - 同じSQL文を複数回実行する場合に高速
             */
            
            // 3. パラメータを設定（?の部分に値を入れる）
            pstmt.setString(1, title);         // 1番目 → title
            pstmt.setString(2, name);          // 2番目 → name
            pstmt.setString(3, message);       // 3番目 → message
            pstmt.setString(4, remoteAddress); // 4番目 → remoteAddress
            /*
             * setString(index, value):
             * - index: ?の位置（1から始まる）
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
        // SELECT文の準備
        // ORDER BY post_date DESC: 投稿日時の降順（新しい順）
        String sql = "SELECT * FROM public.board_data ORDER BY post_date DESC";
        
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
            conn = DBConnection.getConnection();
            
            // 2. Statementオブジェクト作成
            stmt = conn.createStatement();
            /*
             * Statement vs PreparedStatement:
             * - Statement: パラメータなしのSQL用
             * - PreparedStatement: パラメータありのSQL用
             * 今回は?がないのでStatementを使用
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
                data.setId(rs.getInt("id"));
                /*
                 * rs.getInt("カラム名"):
                 * - 指定されたカラムの値をint型で取得
                 * - カラム名で指定（インデックスでも可）
                 */
                
                data.setTitle(rs.getString("title"));
                data.setName(rs.getString("name"));
                data.setMessage(rs.getString("message"));
                /*
                 * rs.getString("カラム名"):
                 * - 指定されたカラムの値をString型で取得
                 */
                
                // Timestamp型 → String型に変換
                Timestamp ts = rs.getTimestamp("post_date");
                data.setPostDate(ts.toString());
                /*
                 * Timestamp型:
                 * - データベースの日時型
                 * - toString()で文字列に変換
                 * - 形式: "2025-10-10 18:25:26.123"
                 */
                
                data.setRemoteAddress(rs.getString("remote_address"));
                data.setViewCount(rs.getInt("view_count"));
                
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
    public static BoardData getDataById(int id) {
        // WHERE句でID指定
        String sql = "SELECT * FROM public.board_data WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // 1. データベースに接続
            conn = DBConnection.getConnection();
            
            // 2. SQL準備
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);  // ?にIDを設定
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
                data.setId(rs.getInt("id"));
                data.setTitle(rs.getString("title"));
                data.setName(rs.getString("name"));
                data.setMessage(rs.getString("message"));
                
                Timestamp ts = rs.getTimestamp("post_date");
                data.setPostDate(ts.toString());
                
                data.setRemoteAddress(rs.getString("remote_address"));
                data.setViewCount(rs.getInt("view_count"));
                
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
     * @param title 新しいタイトル
     * @param name 新しい投稿者名
     * @param message 新しいメッセージ
     * @return 成功時true、失敗時false
     * 
     * 処理内容:
     * - 指定されたIDの投稿を更新
     * - post_dateも現在時刻に更新される
     */
    public static boolean updateData(int id, String title, String name, String message) {
        // UPDATE文の準備
        // WHERE id = ?: 指定されたIDの行のみ更新
        String sql = "UPDATE public.board_data SET title = ?, name = ?, message = ?, " +
                     "post_date = CURRENT_TIMESTAMP WHERE id = ?";
        /*
         * UPDATE文の構造:
         * UPDATE テーブル名 SET カラム1 = 値1, カラム2 = 値2 WHERE 条件
         * WHERE句がないと全データが更新されるので注意！
         */
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // 1. データベースに接続
            conn = DBConnection.getConnection();
            
            // 2. SQL準備
            pstmt = conn.prepareStatement(sql);
            
            // 3. パラメータ設定
            pstmt.setString(1, title);    // SET title = ?
            pstmt.setString(2, name);     // SET name = ?
            pstmt.setString(3, message);  // SET message = ?
            pstmt.setInt(4, id);          // WHERE id = ?
            /*
             * パラメータの順序に注意:
             * SQL文の?の順番通りに設定する必要がある
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
     * 【deleteData メソッド】
     * 投稿を削除する
     * 
     * @param id 削除対象の投稿ID
     * @return 成功時true、失敗時false
     */
    public static boolean deleteData(int id) {
        // DELETE文の準備
        String sql = "DELETE FROM public.board_data WHERE id = ?";
        /*
         * DELETE文の構造:
         * DELETE FROM テーブル名 WHERE 条件
         * WHERE句がないと全データが削除されるので必須！
         */
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // 1. データベースに接続
            conn = DBConnection.getConnection();
            
            // 2. SQL準備
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);  // WHERE id = ?
            
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
    public static void incrementViewCount(int id) {
        // view_count を +1 するSQL
        String sql = "UPDATE public.board_data SET view_count = view_count + 1 WHERE id = ?";
        /*
         * view_count = view_count + 1:
         * - 現在の値に1を加算
         * - 例: 5 → 6
         */
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
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
        DBConnection.closeConnection(conn);
        /*
         * DBConnection.closeConnection():
         * - null チェックと例外処理を含む
         * - 確実にクローズしてくれる
         */
    }
}