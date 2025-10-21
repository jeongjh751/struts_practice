package service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dao.BoardDao;
import dto.request.BoardCreateRequest;
import dto.request.BoardUpdateRequest;
import dto.response.BoardDetailResponse;
import dto.response.BoardListResponse;
import entity.BoardEntity;

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
    public List<BoardListResponse> getBoardList(String category, String searchKeyword) {
        logger.info("【Service】掲示板一覧取得開始");
        
        try {
            // 1. 全データ取得
            List<BoardEntity> entities = BoardDao.findAll();
            
            if (entities == null || entities.isEmpty()) {
                logger.warn("【Service】照会された掲示板がありません");
                return new ArrayList<>();
            }
            
            // 2. カテゴリフィルタリング（将来の機能用 - 現在はコメントアウト）
            /*
            if (category != null && !category.trim().isEmpty()) {
                logger.debug("【Service】カテゴリフィルタ適用: " + category);
                entities = entities.stream()
                    .filter(e -> category.equals(e.getCategory()))
                    .collect(Collectors.toList());
            }
            */
            
            // 3. タイトル検索フィルタリング
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                logger.debug("【Service】検索キーワード適用: " + searchKeyword);
                String keyword = searchKeyword.toLowerCase();
                entities = entities.stream()
                    .filter(e -> e.getTitle() != null && 
                                e.getTitle().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
                logger.debug("【Service】検索結果: " + entities.size() + "件");
            }
            
            // 4. Entity → ListResponse DTO変換
            List<BoardListResponse> responses = entities.stream() // Listをstreamに変換
                    .map(BoardListResponse::from) 
                    // 各EntityをResponseに変換
                    // BoardListResponse.from(entity)メソッド呼び出し
                    .collect(Collectors.toList()); // Streamを再びListに変換
                // Stream APIとは？コレクション(List、Setなど)を関数型で処理する方法
                logger.debug("【Service】掲示板一覧取得完了: " + responses.size() + "件");
                return responses;
            
        } catch (Exception e) {
            logger.error("【Service】一覧取得エラー: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 掲示板詳細を取得
     * 
     * @param boardId 掲示板ID
     * @return 掲示板詳細Response DTO（見つからない場合null）
     * 
     * 処理フロー:
     * 1. DAOからEntity照会
     * 2. 閲覧数増加
     * 3. Entity → DetailResponse DTOへ変換
     * 4. DTOを返却
     */
    public BoardDetailResponse getBoardDetail(long boardId) {
        logger.info("【Service】詳細取得開始 - boardId: " + boardId);
        
        try {
            // 1. DAOからEntity照会
            BoardEntity entity = BoardDao.findById(boardId);
            
            if (entity == null) {
                logger.error("【Service】掲示板が見つかりません - boardId: " + boardId);
                return null;
            }
            
            // 2. 閲覧数増加
            BoardDao.incrementViewCount(boardId);
            
            // 3. Entity -> DetailResponse DTO変換
            BoardDetailResponse response = BoardDetailResponse.from(entity);
            
            logger.debug("【Service】掲示板詳細取得完了 - boardId: " + boardId);
            return response;
            
        } catch (Exception e) {
            logger.error("【Service】詳細取得エラー: " + e.getMessage(), e);
            return null;
        }

    }
    
    /**
     * 新規掲示板を作成
     * 
     * @param request 掲示板生成Request DTO
     * @return 成功時true、失敗時false
     * 
     * 処理フロー:
     * 1. 入力値検証
     * 2. Request DTO -> DAOメソッドパラメータへ変換
     * 3. DAOを通じてDB保存
     */
    public boolean createBoard(BoardCreateRequest request) {
        
        logger.info("【Service】新規投稿開始");
        logger.debug("【Service】writer: " + request.getWriter() + 
                ", title: " + request.getTitle());

        try {
            // 1. 入力値検証
            if (!request.isValid()) {
                logger.error("【Service】入力値検証失敗");
                return false;
            }
            
            // 2. DAOを通じてDB保存
            boolean success = BoardDao.insert(
                request.getCategory(),
                request.getTitle(),
                request.getContent(),
                request.getWriter(),
                request.getIpAddress(),
                request.getFileName(),
                request.getFilePath(),
                request.getFileSize()
            );
            
            if (success) {
                logger.debug("【Service】掲示板生成成功 - writer: " + request.getWriter());
            } else {
                logger.error("【Service】掲示板生成失敗 - writer: " + request.getWriter());
            }
            
            return success;
            
        } catch (Exception e) {
            logger.error("【Service】掲示板生成エラー: " + e.getMessage(), e);
            return false;
        }

    }
    
    /**
     * 掲示板を更新
     * 
     * @param request 掲示板修正Request DTO
     * @return 成功時true、失敗時false
     * 
     * 処理フロー:
     * 1. 入力値検証
     * 2. 既存掲示板存在確認
     * 3. Request DTO → DAOメソッドパラメータへ変換
     * 4. DAOを通じてDB更新
     */
    public boolean updateBoard(BoardUpdateRequest request) {
    	
    	logger.info("【Service】掲示板修正開始 - boardId: " + request.getBoardId());
        
    	try {
            // 1. 入力値検証
            if (!request.isValid()) {
                logger.error("【Service】入力値検証失敗");
                return false;
            }
            
            // 2. 既存掲示板存在確認
            BoardEntity existingEntity = BoardDao.findById(request.getBoardId());
            if (existingEntity == null) {
                logger.error("【Service】修正する掲示板が見つかりません - boardId: " 
                           + request.getBoardId());
                return false;
            }
            
            // 3. DAOを通じてDB更新
            boolean success = BoardDao.update(
                request.getBoardId(),
                request.getCategory(),
                request.getTitle(),
                request.getContent(),
                request.getWriter(),
                request.getFileName(),
                request.getFilePath(),
                request.getFileSize()
            );
            
            if (success) {
                logger.debug("【Service】掲示板修正成功 - boardId: " + request.getBoardId());
            } else {
                logger.error("【Service】掲示板修正失敗 - boardId: " + request.getBoardId());
            }
            
            return success;
            
        } catch (Exception e) {
            logger.error("【Service】掲示板修正エラー: " + e.getMessage(), e);
            return false;
        }

    }
    
    /**
     * 掲示板を削除（論理削除）
     * 
     * @param boardId 掲示板ID
     * @return 成功時true、失敗時false
     */
    public boolean deleteBoard(long boardId) {
        logger.info("【Service】掲示板削除開始 - boardId: " + boardId);
        
        try {
            // 1. 掲示板存在確認
            BoardEntity entity = BoardDao.findById(boardId);
            if (entity == null) {
                logger.error("【Service】削除する掲示板が見つかりません - boardId: " + boardId);
                return false;
            }
            
            // 2. DAOを通じて論理削除
            boolean success = BoardDao.delete(boardId);
            
            if (success) {
                logger.info("【Service】掲示板削除成功 - boardId: " + boardId);
            } else {
                logger.error("【Service】掲示板削除失敗 - boardId: " + boardId);
            }
            
            return success;
            
        } catch (Exception e) {
            logger.error("【Service】掲示板削除エラー: " + e.getMessage(), e);
            return false;
        }

    }
    
    /**
     * 編集フォーム用のデータ取得
     * 
     * @param boardId 掲示板ID
     * return 掲示板詳細Response DTO（なければnull）
     * 
     * 【機能】
     * - 編集画面で既存データを表示するために使用
     * - 閲覧数は増やさない（getBoardDetailとの違い）
     */
    public BoardDetailResponse getBoardForEdit(long boardId) {
    	logger.info("【Service】修正フォーム用照会 - boardId: " + boardId);
        
        try {
            // 閲覧数増加なしで単純照会のみ
            BoardEntity entity = BoardDao.findById(boardId);
            
            if (entity == null) {
                logger.error("【Service】掲示板が見つかりません - boardId: " + boardId);
                return null;
            }
            
            BoardDetailResponse response = BoardDetailResponse.from(entity);
            logger.debug("【Service】修正フォーム用照会完了 - boardId: " + boardId);
            
            return response;
            
        } catch (Exception e) {
            logger.error("【Service】修正フォーム用照会エラー: " + e.getMessage(), e);
            return null;
        }
    }
}