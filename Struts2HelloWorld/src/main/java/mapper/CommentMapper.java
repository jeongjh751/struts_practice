package mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import model.CommentData;

/*
 * 【CommentMapper インターフェース】
 * MyBatis Mapper Interface - コメント機能
 */
public interface CommentMapper {
    
    /**
     * 特定掲示板のコメント全件取得（親→子の順）
     * 
     * @param boardId 掲示板ID
     * @return コメントリスト
     */
    List<CommentData> getCommentsByBoardId(@Param("boardId") long boardId);
    
    /**
     * コメントID検索
     * 
     * @param commentId コメントID
     * @return コメントデータ
     */
    CommentData getCommentById(@Param("commentId") long commentId);
    
    /**
     * コメント新規登録（通常コメント）
     * 
     * @param boardId 掲示板ID
     * @param writer 作成者
     * @param content コメント内容
     * @param ipAddress IPアドレス
     * @return 影響を受けた行数
     */
    int addComment(
        @Param("boardId") long boardId,
        @Param("writer") String writer,
        @Param("content") String content,
        @Param("ipAddress") String ipAddress
    );
    
    /**
     * 返信コメント追加
     * 
     * @param boardId 掲示板ID
     * @param writer 作成者
     * @param content コメント内容
     * @param parentCommentId 親コメントID
     * @param ipAddress IPアドレス
     * @return 影響を受けた行数
     */
    int addReply(
        @Param("boardId") long boardId,
        @Param("writer") String writer,
        @Param("content") String content,
        @Param("parentCommentId") long parentCommentId,
        @Param("ipAddress") String ipAddress
    );
    
    /**
     * コメント更新
     * 
     * @param commentId コメントID
     * @param content コメント内容
     * @return 影響を受けた行数
     */
    int updateComment(
        @Param("commentId") long commentId,
        @Param("content") String content
    );
    
    /**
     * コメント論理削除
     * 
     * @param commentId コメントID
     * @return 影響を受けた行数
     */
    int deleteComment(@Param("commentId") long commentId);
    
    /**
     * 特定掲示板のコメント数を取得
     * 
     * @param boardId 掲示板ID
     * @return コメント数
     */
    int getCommentCount(@Param("boardId") long boardId);
}