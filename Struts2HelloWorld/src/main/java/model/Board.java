package model;

import java.text.SimpleDateFormat;
// 日付を指定した形式の文字列に変換するクラス
import java.util.Date;
// 現在の日時を取得するクラス
import java.util.List;
// リスト（配列のような動的な集合）のインターフェース
import java.util.Vector;
// スレッドセーフなListの実装クラス
// 複数のユーザーが同時にアクセスしても安全にデータを管理できる

/**
 * 【Boardクラス】
 * 掲示板のデータを管理するモデルクラス
 * 
 * 役割:
 * - 掲示板の投稿データをメモリ上に保存
 * - 投稿の追加機能
 * - 投稿一覧の取得機能
 * 
 * 特徴:
 * - すべてのフィールドとメソッドがstaticで宣言されている
 * - つまり、アプリケーション全体で1つのデータを共有する（シングルトンパターン）
 * - 複数のユーザーが同じ掲示板データにアクセスできる
 * 
 * 注意点:
 * - データはメモリ上にのみ存在する（揮発性）
 * - サーバーを再起動すると全データが消える
 * - 実際の業務アプリではデータベースを使用すべき
 */
public class Board {

	// ========== staticフィールド ==========
	// staticを付けることで、クラスレベルの変数になる
	// つまり、Boardクラスのインスタンスを何個作っても、
	// このboardリストは1つだけ存在し、全てのインスタンスで共有される

	private static List<BoardData> board = new Vector<BoardData>();
	/*
	 * 【掲示板データを格納するリスト】
	 * 
	 * - List<BoardData>: BoardDataオブジェクトを格納するリスト型
	 * - static: アプリケーション全体で1つだけ存在する共有変数
	 * - private: このクラス内からのみアクセス可能（カプセル化）
	 * - new Vector<BoardData>(): Vectorクラスでリストを初期化
	 * 
	 * 【Vectorを使う理由】
	 * - Vector: スレッドセーフなList実装
	 * - 複数のユーザーが同時に掲示板にアクセスしても、
	 *   データの整合性が保たれる
	 * - 同期化されたメソッドを持つため、マルチスレッド環境で安全
	 * 
	 * 【ArrayListとの違い】
	 * - ArrayList: 高速だがスレッドセーフではない（単一スレッド向け）
	 * - Vector: 少し遅いがスレッドセーフ（マルチスレッド向け）
	 * 
	 * 【メモリイメージ】
	 * board = [BoardData1, BoardData2, BoardData3, ...]
	 *          ↑最新    ↑2番目     ↑3番目
	 * add(0, data)で常に先頭に追加されるため、最新の投稿が最初に来る
	 */

	private static SimpleDateFormat sdformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	/*
	 * 【日付フォーマッター】
	 * 
	 * - SimpleDateFormat: 日付を文字列に変換するクラス
	 * - "yyyy/MM/dd HH:mm:ss": 日付の表示形式を指定
	 * - static: 全ての投稿で同じフォーマッターを共有
	 *   毎回新しいSimpleDateFormatオブジェクトを作るのは無駄なので、
	 *   1つだけ作って使い回す
	 * 
	 * 【使用例】
	 * Date now = new Date();  // 現在日時を取得
	 * String dateStr = sdformat.format(now);  // "2025/10/09 16:46:48"
	 */
	
	private static int nextId = 1; // ID自動採番用
	/*
	 * 各投稿に一意のIDを自動で割り当てるためのカウンター
	 * staticなので、アプリケーション全体で1つのカウンターを共有
	 */
	
	// ========== publicメソッド ==========

