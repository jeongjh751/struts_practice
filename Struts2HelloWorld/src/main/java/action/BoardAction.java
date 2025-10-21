package action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import dao.CommentDao;
import dto.request.BoardCreateRequest;
import dto.request.BoardUpdateRequest;
import dto.response.BoardDetailResponse;
import dto.response.BoardListResponse;
import model.CommentData;
import model.FileInfo;
import service.BoardService;
import service.FileService;

/**
 * 【BoardActionクラス】
 * Struts2のコントローラークラス（Actionクラス）
 * 
 * 役割:
 * - ユーザーからのリクエストを受け取る
 * - ビジネスロジックを実行（Boardクラスを呼び出す）
 * - 処理結果をビュー（JSP）に渡す
 * 
 * MVCパターンにおける位置づけ:
 * - Model: Board, BoardData（データとビジネスロジック）
 * - View: board.jsp（画面表示）
 * - Controller: BoardAction（このクラス）← リクエストの制御
 * 
 * ActionSupportクラスを継承することで得られる機能:
 * - バリデーション機能（入力チェック）
 * - エラーメッセージの管理
 * - 国際化（i18n）のサポート
 * - 結果の返却（success, error, input等）
 */
public class BoardAction extends ActionSupport {
	/*
	 * 【extends ActionSupport】
	 * - ActionSupportクラスを継承
	 * - ActionSupportはStruts2の基本Actionクラス
	 * - 便利なメソッドが多数用意されている:
	 *   addActionError(): エラーメッセージを追加
	 *   hasActionErrors(): エラーがあるかチェック
	 *   getText(): プロパティファイルからメッセージ取得
	 * 
	 * 【他の選択肢】
	 * 1. Actionインターフェースを実装（最小限の機能のみ）
	 * 2. ActionSupportを継承（推奨）← 今回採用
	 * 3. 何も継承しない（POJOアクション）
	 */

	// ========== Log4j2宣言 ==========
    private static final Logger logger = LogManager.getLogger(BoardAction.class);
    
	// ========== フィールド宣言 ==========
	// これらのフィールドはStruts2によって自動的にバインドされる
    
    private BoardService boardService = new BoardService();  
    private FileService fileService = FileService.getInstance();
    
	private long boardId; // 編集/削除対象のID
    /*
     * 1. 編集・削除する投稿のIDを受け取るためのフィールド
     * 2. JSPから<input type="hidden" name="id" value="..."/>で送られてくる
     * 3. Struts2が自動的にsetId()を呼び出して値を設定する
     */
	
	private String category;
	
	private String title;
	/*
	 * 【投稿タイトルを保持するフィールド】
	 */

	private String content;
	/*
	 * 【投稿メッセージを保持するフィールド】
	 * JSPの <s:textfield name="message"/> と対応
	 */
	
	private String writer;
	/*
	 * 【投稿者名を保持するフィールド】
	 * 
	 * JSPからのデータバインディング:
	 * 1. JSPで <s:textfield name="name"/> と書く
	 * 2. ユーザーが「太郎」と入力してフォーム送信
	 * 3. Struts2が自動的にsetName("太郎")を呼び出す
	 * 4. このnameフィールドに「太郎」が設定される
	 */

	private String updater;
	/*
	 * 【更新者名を保持するフィールド】
	 */
	
	private String ipAdress;
	/*
	 * 【投稿者のIPアドレスを保持するフィールド】
	 * JSPの <input type="hidden" name="remoteAddress"/> と対応
	 */
	
	private List<BoardListResponse> data;        // 一覧用
    private BoardDetailResponse item;            // 詳細用

	private List<CommentData> comments; // コメントリスト
    
	private String searchKeyword; // 検索キーワード

    private File upload; // アップロードされたファイル
    private String uploadContentType; // ファイルのContentタイプ
    private String uploadFileName; // ファイル名
    
    private String fileName;
    private InputStream inputStream;

	private static final long serialVersionUID = 1L;
	/*
	 * 【シリアライズバージョンUID】
	 * 
	 * - Serializableインターフェースを実装する場合に必要
	 * - ActionSupportがSerializableを実装しているため、
	 *   警告を消すために定義している
	 * - オブジェクトをファイルやネットワーク経由で保存・送信する際、
	 *   クラスのバージョン管理に使用される
	 * - 通常は1Lで問題ない（変更がない場合）
	 * 
	 * 【なぜ必要か】
	 * - SessionにActionを保存する場合
	 * - クラスタリング環境でActionを転送する場合
	 * - オブジェクトをシリアライズする場合
	 */

