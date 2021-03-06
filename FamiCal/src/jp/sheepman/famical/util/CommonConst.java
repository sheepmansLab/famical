/**
 * 
 */
package jp.sheepman.famical.util;

/**
 * @author sheepman
 *
 */
public class CommonConst {
	/** エンコード */
	public static final String ENCODE = "UTF-8";
	/** キャッシュファイル名 */
	public static final String CHACHE_FILE = "chache_family_id.txt";
	
	/**	リクエストコード ギャラリー */
	public static final int REQUEST_CODE_GALLERY = 1;
	
	/** テーブル名 wc_record */
	public static final String TABLE_NAME_WC_RECORD = "wc_record";
	/** テーブル名 family */
	public static final String TABLE_NAME_FAMILY = "family";
	/** テーブル名 images */
	public static final String TABLE_NAME_IMAGES = "images";
	
	/** フラグメントタグ名 カレンダー */
	public static final String FRAGMENT_TAG_CALENDAR = "FRAGMENT_CALENDAR";
	/** フラグメントタグ名 入力 */
	public static final String FRAGMENT_TAG_WCREC_INPUT = "FRAGMENT_TAG_WCREC_INPUT";
	/** フラグメントタグ名 家族選択 */
	public static final String FRAGMENT_TAG_FAMILY_SELECT = "FRAGMENT_TAG_FAMILY_SELECT";
	/** フラグメントタグ名 家族入力 */
	public static final String FRAGMENT_TAG_FAMILY_DIALOG = "FRAGMENT_TAG_FAMILY_DIALOG";

	/** Bundle用Key モーダル判定フラグ */
	public static final String BUNDLE_KEY_IS_MODAL = "isModal";
	/** Bundle用Key wc_record_date */
	public static final String BUNDLE_KEY_WC_RECORD_DATE = "wc_record_date";
	/** Bundle用Key family_id */	
	public static final String BUNDLE_KEY_FAMILY_ID = "family_id";
	/** Bundle用Key mCalendarBase */
	public static final String BUNDLE_KEY_M_CALENDAR_BASE = "mCalendarBase";
	
}
