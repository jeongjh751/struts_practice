package dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mapper.CommentMapper;
import model.CommentData;
import util.MyBatisUtil;

/**
 * 【Commentクラス】
 * コメントデータの管理とデータベース操作を担当するクラス
 * 
 * 役割:
 * - コメントの追加（INSERT）
 * - コメントの取得（SELECT）
 * - コメントの更新（UPDATE）
 * - コメントの削除（DELETE）
 * 
 * Boardクラスと同じスタイルで実装
 */
public class CommentDao {
    
    // ========== ロガー ==========
    private static final Logger logger = LogManager.getLogger(CommentDao.class);

    /**
     * コメント追加
     * 
     * @param comment コメントデータ
     * @return 成功した場合true、失敗した場合false
     */
    public static boolean addComment(CommentData comment) {
    	
    	logger.info("【コメント追加】addComment開始");
        logger.debug("【コメント追加】board_id: " + comment.getBoardId() + 
                    ", writer: " + comment.getWriter());
        
        // 返信コメントの場合
        if (comment.getParentCommentId() != null) {
            return addReply(
                comment.getBoardId(),
                comment.getWriter(),
                comment.getContent(),
                comment.getParentCommentId(),
                comment.getIpAddress()
            );
        }
        
        // 通常コメントの場合
        return addComment(
            comment.getBoardId(),
            comment.getWriter(),
            comment.getContent(),
            comment.getIpAddress()
        );

    }
    
    /**
     * コメント追加
     * 
     * @param boardId 投稿ID
     * @param writer 作成者
     * @param content 内容
     * @param ipAddress IPアドレス
     * @return 成功した場合true
     */
    public static boolean addComment(long boardId, String writer, String content, String ipAddress) {
    	
    	logger.info("【コメント追加】addComment開始 - board_id: " + boardId);
        
        SqlSession sqlSession = null;
        
        try {
        	
            sqlSession = MyBatisUtil.getSqlSession();
            
            CommentMapper mapper = sqlSession.getMapper(CommentMapper.class);
            
            int result = mapper.addComment(boardId, writer, content, ipAddress);
            
            sqlSession.commit();
            
            logger.debug("【コメント追加】追加成功 - board_id: " + boardId);
            
            return result > 0;
            
        } catch (Exception e) {

            if (sqlSession != null) {
                sqlSession.rollback();
            }
            logger.error("【コメント追加】SQLException エラー", e);
            return false;
            
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }

    }
    