	// ========== Getter/Setterメソッド ==========
	// Struts2のフレームワークがこれらのメソッドを使って
	// 自動的にデータのやり取りを行う
	
	public long getBoardId() {
		return boardId;
	}

	public void setBoardId(long boardId) {
		this.boardId = boardId;
	}
	
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

	/**
	 * 投稿内容を取得
	 * @return 内容本文
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 内容を設定
	 * @param content 内容本文
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 投稿者名を取得
	 * @return 投稿者名
	 */
	public String getWriter() {
		return writer;
	}

	/**
	 * 投稿者名を設定
	 * @param writer 投稿者名
	 * 
	 * 【Struts2による自動呼び出し】
	 * ユーザーがフォームを送信すると、Struts2が自動的に:
	 * 1. リクエストパラメータから"name"の値を取得
	 * 2. このsetName()メソッドを呼び出して値を設定
	 * 3. その後、execute()メソッドが実行される
	 */
	public void setWriter(String writer) {
		this.writer = writer;
	}

	/**
	 * 更新者名を取得
	 * @return 更新者名
	 */
	public String getUpdater() {
	    return updater;
	}
	
	/**
	 * 更新者名を設定
	 * @param updater 更新者名
	 */
	public void setUpdater(String updater) {
	    this.updater = updater;
	}
	
	/**
	 * IPアドレスを取得
	 * @return IPアドレス
	 */
	public String getIpAddress() {
		return ipAdress;
	}

	/**
	 * IPアドレスを設定
	 * @param ipAddress IPアドレス
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAdress = ipAddress;
	}

	public List<BoardListResponse> getData() {
        return data;
    }

    public void setData(List<BoardListResponse> data) {
        this.data = data;
    }
    
    public BoardDetailResponse getItem() {
        return item;
    }
    
    public void setItem(BoardDetailResponse item) {
        this.item = item;
    }
    
    public List<CommentData> getComments() {
        return comments;
    }
    
    public void setComments(List<CommentData> comments) {
        this.comments = comments;
    }
    
    public String getSearchKeyword() {
        return searchKeyword;
    }
    
    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
    
    public File getUpload() {
        return upload;
    }
    
    public void setUpload(File upload) {
        this.upload = upload;
    }
    
    public String getUploadContentType() {
        return uploadContentType;
    }
    
    public void setUploadContentType(String uploadContentType) {
        this.uploadContentType = uploadContentType;
    }
    
    public String getUploadFileName() {
        return uploadFileName;
    }
    
    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }
    
    public InputStream getInputStream() {
        return inputStream;
    }

    public String getFileName() {
        return fileName;
    }
    
	// ========== Actionメソッド ==========

    /**
     * 一覧表示 (リスト画面)
     * 
     * 呼び出しタイミング:
     * - boardList.action
     * 
     * 処理の流れ:
     * 1. ServiceからDTOリスト取得
     * 2. dataフィールドに設定
     * 3. JSPへ
     */
    public String list() {
        logger.info("【一覧表示】list()メソッド開始");
        
        try {
        	 // ServiceからDTOリスト取得
            data = boardService.getBoardList(category, searchKeyword);
            
            logger.debug("【一覧表示】投稿件数: " + data.size());
            
        } catch (Exception e) {
            logger.error("【一覧表示】エラー発生: " + e.getMessage(), e);
            addActionError("データの取得に失敗しました");
        }
        
        return "list";
    }

    /*
     * 掲示板詳細照会
     * 
     * 呼び出しタイミング:
     * - boardDetail.action?boardId=1
     */
    public String detail() {
        logger.info("【詳細表示】detail()メソッド開始 - boardId: " + boardId);
        
        // ServiceからDTO取得
        item = boardService.getBoardDetail(boardId);
        
        if (item != null) {
            // コメント取得
            comments = CommentDao.getCommentsByBoardId(boardId);
            logger.debug("【詳細表示】投稿表示成功 - コメント数: " + comments.size());
            return "detail";
        } else {
            addActionError("投稿が見つかりませんでした");
            return "error";
        }
    }
    
    // 新規投稿フォーム表示
    public String input() {
        return "input";
    }
    
