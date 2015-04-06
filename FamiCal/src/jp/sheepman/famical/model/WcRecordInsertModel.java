/**
 * 
 */
package jp.sheepman.famical.model;

import jp.sheepman.common.model.BaseModel;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.common.util.DatabaseUtil;
import jp.sheepman.famical.entity.WcRecordEntity;
import jp.sheepman.famical.form.WcRecordForm;
import jp.sheepman.famical.util.CommonConst;
import android.content.Context;

/**
 * @author sheepman
 *
 */
public class WcRecordInsertModel extends BaseModel {
	private final String tablename = CommonConst.TABLE_NAME_WC_RECORD;
	private Context mContext;
	
	public WcRecordInsertModel(Context context){
		mContext = context;
	}
	
	/**
	 * 記録データのInsert処理
	 */
	public void insert(WcRecordForm form){
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		//input情報をセット
		WcRecordEntity entity = new WcRecordEntity();
		entity.setFamily_id(form.getFamily_id());
		entity.setWc_record_date( CalendarUtil.cal2str(form.getWc_record_date()));
		entity.setPe_count(form.getPe_count());
		entity.setPo_count(form.getPo_count());
		entity.setComment(form.getComment());
		//Insert処理
		dbutil.insert(tablename , entity);
		dbutil.close();
	}
}
