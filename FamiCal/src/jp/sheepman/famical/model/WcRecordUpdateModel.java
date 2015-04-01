/**
 * 
 */
package jp.sheepman.famical.model;

import java.util.ArrayList;
import java.util.List;

import jp.sheepman.common.entity.BaseEntity;
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
public class WcRecordUpdateModel extends BaseModel {
	private final String tablename = CommonConst.TABLE_NAME_WC_RECORD;
	private Context mContext;
	
	public WcRecordUpdateModel(Context context){
		mContext = context;
	}
	
	/**
	 * 記録データの更新処理
	 * @param form
	 */
	public void execute(WcRecordForm form){
		final String whereClause = "family_id = ? "
								 + "AND wc_record_date = ? ";
		List<String> list = new ArrayList<String>();
		
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		//update情報をセット
		WcRecordEntity entity = new WcRecordEntity();
		entity.setFamily_id(form.getFamily_id());
		entity.setWc_record_date( CalendarUtil.cal2str(form.getWc_record_date()));
		entity.setPe_count(form.getPe_count());
		entity.setPo_count(form.getPo_count());
		entity.setComment(form.getComment());
		//条件設定
		list.add(String.valueOf(form.getFamily_id()));
		list.add(CalendarUtil.cal2str(form.getWc_record_date()));
		//Update処理
		dbutil.update(tablename, whereClause, entity, list);
		dbutil.close();
	}
	
	@Override
	public BaseEntity getEntity() {
		return new WcRecordEntity();
	}
}