	/*
	 * 掲示板生成（新規投稿）
     * 
     * 呼び出しタイミング:
     * - JSPで <s:submit value="投稿"/> がクリックされた時
     * 
     * 処理の流れ:
     * 1. 入力値検証
     * 2. ファイルアップロード処理
     * 3. Request DTO生成
     * 4. Service呼び出し
	 */
    public String execute() {
        logger.info("【新規投稿】execute()メソッド開始");
        logger.debug("【新規投稿】writer: " + writer + ", title: " + title);
        
        if (isValid()) {
            // 1. ファイルアップロード処理
        	FileInfo fileInfo = null;

        	if (upload != null) {
                logger.debug("【新規投稿】ファイルアップロード: " + uploadFileName);
                
                try {
                    // FileServiceでファイル処理を委任
                    fileInfo = fileService.uploadFile(upload, uploadContentType, uploadFileName);
                    logger.debug("【新規投稿】ファイル保存成功: " + fileInfo.getFileName());
                    
                } catch (Exception e) {
                    logger.error("【新規投稿】ファイル保存失敗: " + e.getMessage(), e);
                    addActionError(e.getMessage());
                    return "input";
                }
            }
        	
        	// 2. Request DTO生成
            BoardCreateRequest request;
            
            if (fileInfo != null) {
            	request = new BoardCreateRequest(
                        category, title, content, writer, ipAdress,
                        fileInfo.getFileName(), fileInfo.getFilePath(), fileInfo.getFileSize()
                );
            } else {
            	request = new BoardCreateRequest(
                        category, title, content, writer, ipAdress
                );
            }
            
            // 3. Service呼び出し
            boolean success = boardService.createBoard(request);
            
            if (success) {
                logger.debug("【新規投稿】投稿成功 - writer: " + writer);
                return "success";
            } else {
                logger.error("【新規投稿】投稿失敗 - writer: " + writer);
                addActionError("投稿に失敗しました");
                return "input";
            }
        } else {
            return "input";
        }

    }

	/*
     * 修正フォーム表示
     * 
     * 呼び出しタイミング:
     * - boardEditForm.action?boardId=1
	 */
    public String editForm() {
        logger.info("【編集フォーム】editForm()メソッド開始 - boardId: " + boardId);
        
        item = boardService.getBoardForEdit(boardId);
        
        if (item != null) {
            // DTOデータをフィールドに設定（フォーム初期値）
            this.category = item.getCategory();
            this.title = item.getTitle();
            this.content = item.getContent();
            this.writer = item.getWriter();
            
            logger.debug("【編集フォーム】編集画面表示 - boardId: " + boardId);
            return "edit";
        } else {
            logger.error("【編集フォーム】投稿が見つかりませんでした - boardId: " + boardId);
            addActionError("投稿が見つかりませんでした");
            data = boardService.getBoardList(null, null);
            return "list";
        }
    }

    
    /*
     * 掲示板修正
     * 
     * 呼び出しタイミング:
     * - boardEdit.action
     */
    public String edit() {
        logger.info("【編集】edit()メソッド開始 - boardId: " + boardId);
        
        if (title != null && content != null && writer != null &&
            !title.equals("") && !content.equals("") && !writer.equals("")) {
            
        	// 1. ファイルアップロード処理
        	FileInfo fileInfo = null;

        	if (upload != null) {
                logger.debug("【編集】ファイルアップロード: " + uploadFileName);
                
                try {
                    fileInfo = fileService.uploadFile(upload, uploadContentType, uploadFileName);
                    logger.debug("【編集】ファイル保存成功: " + fileInfo.getFileName());
                    
                } catch (Exception e) {
                    logger.error("【編集】ファイル保存失敗: " + e.getMessage(), e);
                    addActionError(e.getMessage());
                    data = boardService.getBoardList(null, null);
                    return "list";
                }
            }
            
        	// 2. Request DTO生成
            BoardUpdateRequest request;
            if (fileInfo != null) {
                request = new BoardUpdateRequest(
                    boardId, category, title, content, writer,
                    fileInfo.getFileName(), fileInfo.getFilePath(), fileInfo.getFileSize()
                );
            } else {
                request = new BoardUpdateRequest(
                    boardId, category, title, content, writer
                );
            }
            
            // 3. Service呼び出し
            boolean success = boardService.updateBoard(request);
            
            if (success) {
                logger.debug("【編集】更新成功 - boardId: " + boardId);
            } else {
                logger.error("【編集】更新失敗 - boardId: " + boardId);
                addActionError("投稿が見つかりませんでした");
            }
        } else {
            logger.error("【編集】入力値エラー");
            addActionError("すべて入力してください");
        }
        data = boardService.getBoardList(null, null);
        return "list";

    }


