package action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import model.Comment;
import model.CommentData;

/**
 * 【CommentActionクラス】
 * コメント機能のコントローラー
 * 
 * 役割:
 * - ユーザーからのコメント関連リクエストを受け取る
 * - CommentDAOを呼び出してデータベース操作を実行
 * - 処理結果をビュー（JSP）に渡す
 */
public class CommentAction extends ActionSupport {
    
    // ========== ロガー ==========
    private static final Logger logger = LogManager.getLogger(CommentAction.class);
    
    // ========== フィールド ==========
    private long commentId;      // コメントID（編集・削除時に使用）
    private long boardId;        // 投稿ID（必須）
    private String writer;       // コメント作成者
    private String content;      // コメント内容
    private String ipAddress;    // IPアドレス
    private Long parentCommentId; // 親コメントID（返信の場合のみ）
    
    private List<CommentData> comments; // コメント一覧
    private CommentData comment;        // 単一コメント
    
    private static final long serialVersionUID = 1L;
    
    // ========== Getter/Setter ==========
    
    public long getCommentId() {
        return commentId;
    }
    
    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }
    
    public long getBoardId() {
        return boardId;
    }
    
    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }
    
    public String getWriter() {
        return writer;
    }
    
    public void setWriter(String writer) {
        this.writer = writer;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public Long getParentCommentId() {
        return parentCommentId;
    }
    
    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
    
    public List<CommentData> getComments() {
        return comments;
    }
    
    public void setComments(List<CommentData> comments) {
        this.comments = comments;
    }
    
    public CommentData getComment() {
        return comment;
    }
    
    public void setComment(CommentData comment) {
        this.comment = comment;
    }
    
    // ========== Actionメソッド ==========
    
    /**
     * コメント追加
     * 呼び出し元: コメント投稿フォーム
     * @return 成功時は投稿詳細ページへリダイレクト
     */
    public String add() {
        logger.debug("【コメント追加】add()メソッド開始");
        logger.debug("【コメント追加】board_id: " + boardId + ", writer: " + writer);
        
        // IPアドレスをActionで直接取得
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            HttpServletRequest request = ServletActionContext.getRequest();
            ipAddress = request.getRemoteAddr();
            logger.debug("【コメント追加】IPアドレス取得: " + ipAddress);
        }
        
        // バリデーション
        if (!isValidForAdd()) {
            logger.warn("【コメント追加】バリデーションエラー");
            return "error";
        }
        
        // CommentDataオブジェクト作成
        CommentData newComment = new CommentData();
        newComment.setBoardId(boardId);
        newComment.setWriter(writer);
        newComment.setContent(content);
        newComment.setParentCommentId(parentCommentId);
        newComment.setIpAddress(ipAddress);
        
        // データベースに追加
        boolean success = Comment.addComment(newComment);
        
        if (success) {
            logger.info("【コメント追加】追加成功 - board_id: " + boardId);
            return "success";
        } else {
            logger.error("【コメント追加】追加失敗 - board_id: " + boardId);
            addActionError("コメントの追加に失敗しました");
            return "error";
        }
    }
    
    /**
     * コメント一覧取得
     * 呼び出し元: 投稿詳細ページ
     * @return コメント一覧を含むページ
     */
    public String list() {
        logger.debug("【コメント一覧】list()メソッド開始 - board_id: " + boardId);
        
        comments = Comment.getCommentsByBoardId(boardId);
        
        logger.info("【コメント一覧】取得完了 - 件数: " + comments.size());
        
        return "success";
    }
    
    /**
     * コメント編集フォーム表示
     * @return 編集フォームページ
     */
    public String editForm() {
        logger.debug("【コメント編集フォーム】editForm()メソッド開始 - comment_id: " + commentId);
        
        comment = Comment.getCommentById(commentId);
        
        if (comment != null) {
            this.content = comment.getContent();
            logger.info("【コメント編集フォーム】表示成功 - comment_id: " + commentId);
            return "edit";
        } else {
            logger.warn("【コメント編集フォーム】コメントが見つかりません - comment_id: " + commentId);
            addActionError("コメントが見つかりませんでした");
            return "error";
        }
    }
    
    /**
     * コメント更新
     * @return 成功時は投稿詳細ページへリダイレクト
     */
    public String edit() {
        logger.debug("【コメント更新】edit()メソッド開始 - comment_id: " + commentId);
        
        if (content == null || content.trim().isEmpty()) {
            logger.warn("【コメント更新】内容が空です");
            addActionError("コメント内容を入力してください");
            return "error";
        }
        
        // 更新前にboardIdを取得（Redirect用）
        if (boardId == 0) {
            CommentData comment = Comment.getCommentById(commentId);
            if (comment != null) {
                boardId = comment.getBoardId();
            }
        }
        
        boolean success = Comment.updateComment(commentId, content);
        
        if (success) {
            logger.info("【コメント更新】更新成功 - comment_id: " + commentId);
            return "success";
        } else {
            logger.error("【コメント更新】更新失敗 - comment_id: " + commentId);
            addActionError("コメントの更新に失敗しました");
            return "error";
        }
    }
    
    /**
     * コメント削除
     * @return 成功時は投稿詳細ページへリダイレクト
     */
    public String delete() {
        logger.debug("【コメント削除】delete()メソッド開始 - comment_id: " + commentId);
        
        boolean success = Comment.deleteComment(commentId);
        
        if (success) {
            logger.info("【コメント削除】削除成功 - comment_id: " + commentId);
            return "success";
        } else {
            logger.error("【コメント削除】削除失敗 - comment_id: " + commentId);
            addActionError("コメントの削除に失敗しました");
            return "error";
        }
    }
    
    // ========== バリデーション ==========
    
    /**
     * コメント追加時のバリデーション
     * @return バリデーション成功時true
     */
    private boolean isValidForAdd() {
        boolean valid = true;
        
        if (boardId <= 0) {
            addActionError("投稿IDが不正です");
            valid = false;
        }
        
        if (writer == null || writer.trim().isEmpty()) {
            addActionError("名前を入力してください");
            valid = false;
        }
        
        if (content == null || content.trim().isEmpty()) {
            addActionError("コメント内容を入力してください");
            valid = false;
        }
        
        return valid;
    }
}