    /**
     * 返信コメント追加
     * 
     * @param boardId 投稿ID
     * @param writer 作成者
     * @param content 内容
     * @param parentCommentId 親コメントID
     * @param ipAddress IPアドレス
     * @return 成功した場合true
     */
    public static boolean addReply(long boardId, String writer, String content, 
                                   long parentCommentId, String ipAddress) {
    	
    	logger.info("【返信追加】addReply開始 - parent_comment_id: " + parentCommentId);
        
        SqlSession sqlSession = null;
        
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            CommentMapper mapper = sqlSession.getMapper(CommentMapper.class);
            
            int result = mapper.addReply(boardId, writer, content, parentCommentId, ipAddress);
            
            sqlSession.commit();
            
            logger.debug("【返信追加】追加成功 - parent_comment_id: " + parentCommentId);
            
            return result > 0;
            
        } catch (Exception e) {
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            logger.error("【返信追加】SQLException エラー", e);
            return false;
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }

    }
    
    /**
     * 特定の投稿のコメント一覧取得
     * 
     * @param boardId 投稿ID
     * @return コメントリスト
     */
    public static List<CommentData> getCommentsByBoardId(long boardId) {
    	
    	logger.info("【コメント取得】getCommentsByBoardId開始 - board_id: " + boardId);
        
        SqlSession sqlSession = null;
        
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            CommentMapper mapper = sqlSession.getMapper(CommentMapper.class);
            
            List<CommentData> comments = mapper.getCommentsByBoardId(boardId);
            if (comments == null) {
                logger.debug("【コメント取得】コメントなし - 空のリスト返却");
                return new ArrayList<>();
            }
            
            logger.debug("【コメント取得】取得成功 - 件数: " + (comments != null ? comments.size() : 0));
            
            return comments;
            
        } catch (Exception e) {
            logger.error("【コメント取得】SQLException エラー - board_id: " + boardId, e);
            return new ArrayList<>();
            
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }

    }
    
    /**
     * 特定のコメント取得
     * 
     * @param commentId コメントID
     * @return コメントデータ（見つからない場合はnull）
     */
    public static CommentData getCommentById(long commentId) {
    	
    	logger.info("【コメント取得】getCommentById開始 - comment_id: " + commentId);
        
        SqlSession sqlSession = null;
        
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            CommentMapper mapper = sqlSession.getMapper(CommentMapper.class);
            
            CommentData comment = mapper.getCommentById(commentId);
            
            if (comment != null) {
                logger.debug("【コメント取得】取得成功 - comment_id: " + commentId);
            } else {
                logger.warn("【コメント取得】コメントが見つかりません - comment_id: " + commentId);
            }
            
            return comment;
            
        } catch (Exception e) {
            logger.error("【コメント取得】SQLException エラー - comment_id: " + commentId, e);
            return null;
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }

    }
    
    /**
     * コメント更新
     * 
     * @param commentId コメントID
     * @param content 新しい内容
     * @return 成功した場合true
     */
    public static boolean updateComment(long commentId, String content) {
        
    	logger.info("【コメント更新】updateComment開始 - comment_id: " + commentId);
        
        SqlSession sqlSession = null;
        
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            CommentMapper mapper = sqlSession.getMapper(CommentMapper.class);
            
            int result = mapper.updateComment(commentId, content);
            
            sqlSession.commit();
            
            if (result > 0) {
                logger.debug("【コメント更新】更新成功 - comment_id: " + commentId);
                return true;
            } else {
                logger.warn("【コメント更新】対象データなし - comment_id: " + commentId);
                return false;
            }
            
        } catch (Exception e) {
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            logger.error("【コメント更新】SQLException エラー - comment_id: " + commentId, e);
            return false;
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }

    }
    
    /**
     * コメント削除（論理削除）
     * 
     * @param commentId コメントID
     * @return 成功した場合true
     */
    public static boolean deleteComment(long commentId) {
    	
    	logger.info("【コメント削除】deleteComment開始 - comment_id: " + commentId);
        
        SqlSession sqlSession = null;
        
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            CommentMapper mapper = sqlSession.getMapper(CommentMapper.class);
            
            int result = mapper.deleteComment(commentId);

            sqlSession.commit();
            
            if (result > 0) {
                logger.debug("【コメント削除】削除成功 - comment_id: " + commentId);
                return true;
            } else {
                logger.warn("【コメント削除】対象データなし - comment_id: " + commentId);
                return false;
            }
            
        } catch (Exception e) {
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            logger.error("【コメント削除】SQLException エラー - comment_id: " + commentId, e);
            return false;
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }

    }
    
    /**
     * 投稿のコメント数を取得
     * 
     * @param boardId 投稿ID
     * @return コメント数
     */
    public static int getCommentCount(long boardId) {
    	
    	logger.info("【コメント数取得】getCommentCount開始 - board_id: " + boardId);
        
        SqlSession sqlSession = null;
        
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            CommentMapper mapper = sqlSession.getMapper(CommentMapper.class);
            
            int count = mapper.getCommentCount(boardId);

            logger.debug("【コメント数取得】取得成功 - board_id: " + boardId + ", count: " + count);
            
            return count;
            
        } catch (Exception e) {
            logger.error("【コメント数取得】SQLException エラー - board_id: " + boardId, e);
            return 0;
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }

    }
}