    /*
     * 掲示板削除
     * 
     * 呼び出しタイミング:
     * - boardDelete.action?boardId=1
     */
    public String delete() {
        logger.info("【削除】delete()メソッド開始 - boardId: " + boardId);
        
        // Service呼び出し
        boolean success = boardService.deleteBoard(boardId);
        
        if (!success) {
            logger.error("【削除】削除失敗 - boardId: " + boardId);
            addActionError("投稿が見つかりませんでした");
        } else {
            logger.info("【削除】削除成功 - boardId: " + boardId);
        }
        
        return "list";
    }

    /*
	 * 入力値検証
	 * 
	 * 検証項目:
	 * - タイトル必須
	 * - 名前必須
	 * - 内容必須
	 */
	public boolean isValid() {
		
		if (title == null || title.equals("")) {
			logger.error("【バリデーション】タイトル未入力");
            addActionError("タイトルを入力してください");
        }
		
		if (writer == null || writer.equals("")) {
			/*
			 * 【名前の必須チェック】
			 * - name == null: フォームに名前フィールドが存在しない場合
			 * - name.equals(""): 名前が空文字列（入力されていない）場合
			 * 
			 * 【より良い書き方】
			 * if (name == null || name.trim().isEmpty()) {
			 *     // trim()で前後の空白を削除
			 *     // isEmpty()はJava 6以降で使用可能
			 * }
			 */
			logger.error("【バリデーション】名前未入力");
			addActionError("名前を入力してください");
			/*
			 * 【addActionError()メソッド】
			 * - ActionSupportクラスから継承したメソッド
			 * - エラーメッセージをリストに追加
			 * - 複数回呼び出すと、複数のエラーメッセージが蓄積される
			 * - JSPの<s:actionerror/>タグで全てのエラーが表示される
			 * 
			 * 表示例:
			 * ・名前を入力してください
			 * ・メッセージを入力してください
			 */
		}

		if (content == null || content.equals("")) {
			/*
			 * 【内容の必須チェック】
			 * 名前と同じロジック
			 */
			logger.error("【バリデーション】内容未入力");
			addActionError("内容を入力してください");
		}

		return !hasActionErrors();
		/*
		 * 【hasActionErrors()メソッド】
		 * - ActionSupportクラスから継承したメソッド
		 * - エラーが1つでもあればtrue、なければfalseを返す
		 * 
		 * 【!hasActionErrors()の意味】
		 * - エラーがない場合: hasActionErrors() = false
		 *   → !false = true を返す（バリデーションOK）
		 * - エラーがある場合: hasActionErrors() = true
		 *   → !true = false を返す（バリデーションNG）
		 * 
		 * 【動作の流れ】
		 * 1. addActionError()でエラーを追加
		 * 2. hasActionErrors()でエラーの有無をチェック
		 * 3. !hasActionErrors()で反転させて戻り値にする
		 * 4. execute()メソッドでif(isValid())として判定
		 */
	}
	
	/*
     * ファイルダウンロード
     * 
     * 呼び出しタイミング:
     * - boardDownload.action?boardId=1
     */
    public String download() {
        logger.info("【ファイルダウンロード】boardId: " + boardId);
        
        try {
        	// ServiceからDTO照会
        	item = boardService.getBoardForEdit(boardId);
            
            if (item == null || !item.hasFile()) {
            	logger.error("【ファイルダウンロード】ファイルが見つかりません");
                addActionError("ファイルが見つかりません");
                return ERROR;
            }
            
            // ファイルパス
            String realPath = ServletActionContext.getServletContext()
                    .getRealPath(item.getFilePath());
                
                File file = new File(realPath);
                
                if (!file.exists()) {
                    logger.error("【ファイルダウンロード】ファイルが存在しません: " + realPath);
                    addActionError("ファイルが存在しません");
                    return ERROR;
                }
                
                // InputStreamとファイル名を設定
                this.inputStream = new FileInputStream(file);
                this.fileName = item.getFileName();
                
                logger.debug("【ファイルダウンロード】成功: " + fileName);
                
                return SUCCESS;
                
            } catch (Exception e) {
                logger.error("【ファイルダウンロード】エラー", e);
                addActionError("ダウンロードに失敗しました: " + e.getMessage());
                return ERROR;
            }
    }
}