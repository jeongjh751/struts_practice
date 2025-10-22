package mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.BoardEntity;

/*
 * 【BoardMapper インターフェース】
 * MyBatis Mapper Interface - 掲示板機能
 */
public interface BoardMapper {
    
    /**
     * 全件検索
     * 
     * @return List<BoardEntity> 掲示板データのリスト
     */
    List<BoardEntity> findAll();
    
    /**
     * ID検索
     * 
     * @param boardId 掲示板ID
     * @return BoardEntity 掲示板データ（見つからない場合null）
     */
    BoardEntity findById(@Param("boardId") long boardId);
    
    /**
     * カテゴリ検索
     * 
     * @param category カテゴリ名（お知らせ/自由/質問/設問）
     * @return List<BoardEntity> 該当カテゴリのデータリスト
     */
    List<BoardEntity> findByCategory(@Param("category") String category);
    
    /**
     * 新規登録
     * 
     * @param category カテゴリ（お知らせ/自由/質問/設問）
     * @param title タイトル
     * @param content 本文内容
     * @param writer 作成者名
     * @param ipAddress IPアドレス（投稿者のIP）
     * @param fileName アップロードファイル名
     * @param filePath サーバー保存パス
     * @param fileSize ファイルサイズ（bytes）
     * @return int 影響を受けた行数（通常は1）
     */
    int insert(
            @Param("category") String category,
            @Param("title") String title,
            @Param("content") String content,
            @Param("writer") String writer,
            @Param("ipAddress") String ipAddress,
            @Param("fileName") String fileName,
            @Param("filePath") String filePath,
            @Param("fileSize") Long fileSize
    );
    
    /**
     * 更新
     * 
     * @param boardId 更新対象の掲示板ID
     * @param category カテゴリ
     * @param title タイトル
     * @param writer 作成者名
     * @param content 本文内容
     * @param fileName ファイル名（新しいファイルがある場合のみ）
     * @param filePath ファイルパス（新しいファイルがある場合のみ）
     * @param fileSize ファイルサイズ（新しいファイルがある場合のみ）
     * @return int 影響を受けた行数（通常は1）
     */
    int update(
            @Param("boardId") long boardId,
            @Param("category") String category,
            @Param("title") String title,
            @Param("writer") String writer,
            @Param("content") String content,
            @Param("fileName") String fileName,
            @Param("filePath") String filePath,
            @Param("fileSize") Long fileSize
    );
    
    
    /**
     * 論理削除
     * 
     * @param boardId 削除対象の掲示板ID
     * @return int 影響を受けた行数（通常は1）
     */
    int delete(@Param("boardId") long boardId);
    
    /**
     * 閲覧数増加
     * 
     * @param boardId 対象の掲示板ID
     */
    void incrementViewCount(@Param("boardId") long boardId);
}