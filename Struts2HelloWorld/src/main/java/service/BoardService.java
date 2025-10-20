package service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dao.BoardDao;
import model.BoardData;

/**
 * 【BoardServiceクラス】
 * 掲示板のビジネスロジックを担当するサービス層
 * 
 * 役割:
 * - 掲示板に関する業務ロジックの実行
 * - データの検証と加工
  */
public class BoardService {
    
    private static final Logger logger = LogManager.getLogger(BoardService.class);
    
    /**
     * 掲示板一覧を取得（検索・フィルタリング付き）
     * 
     * @param category カテゴリフィルタ（nullの場合は全件取得）
     * @param searchKeyword 検索キーワード（nullの場合は検索なし）
     * @return 掲示板データリスト
     * 
     * 【Actionから移行したロジック】
     * - データ取得
     * - カテゴリフィルタリング
     * - タイトル検索
     */
    public List<BoardData> getBoardList(String category, String searchKeyword) {
        logger.info("【Service】掲示板一覧取得開始");
        
        try {
            // 1. 全データ取得
            List<BoardData> data = BoardDao.getChatData();
            
            if (data == null) {
                logger.warn("【Service】データが取得できませんでした");
                return new ArrayList<>();
            }
            
            // 2. カテゴリフィルタリング（将来の機能用 - 現在はコメントアウト）
            /*
            if (category != null && !category.trim().isEmpty()) {
                logger.debug("【Service】カテゴリフィルタ適用: " + category);
                data = filterByCategory(data, category);
            }
            */
            
            // 3. タイトル検索フィルタリング
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                logger.debug("【Service】検索キーワード適用: " + searchKeyword);
                data = filterByKeyword(data, searchKeyword);
                logger.debug("【Service】検索結果: " + data.size() + "件");
            }
            
            logger.debug("【Service】投稿件数: " + data.size());
            return data;
            
        } catch (Exception e) {
            logger.error("【Service】一覧取得エラー: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 掲示板詳細を取得
     * 
     * @param boardId 掲示板ID
     * @return 掲示板データ（見つからない場合null）
     * 
     * 【機能】
     * - ID指定でデータ取得
     * - 閲覧数の自動カウントアップ
     */
    public BoardData getBoardDetail(long boardId) {
        logger.info("【Service】詳細取得開始 - boardId: " + boardId);
        
        BoardData item = BoardDao.getDataById(boardId);
        
        if (item != null) {
            // 閲覧数+1
            BoardDao.incrementViewCount(boardId);
            logger.debug("【Service】詳細取得成功 - boardId: " + boardId);
        } else {
            logger.error("【Service】投稿が見つかりませんでした - boardId: " + boardId);
        }
        
        return item;
    }
    
    /**
     * 新規掲示板を作成
     * 
     * @param category カテゴリ
     * @param title タイトル
     * @param content 内容
     * @param writer 作成者
     * @param ipAddress IPアドレス
     * @param fileName ファイル名（nullの場合はファイルなし）
     * @param filePath ファイルパス
     * @param fileSize ファイルサイズ
     * @return 成功時true、失敗時false
     */
    public boolean createBoard(String category, String title, String content,
                               String writer, String ipAddress,
                               String fileName, String filePath, long fileSize) {
        
        logger.info("【Service】新規投稿開始");
        logger.debug("【Service】writer: " + writer + ", title: " + title);
        
        boolean success = BoardDao.addChatData(
            category, title, content, writer, ipAddress,
            fileName, filePath, fileSize
        );
        
        if (success) {
            logger.debug("【Service】投稿成功 - writer: " + writer);
        } else {
            logger.error("【Service】投稿失敗 - writer: " + writer);
        }
        
        return success;
    }
    
    /**
     * ファイルなしで掲示板を作成（下位互換性維持）
     * 
     * @param category カテゴリ
     * @param title タイトル
     * @param content 内容
     * @param writer 作成者
     * @param ipAddress IPアドレス
     * @return 成功時true、失敗時false
     */
    public boolean createBoard(String category, String title, String content,
                               String writer, String ipAddress) {
        return createBoard(category, title, content, writer, ipAddress, 
                          null, null, 0);
    }
    
    /**
     * 掲示板を更新
     * 
     * @param boardId 掲示板ID
     * @param category カテゴリ
     * @param title タイトル
     * @param content 内容
     * @param writer 作成者
     * @param fileName ファイル名
     * @param filePath ファイルパス
     * @param fileSize ファイルサイズ
     * @return 成功時true、失敗時false
     */
    public boolean updateBoard(long boardId, String category, String title,
                               String content, String writer,
                               String fileName, String filePath, long fileSize) {
        
        logger.info("【Service】更新開始 - boardId: " + boardId);
        
        boolean success = BoardDao.updateData(
            boardId, category, title, content, writer,
            fileName, filePath, fileSize
        );
        
        if (success) {
            logger.debug("【Service】更新成功 - boardId: " + boardId + 
                       ", 作成者: " + writer);
        } else {
            logger.error("【Service】更新失敗 - boardId: " + boardId);
        }
        
        return success;
    }
    
    /**
     * ファイルなしで掲示板を更新（下位互換性維持）
     * 
     * @param boardId 掲示板ID
     * @param category カテゴリ
     * @param title タイトル
     * @param content 内容
     * @param writer 作成者
     * @return 成功時true、失敗時false
     */
    public boolean updateBoard(long boardId, String category, String title,
                               String content, String writer) {
        return updateBoard(boardId, category, title, content, writer,
                          null, null, 0);
    }
    
    /**
     * 掲示板を削除（論理削除）
     * 
     * @param boardId 掲示板ID
     * @return 成功時true、失敗時false
     */
    public boolean deleteBoard(long boardId) {
        logger.info("【Service】削除開始 - boardId: " + boardId);
        
        boolean success = BoardDao.deleteData(boardId);
        
        if (!success) {
            logger.error("【Service】削除失敗 - boardId: " + boardId);
        } else {
            logger.info("【Service】削除成功 - boardId: " + boardId);
        }
        
        return success;
    }
    
    /**
     * 編集フォーム用のデータ取得
     * 
     * @param boardId 掲示板ID
     * @return 掲示板データ（見つからない場合はnull）
     * 
     * 【機能】
     * - 編集画面で既存データを表示するために使用
     * - 閲覧数は増やさない（getBoardDetailとの違い）
     */
    public BoardData getBoardForEdit(long boardId) {
        logger.info("【Service】編集データ取得 - boardId: " + boardId);
        
        BoardData item = BoardDao.getDataById(boardId);
        
        if (item != null) {
            logger.debug("【Service】編集データ取得成功 - boardId: " + boardId);
        } else {
            logger.error("【Service】投稿が見つかりませんでした - boardId: " + boardId);
        }
        
        return item;
    }
    
    // ========== プライベートヘルパーメソッド ==========
    
    /**
     * カテゴリでフィルタリング（将来の機能用）
     * 
     * @param boards 掲示板リスト
     * @param category カテゴリ
     * @return フィルタリング後のリスト
     * 
     * 【使用例】
     * List<BoardData> noticeBoards = filterByCategory(allBoards, "お知らせ");
     */
    private List<BoardData> filterByCategory(List<BoardData> boards, String category) {
        List<BoardData> filtered = new ArrayList<>();
        
        for (BoardData board : boards) {
            if (board != null && category.equals(board.getCategory())) {
                filtered.add(board);
            }
        }
        
        logger.debug("【Service】カテゴリフィルタ結果: " + filtered.size() + "件");
        return filtered;
    }
    
    /**
     * キーワードでフィルタリング
     * 
     * @param boards 掲示板リスト
     * @param keyword 検索キーワード
     * @return フィルタリング後のリスト
     * 
     * 【検索対象】
     * - タイトル（大文字小文字を区別しない）
     * 
     * 【検索ロジック】
     * 1. キーワードを小文字に変換
     * 2. タイトルも小文字に変換
     * 3. 部分一致で検索
     */
    private List<BoardData> filterByKeyword(List<BoardData> boards, String keyword) {
        List<BoardData> filtered = new ArrayList<>();
        String lowerKeyword = keyword.trim().toLowerCase();
        
        for (BoardData board : boards) {
            if (board != null && board.getTitle() != null) {
                String lowerTitle = board.getTitle().toLowerCase();
                if (lowerTitle.contains(lowerKeyword)) {
                    filtered.add(board);
                }
            }
        }
        
        return filtered;
    }
    
    /**
     * 掲示板データの基本検証
     * 
     * @param title タイトル
     * @param content 内容
     * @param writer 作成者
     * @return 有効な場合true
     * 
     * 【検証項目】
     * - タイトル: null、空文字チェック
     * - 内容: null、空文字チェック
     * - 作成者: null、空文字チェック
     */
    public boolean validateBoardData(String title, String content, String writer) {
        if (title == null || title.trim().isEmpty()) {
            logger.warn("【Service】バリデーションエラー: タイトルが空です");
            return false;
        }
        if (content == null || content.trim().isEmpty()) {
            logger.warn("【Service】バリデーションエラー: 内容が空です");
            return false;
        }
        if (writer == null || writer.trim().isEmpty()) {
            logger.warn("【Service】バリデーションエラー: 作成者が空です");
            return false;
        }
        return true;
    }
}