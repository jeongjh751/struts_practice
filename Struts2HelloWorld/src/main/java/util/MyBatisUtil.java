package util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 【MyBatisUtilクラス】
 * MyBatis SqlSessionFactory管理
 * 
 * 役割:
 * - HikariCPとMyBatisを統合
 * - SqlSessionの生成
 */
public class MyBatisUtil {
    
    private static final Logger logger = LogManager.getLogger(MyBatisUtil.class);
    private static SqlSessionFactory sqlSessionFactory;
    
    /*
     * static初期化ブロック
     * - クラスがロードされた時に1回だけ実行
     * - SqlSessionFactoryを生成
     * - アプリケーション起動時に実行される
     */
    static {
        try {
            // 1. MyBatis設定ファイルのパス
            String resource = "mybatis-config.xml";
            
            // 2. 設定ファイルを読み込み
            InputStream inputStream = Resources.getResourceAsStream(resource);
            
            // 3. SqlSessionFactoryを生成
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            
            logger.info("【MyBatis】SqlSessionFactory 初期化成功");
            
        } catch (IOException e) {
            logger.error("【MyBatis】SqlSessionFactory 初期化失敗", e);
            throw new RuntimeException("MyBatis初期化エラー", e);
        }
        
    }
    
    /**
     * SqlSession取得
     * true: SQL実行後、自動的にcommit
     * false: 明示的にcommit()を呼ぶ必要あり
     * 
     * @param autoCommit 自動コミット設定
     * @return SqlSession
     */
    public static SqlSession getSqlSession(boolean autoCommit) {
        /*
         * openSession(autoCommit):
         * - SqlSessionを生成
         * - 内部的にConnectionを取得
         * - mybatis-config.xmlのDataSource設定を使用
         */
        return sqlSessionFactory.openSession(autoCommit);
    }
    
    /**
     * SqlSession取得 (autoCommit = false)
     * autoCommit = falseにする理由
     * エラー時のrollbackが可能
     * データ整合性を保証
     */
    public static SqlSession getSqlSession() {
        return getSqlSession(false);
    }
    
    /**
     * SqlSessionクローズ
     */
    public static void closeSqlSession(SqlSession sqlSession) {
        if (sqlSession != null) {
            /*
             * close()の動作:
             * 1. 内部的に保持しているConnectionをクローズ
             * 2. DataSourceにConnectionを返却
             * 3. リソースを解放
             */
            sqlSession.close();
            logger.debug("【MyBatis】SqlSession クローズ");
        }
    }
}