	/**
     * 掲示板に新しい投稿を追加するメソッド
     * 
     * @param name 投稿者の名前
     * @param message 投稿メッセージ
     * @param remoteAddress 投稿者のIPアドレス
     * @return 更新後の掲示板データ全体（List<BoardData>）
     * 
     * 【処理の流れ】
     * 1. 新しいBoardDataオブジェクトを作成
     * 2. 引数で受け取ったデータをsetterで設定
     * 3. 現在日時を自動で設定
     * 4. リストの先頭（インデックス0）に追加
     * 5. 更新後のリスト全体を返す
     */
    public static List<BoardData> addChatData(String name, String message, String remoteAddress) {
        /*
         * 【staticメソッド】
         * - インスタンスを作らずに呼び出せる: Board.addChatData(...)
         * - staticフィールド（board、sdformat）にアクセスできる
         * - thisキーワードは使えない（インスタンスが存在しないため）
         */
        
        // 1. 新しい投稿データオブジェクトを作成
        BoardData data = new BoardData();
        /*
         * new BoardData()でメモリ上に新しいオブジェクトを作成
         * この時点では全フィールドがnull
         */
        data.setId(nextId++); // IDを自動設定
        
        // 2. ユーザーが入力したデータを設定
        data.setName(name);
        // 投稿者名をセット。BoardData.setName()が呼ばれる
        
        data.setMessage(message);
        // メッセージ本文をセット。BoardData.setMessage()が呼ばれる
        
        data.setRemoteAddress(remoteAddress);
        // IPアドレスをセット。BoardData.setRemoteAddress()が呼ばれる
        
        // 3. 現在日時を自動で設定
        data.setPostDate(sdformat.format(new Date()));
        /*
         * 【詳細な動作】
         * new Date(): 現在の日時を取得
         *   例: Wed Oct 09 16:46:48 JST 2025
         * 
         * sdformat.format(...): Date型を文字列に変換
         *   "yyyy/MM/dd HH:mm:ss"の形式に従って変換
         *   例: "2025/10/09 16:46:48"
         * 
         * data.setPostDate(...): 変換した文字列をBoardDataに保存
         */
        
        // 4. 作成した投稿データをリストの先頭に追加
        board.add(0, data);
        /*
         * 【List.add(index, element)メソッド】
         * - index: 挿入位置（0は先頭）
         * - element: 追加する要素
         * 
         * 【インデックス0に追加する理由】
         * - 最新の投稿を一番上に表示したいため
         * - add(0, data)で常に先頭に挿入される
         * - 既存のデータは後ろにずれる
         * 
         * 【動作イメージ】
         * 追加前: [古い投稿1, 古い投稿2, 古い投稿3]
         * add(0, 新しい投稿)
         * 追加後: [新しい投稿, 古い投稿1, 古い投稿2, 古い投稿3]
         * 
         * 【別の方法との比較】
         * board.add(data): リストの最後に追加（新しい投稿が下に表示される）
         * board.add(0, data): リストの先頭に追加（新しい投稿が上に表示される）← 採用
         */
        
        // 5. 更新後の掲示板データ全体を返す
        return board;
        /*
         * このリストはBoardActionクラスのdataフィールドに設定され、
         * JSPで表示される
         * 
         * 【呼び出し元での使用】
         * BoardAction.execute()内:
         * data = Board.addChatData(name, message, remoteAddress);
         * → dataフィールドに最新の掲示板データが設定される
         */
    }

	/**
	 * 現在の掲示板データ全体を取得するメソッド
	 * 
	 * @return 掲示板データのリスト（List<BoardData>）
	 * 
	 * 【使用箇所】
	 * 1. BoardAction.update()メソッド内
	 *    - 「更新」ボタンが押された時に最新データを取得
	 * 2. board.jsp内のスクリプトレット
	 *    - 初期表示時にデータを取得
	 */
	public static List<BoardData> getChatData() {
		return board;
		/*
		 * 単純にstaticフィールドboardをそのまま返す
		 * 
		 * 【注意点】
		 * - このメソッドは参照を返すため、呼び出し元で
		 *   リストを変更すると元のboardも変更される
		 * - より安全な実装なら、コピーを返すべき:
		 *   return new ArrayList<>(board);
		 * 
		 * 【なぜgetterが必要か？】
		 * - boardフィールドはprivateなので外部から直接アクセスできない
		 * - このメソッドを通してのみアクセス可能にすることで、
		 *   将来的にアクセス制御やログ出力などを追加しやすい
		 */
	}

	/*
	 * @param id 取得したい投稿のID 
	 * @return 見つかった投稿データ、見つからない場合はnull
	 * IDをキーにして投稿を探すヘルパーメソッド
	 */
	public static BoardData getDataById(int id) {
		for (BoardData data : board) {
			if (data.getId() == id) {
				return data;
			}
		}
		return null;
	}

    /**
     * 投稿を編集（更新）する
     * @param id 編集対象の投稿ID
     * @param name 新しい名前
     * @param message 新しいメッセージ
     * @return 成功時true、失敗時false
     * - 既存の投稿内容を変更できるようにする
     */
	public static boolean updateData(int id, String name, String message) {
		BoardData data = getDataById(id);
		if (data != null) {
			data.setName(name);
			data.setMessage(message);
			data.setPostDate(sdformat.format(new Date()) + " (編集済)");
			return true;
		}
		return false;
	}

	/**
     * 投稿を削除する
     * @param id 削除対象の投稿ID
     * @return 成功時true、失敗時false
     * - 不要な投稿を削除できるようにする
     */
	public static boolean deleteData(int id) {
		BoardData data = getDataById(id);
		if (data != null) {
			board.remove(data);
			return true;
		}
		return false;
	}

	// ========== 補足情報 ==========

	/*
	 * 【このクラスの問題点と解決策】
	 * 
	 * 問題点1: データが永続化されない
	 * - サーバー再起動で全データが消える
	 * - 解決策: データベース（MySQL、PostgreSQL等）を使用
	 * 
	 * 問題点2: メモリ使用量の制限
	 * - 投稿が増え続けるとメモリ不足になる
	 * - 解決策: 最大件数制限、古いデータの自動削除、ページング
	 * 
	 * 問題点3: SimpleDateFormatはスレッドセーフではない
	 * - staticで共有しているため、同時アクセスで問題が起こる可能性
	 * - 解決策: Java 8以降のDateTimeFormatter使用
	 *   private static DateTimeFormatter formatter = 
	 *       DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	 * 
	 * 問題点4: 機能が少ない
	 * - 削除機能、編集機能、検索機能がない
	 * - 解決策: メソッドを追加
	 *   public static boolean deleteData(int index)
	 *   public static boolean updateData(int index, BoardData newData)
	 *   public static List<BoardData> searchData(String keyword)
	 */

}