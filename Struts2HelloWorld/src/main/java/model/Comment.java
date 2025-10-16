package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class Comment {
    
    // ========== ロガー ==========
    private static final Logger logger = LogManager.getLogger(Comment.class);
    
    /**
     * コメント追加
     * @param comment コメントデータ
     * @return 成功した場合true、失敗した場合false
     */
    public static boolean addComment(CommentData comment) {
        logger.debug("【コメント追加】addComment開始");
        logger.debug("【コメント追加】board_id: " + comment.getBoardId() + 
                    ", writer: " + comment.getWriter());
        
        String sql = "INSERT INTO comment_data (board_id, writer, content, parent_comment_id, ip_address) " +
                     "VALUES (?, ?, ?, ?, ?::inet)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, comment.getBoardId());
            pstmt.setString(2, comment.getWriter());
            pstmt.setString(3, comment.getContent());
            
            // 親コメントID（返信の場合のみ値あり）
            if (comment.getParentCommentId() != null) {
                pstmt.setLong(4, comment.getParentCommentId());
            } else {
                pstmt.setNull(4, Types.BIGINT);
            }
            
            // IPアドレス（空文字列の場合はNULL）
            String ipAddr = comment.getIpAddress();
            if (ipAddr != null && !ipAddr.trim().isEmpty()) {
                pstmt.setString(5, ipAddr);
            } else {
                pstmt.setNull(5, Types.OTHER);  // INET型はTypes.OTHER
            }
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                logger.info("【コメント追加】追加成功 - board_id: " + comment.getBoardId());
                return true;
            } else {
                logger.warn("【コメント追加】追加失敗");
                return false;
            }
            
        } catch (SQLException e) {
            logger.error("【コメント追加】SQLException エラー", e);
            return false;
        }
    }
    
    /**
     * コメント追加（簡易版）
     * @param boardId 投稿ID
     * @param writer 作成者
     * @param content 内容
     * @param ipAddress IPアドレス
     * @return 成功した場合true
     */
    public static boolean addComment(long boardId, String writer, String content, String ipAddress) {
        CommentData comment = new CommentData();
        comment.setBoardId(boardId);
        comment.setWriter(writer);
        comment.setContent(content);
        comment.setIpAddress(ipAddress);
        
        return addComment(comment);
    }
    
    /**
     * 返信コメント追加
     * @param boardId 投稿ID
     * @param writer 作成者
     * @param content 内容
     * @param parentCommentId 親コメントID
     * @param ipAddress IPアドレス
     * @return 成功した場合true
     */
    public static boolean addReply(long boardId, String writer, String content, 
                                   long parentCommentId, String ipAddress) {
        CommentData comment = new CommentData();
        comment.setBoardId(boardId);
        comment.setWriter(writer);
        comment.setContent(content);
        comment.setParentCommentId(parentCommentId);
        comment.setIpAddress(ipAddress);
        
        return addComment(comment);
    }
    
    /**
     * 特定の投稿のコメント一覧取得
     * @param boardId 投稿ID
     * @return コメントリスト
     */
    public static List<CommentData> getCommentsByBoardId(long boardId) {
        logger.debug("【コメント取得】getCommentsByBoardId開始 - board_id: " + boardId);
        
        List<CommentData> comments = new ArrayList<>();
        
        // 親コメント → 子コメント の順に並べる
        String sql = "SELECT comment_id, board_id, writer, content, " +
                     "parent_comment_id, ip_address, is_deleted, created_at, updated_at " +
                     "FROM comment_data " +
                     "WHERE board_id = ? AND is_deleted = FALSE " +
                     "ORDER BY " +
                     "CASE WHEN parent_comment_id IS NULL THEN comment_id ELSE parent_comment_id END, " +
                     "parent_comment_id NULLS FIRST, " +
                     "created_at ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, boardId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CommentData comment = new CommentData();
                    comment.setCommentId(rs.getLong("comment_id"));
                    comment.setBoardId(rs.getLong("board_id"));
                    comment.setWriter(rs.getString("writer"));
                    comment.setContent(rs.getString("content"));
                    
                    // parent_comment_idはNULLの可能性あり
                    long parentId = rs.getLong("parent_comment_id");
                    if (!rs.wasNull()) {
                        comment.setParentCommentId(parentId);
                    }
                    
                    comment.setIpAddress(rs.getString("ip_address"));
                    comment.setDeleted(rs.getBoolean("is_deleted"));
                    comment.setCreatedAt(rs.getTimestamp("created_at"));
                    comment.setUpdatedAt(rs.getTimestamp("updated_at"));
                    
                    comments.add(comment);
                }
            }
            
            logger.info("【コメント取得】取得成功 - 件数: " + comments.size());
            
        } catch (SQLException e) {
            logger.error("【コメント取得】SQLException エラー - board_id: " + boardId, e);
        }
        
        return comments;
    }
    
    /**
     * 特定のコメント取得
     * @param commentId コメントID
     * @return コメントデータ（見つからない場合はnull）
     */
    public static CommentData getCommentById(long commentId) {
        logger.debug("【コメント取得】getCommentById開始 - comment_id: " + commentId);
        
        String sql = "SELECT comment_id, board_id, writer, content, " +
                     "parent_comment_id, ip_address, is_deleted, created_at, updated_at " +
                     "FROM comment_data " +
                     "WHERE comment_id = ? AND is_deleted = FALSE";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, commentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    CommentData comment = new CommentData();
                    comment.setCommentId(rs.getLong("comment_id"));
                    comment.setBoardId(rs.getLong("board_id"));
                    comment.setWriter(rs.getString("writer"));
                    comment.setContent(rs.getString("content"));
                    
                    long parentId = rs.getLong("parent_comment_id");
                    if (!rs.wasNull()) {
                        comment.setParentCommentId(parentId);
                    }
                    
                    comment.setIpAddress(rs.getString("ip_address"));
                    comment.setDeleted(rs.getBoolean("is_deleted"));
                    comment.setCreatedAt(rs.getTimestamp("created_at"));
                    comment.setUpdatedAt(rs.getTimestamp("updated_at"));
                    
                    logger.info("【コメント取得】取得成功 - comment_id: " + commentId);
                    return comment;
                }
            }
            
        } catch (SQLException e) {
            logger.error("【コメント取得】SQLException エラー - comment_id: " + commentId, e);
        }
        
        logger.warn("【コメント取得】コメントが見つかりません - comment_id: " + commentId);
        return null;
    }
    
    /**
     * コメント更新
     * @param commentId コメントID
     * @param content 新しい内容
     * @return 成功した場合true
     */
    public static boolean updateComment(long commentId, String content) {
        logger.debug("【コメント更新】updateComment開始 - comment_id: " + commentId);
        
        String sql = "UPDATE comment_data SET content = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE comment_id = ? AND is_deleted = FALSE";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, content);
            pstmt.setLong(2, commentId);
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                logger.info("【コメント更新】更新成功 - comment_id: " + commentId);
                return true;
            } else {
                logger.warn("【コメント更新】対象データなし - comment_id: " + commentId);
                return false;
            }
            
        } catch (SQLException e) {
            logger.error("【コメント更新】SQLException エラー - comment_id: " + commentId, e);
            return false;
        }
    }
    
    /**
     * コメント削除（論理削除）
     * @param commentId コメントID
     * @return 成功した場合true
     */
    public static boolean deleteComment(long commentId) {
        logger.debug("【コメント削除】deleteComment開始 - comment_id: " + commentId);
        
        String sql = "UPDATE comment_data SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE comment_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, commentId);
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                logger.info("【コメント削除】削除成功 - comment_id: " + commentId);
                return true;
            } else {
                logger.warn("【コメント削除】対象データなし - comment_id: " + commentId);
                return false;
            }
            
        } catch (SQLException e) {
            logger.error("【コメント削除】SQLException エラー - comment_id: " + commentId, e);
            return false;
        }
    }
    
    /**
     * 投稿のコメント数を取得
     * @param boardId 投稿ID
     * @return コメント数
     */
    public static int getCommentCount(long boardId) {
        logger.debug("【コメント数取得】getCommentCount開始 - board_id: " + boardId);
        
        String sql = "SELECT COUNT(*) FROM comment_data " +
                     "WHERE board_id = ? AND is_deleted = FALSE";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, boardId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    logger.info("【コメント数取得】取得成功 - board_id: " + boardId + ", count: " + count);
                    return count;
                }
            }
            
        } catch (SQLException e) {
            logger.error("【コメント数取得】SQLException エラー - board_id: " + boardId, e);
        }
        
        return 0;
    }
}