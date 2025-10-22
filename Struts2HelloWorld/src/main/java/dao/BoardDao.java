package dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import entity.BoardEntity;
import mapper.BoardMapper;
import util.MyBatisUtil;

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
    
	private static final Logger logger = LogManager.getLogger(BoardDao.class);
	
    /**
     * 【insert メソッド】
     * 掲示板に新しい投稿を追加する
     * 
     * inet
     * PostgreSQL特有の型キャスト
     * 文字列をINET型（IPアドレス型）に変換
     * 
     * 自動設定されるカラム:
     * board_id: BIGSERIAL型なので自動採番
     * created_at: DEFAULT CURRENT_TIMESTAMP（現在時刻）
     * view_count, like_count, dislike_count DEFAULT 0
     * is_secret, is_deleted DEFAULT FALSE
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
     */
    public static boolean insert(String category, String title, String content, 
            String writer, String ipAddress,String fileName, String filePath, Long fileSize) {
    	    	logger.info("【BoardDao】insertメソッド開始");
    	// SqlSession: MyBatisのDB接続管理object
        // JDBCのConnectionに相当
    	SqlSession sqlSession = null;
        
    	try {
        	// 1. SqlSession取得 (autoCommit = false)
            sqlSession = MyBatisUtil.getSqlSession();
            
            // 2. Mapper取得
            BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
            
            // 3. INSERT実行
            int result = mapper.insert(category, title, content, writer, 
                                       ipAddress, fileName, filePath, fileSize);
            
            // 4. commit
            sqlSession.commit();
            
            logger.debug("【BoardDao】insert 成功: " + result + "件");
            
            // 5. 結果を返す
            return result > 0;
            
        } catch (Exception e) {
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            logger.error("【BoardDao】insert エラー", e);
            return false;
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }

    }
    
    /**
     * 【findAll メソッド】
     * 掲示板データ全体を取得（新しい順）
     * 
     * 処理の流れ:
     * 1. SQL SELECT文を準備
     * 2. データベースに接続
     * 3. SQL実行
     * 4. 結果セットからデータを取得
     * 5. BoardDataオブジェクトに変換してリストに追加
     * 6. リソースをクローズ
     * 7. リストを返す
     * 
     * JDBC->MyBatis変更
     * MyBatis方式
     * 1. SqlSession取得
     * 2. Mapper取得
     * 3. メソッド呼び出し → 自動でList<Entity>に変換
     * 4. SqlSessionクローズ
     * 
     * @return 投稿データのリスト（List<BoardEntity>）
     */
    public static List<BoardEntity> findAll() {
    	
    	SqlSession sqlSession = null;
        
    	try {
        	// 1. SqlSessionを取得
            sqlSession = MyBatisUtil.getSqlSession();
            // 2. Mapperインターフェースを取得
            BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
            // 3. メソッド呼び出し
            return mapper.findAll();
            
        } catch (Exception e) {
            logger.error("【BoardDao】findAll エラー", e);
            return null;
        } finally {
        	// リソースをクローズ
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * Mapper.xmlLに#{boardId}と記述
     * 自動的にパラメータがバインドされる
     * 
     * @param id 取得したい投稿のID
     * @return 見つかった投稿データ、見つからない場合はnull
     */
    public static BoardEntity findById(long boardId) {
    	
    	SqlSession sqlSession = null;
        
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
            
            // IDを渡すだけでSQL実行とマッピングが完了
            return mapper.findById(boardId);
            /*
             * #{boardId}の仕組み:
             * - XMLで#{boardId}と記述
             * - MyBatisが自動的にPreparedStatementのパラメータに設定
             * - SQLインジェクション対策も自動
             */
            
        } catch (Exception e) {
            logger.error("【BoardDao】findById エラー", e);
            return null;
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }

    }
    
    /**
     * 【updateData メソッド】
     * 投稿を更新する
     * 
     * 更新条件
     * board_id = #{boardId}
     * is_deleted = FALSE (削除済みは更新不可)
     * MyBatis方式
     * パラメータを渡すだけ
     * XMLで更新条件を記述
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
     */
    public static boolean update(long boardId, String category, String title, 
    		String writer, String content, String fileName, String filePath, Long fileSize) {
        
    	logger.info("【BoardDao】updateメソッド開始 boardId: " + boardId);
        
        SqlSession sqlSession = null;
        
        try {
            // 1. SqlSession取得 (autoCommit = false)
            sqlSession = MyBatisUtil.getSqlSession();
            
            // 2. Mapper取得
            BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
            
            // 3. UPDATE実行
            int result = mapper.update(boardId, category, title, writer, content,
                                       fileName, filePath, fileSize);
            /*
             * mapper.update()の動作:
             * 1. BoardMapper.xmlのupdateSQLを実行
             * 2. XMLの<if test="fileName != null and fileName != ''">で
             *    ファイル更新の有無を判定
             * 3. 条件に応じて動的にSQL文が生成
             */
            
            // 4. コミット
            sqlSession.commit();
            
            logger.debug("【BoardDao】update 成功: " + result + "件");
            
            // 5. 結果を返す
            return result > 0;
            
        } catch (Exception e) {
            // エラー時の処理
            if (sqlSession != null) {
                sqlSession.rollback();
                
            }
            
            logger.error("【BoardDao】update エラー", e);
            return false;
            
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }

    }
    
    /**
     * 【deleteData メソッド】
     * 投稿を削除する
     * Mapperメソッド呼び出しのみ
     * XMLでUPDATE文を記述
     * 
     * @param id 削除対象の投稿ID
     * @return 成功時true、失敗時false
     */
    public static boolean delete(long boardId) {
        
    	logger.info("【BoardDao】deleteメソッド開始 boardId: " + boardId);
        
        SqlSession sqlSession = null;
        
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
            
            // 論理削除実行
            int result = mapper.delete(boardId);
            /*
             * DELETE文の構造:
             * UPDATE board_data SET is_deleted = TRUE
             * WHERE board_id = #{boardId} AND is_deleted = FALSE
             * 
             * is_deleted = FALSE 条件により、既に削除済みのデータは対象外
             */
            
            // コミット
            sqlSession.commit();
            
            logger.debug("【BoardDao】delete 成功: " + result + "件");
            
            // 結果を返す
            return result > 0;
            
        } catch (Exception e) {
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            
            logger.error("【BoardDao】delete エラー", e);
            return false;
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }

    }
    
    /**
     * 【incrementViewCount メソッド】
     * 閲覧数を1増やす
     * 
     * XMLで view_count = view_count + 1 を記述
     * パラメータはboardIdのみ
     * 
     * 使用箇所:
     * - 詳細画面を表示した時に呼ばれる
     * - 閲覧数をカウントアップ
     * 
     * @param id 対象の投稿ID
     */
    public static void incrementViewCount(long boardId) {
    	SqlSession sqlSession = null;
        
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BoardMapper mapper = sqlSession.getMapper(BoardMapper.class);
            
            // 閲覧数増加
            mapper.incrementViewCount(boardId);

            // コミット
            sqlSession.commit();
            
            logger.debug("【BoardDao】incrementViewCount 成功");
            
        } catch (Exception e) {
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            
            logger.error("【BoardDao】incrementViewCount エラー", e);           
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }

    }
}