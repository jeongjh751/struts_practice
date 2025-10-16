package action;

import java.util.List;
// リスト型を使用するためのインポート

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opensymphony.xwork2.ActionSupport;
// Struts2のActionSupportクラスをインポート
// このクラスを継承することで、バリデーション、エラーメッセージ、
// 国際化などの機能が簡単に使える

import model.Board;
// 掲示板データを管理するBoardクラスをインポート
import model.BoardData;
import model.Comment;
import model.CommentData;

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

	private List<BoardData> data;
	/*
	 * 【掲示板データ全体を保持するフィールド】
	 * 
	 * 使用目的:
	 * - Boardクラスから取得したデータをJSPに渡すための橋渡し役
	 * - execute()やupdate()メソッドでBoardから取得したデータを格納
	 * - JSPで <s:iterator value="data"> として参照される
	 * 
	 * データの流れ:
	 * Board.getChatData() → このdataフィールド → JSPのValueStack → 画面表示
	 */

	private BoardData item; // 詳細表示用

	private List<CommentData> comments;  // 댓글 목록 추가
    
    // getter/setter 추가
    public List<CommentData> getComments() {
        return comments;
    }
    
    public void setComments(List<CommentData> comments) {
        this.comments = comments;
    }
    
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

	/**
	 * 掲示板データ全体を取得
	 * @return 掲示板データのリスト
	 * 
	 * 【JSPでの使用】
	 * <s:iterator value="data"> でこのgetData()が呼ばれる
	 */
	public List<BoardData> getData() {
		return data;
	}

	/**
	 * 掲示板データ全体を設定
	 * @param data 掲示板データのリスト
	 */
	public void setData(List<BoardData> data) {
		this.data = data;
	}
	
    public BoardData getItem() {
        return item;
    }
    
    public void setItem(BoardData item) {
        this.item = item;
    }
	// ========== Actionメソッド ==========

    // 一覧表示 (リスト画面)
    public String list() {
    	logger.debug("【一覧表示】list()メソッド開始");
        data = Board.getChatData();
        logger.info("【一覧表示】投稿件数: " + (data != null ? data.size() : 0));
        return "list";
    }
    
    // 詳細表示
    public String detail() {
    	logger.debug("【詳細表示】detail()メソッド開始 - boardId: " + boardId);
        item = Board.getDataById(boardId);
        if (item != null) {
            Board.incrementViewCount(boardId);  // 閲覧数+1
            logger.info("【詳細表示】投稿表示成功 - boardId: " + boardId);
            
            comments = Comment.getCommentsByBoardId(boardId);
            logger.info("【詳細表示】投稿表示成功 - コメント数: " + comments.size());
           
            return "detail";
        } else {
        	logger.warn("【詳細表示】投稿が見つかりませんでした - boardId: " + boardId);
            addActionError("投稿が見つかりませんでした");
            return "error";
        }
    }
    
    // 新規投稿フォーム表示
    public String input() {
        return "input";
    }
    
	/**
	 * 【executeメソッド - デフォルトのアクションメソッド】
	 * 
	 * 呼び出しタイミング:
	 * - JSPで <s:submit value="投稿"/> がクリックされた時
	 * - <s:form action="board.action"> が送信された時
	 * - method属性が指定されていない場合のデフォルトメソッド
	 * 
	 * 処理の流れ:
	 * 1. isValid()メソッドで入力値をチェック
	 * 2. 入力が正しい場合:
	 *    - Board.addChatData()を呼んで新しい投稿を追加
	 *    - 追加後の全データをdataフィールドに格納
	 * 3. 入力にエラーがある場合:
	 *    - 投稿は追加せず、現在のデータのみを取得
	 *    - エラーメッセージはJSPの<s:actionerror/>で表示される
	 * 4. "success"を返してboard.jspを表示
	 * 
	 * @return 処理結果を表す文字列（"success"）
	 */
    public String execute() {
    	  logger.debug("【新規投稿】execute()メソッド開始");
          logger.debug("【新規投稿】writer: " + writer + ", title: " + title);
          
        if (isValid()) {
            boolean success = Board.addChatData(category, title, content, writer, ipAdress);
            if (success) {
            	logger.info("【新規投稿】投稿成功 - writer: " + writer);
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
	
	/**
	 * 【updateメソッド - 更新専用のアクションメソッド】
	 * 
	 * 呼び出しタイミング:
	 * - JSPで <s:submit value="更新" method="update"/> がクリックされた時
	 * - method属性で"update"が指定されているため、
	 *   execute()ではなくこのメソッドが呼ばれる
	 * 
	 * 処理内容:
	 * - 掲示板の最新データを取得するだけ
	 * - 新しい投稿は追加しない
	 * - バリデーションも行わない（チェック不要）
	 * 
	 * 使用目的:
	 * - 他のユーザーが投稿した最新データを確認したい時
	 * - ページをリロードせずに最新情報を取得したい時
	 * 
	 * @return 処理結果を表す文字列（"success"）
	 */
	
	  
    public String editForm() {
    	logger.debug("【編集フォーム】editForm()メソッド開始 - boardId: " + boardId);
        item = Board.getDataById(boardId);
        if (item != null) {
            // 既存データをフィールドに設定
            this.category = item.getCategory();
            this.title = item.getTitle();
            this.content = item.getContent();
            this.writer = item.getWriter();
            logger.info("【編集フォーム】編集画面表示 - boardId: " + boardId);
            return "edit";
        } else {
        	logger.warn("【編集フォーム】投稿が見つかりませんでした - boardId: " + boardId);
            addActionError("投稿が見つかりませんでした");
            data = Board.getChatData();
            return "list";
        }
    }
    
    /**
     * 編集アクション：投稿を更新する
     * - 既存の投稿内容を変更できるようにする
     * 【呼び出し元】
     * board.jspの編集フォーム：action="boardEdit.action"
     * @return "success"を返してboard.jspを表示
     */
	public String edit() {
		 logger.debug("【編集】edit()メソッド開始 - boardId: " + boardId);
	       
		if (title != null && content != null && writer != null &&
				!title.equals("") && !content.equals("") && !writer.equals("")) {
			boolean success = Board.updateData(boardId, category, title, content, writer);
			if (success) {
	            logger.info("【編集】更新成功 - boardId: " + boardId + 
	                       ", 作成者: " + writer);
	        } else {
	            logger.error("【編集】更新失敗 - boardId: " + boardId);
	            addActionError("投稿が見つかりませんでした");
	        }
		} else {
			logger.warn("【編集】入力値エラー");
			addActionError("すべて入力してください");
		}
		return "list";
	}

    /**
     * 削除アクション：投稿を削除する
     * - 不要な投稿を削除できるようにする
     * 【呼び出し元】
     * board.jspの削除フォーム：action="boardDelete.action"
     * @return "success"を返してboard.jspを表示
     */
	public String delete() {
		logger.debug("【削除】delete()メソッド開始 - boardId: " + boardId);
   		boolean success = Board.deleteData(boardId);
		if (!success) {
			logger.error("【削除】削除失敗 - boardId: " + boardId);
			addActionError("投稿が見つかりませんでした");
		}
		logger.info("【削除】削除成功 - boardId: " + boardId);
		return "list";
	}

	public boolean isValid() {
		/*
		 * 【バリデーションの実装パターン】
		 * 
		 * パターン1: validate()メソッドをオーバーライド（今回不採用）
		 * public void validate() {
		 *     // 全てのアクションメソッドで実行される
		 * }
		 * 
		 * パターン2: カスタムメソッドを作成（今回採用）
		 * public boolean isValid() {
		 *     // 必要な時だけ呼び出せる
		 * }
		 * 
		 * パターン3: XMLバリデーション
		 * BoardAction-validation.xmlファイルで定義
		 * 
		 * パターン4: アノテーションバリデーション
		 * @RequiredStringValidator(message="必須です")
		 */

		if (title == null || title.equals("")) {
			logger.debug("【バリデーション】タイトル未入力");
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
			logger.warn("【バリデーション】名前未入力");
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
}