/**
 * 
 */
package jp.sheepman.famical.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import jp.sheepman.common.entity.BaseEntity;
import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.model.BaseModel;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.common.util.DatabaseUtil;
import jp.sheepman.famical.entity.WcRecordEntity;
import jp.sheepman.famical.form.WcRecordForm;
import android.content.Context;

/**
 * @author sheepman
 *
 */
public class WcRecordSelectModel extends BaseModel {
	private Context mContext;

	final String sql01 = "SELECT family_id ,wc_record_date, pe_count, po_count, comment "
						+ "FROM wc_record "
						+ "WHERE wc_record_date BETWEEN ? AND ? ";
	
	public WcRecordSelectModel(Context context){
		this.mContext = context;
	}
	
	/**
	 * 記録データを取得
	 */
	public List<BaseForm> select(WcRecordForm form) {
		List<BaseForm> list = new ArrayList<BaseForm>();
		List<BaseEntity> data = new ArrayList<BaseEntity>();
		
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		List<String> params = new ArrayList<String>();

		Calendar wc_record_date = form.getWc_record_date();
		String start_date = CalendarUtil.cal2str(CalendarUtil.getMonthFirstDate(wc_record_date));
		String end_date = CalendarUtil.cal2str(CalendarUtil.getMonthLastDate(wc_record_date));
		
		params.add(start_date);
		params.add(end_date);
		data = dbutil.select(sql01, params, this);
		dbutil.close();
		
		Iterator<BaseEntity> ite = data.iterator();
		while(ite.hasNext()){
			WcRecordEntity entity = (WcRecordEntity)ite.next();
			WcRecordForm f = new WcRecordForm();
			f.setFamily_id(entity.getFamily_id());
			f.setWc_record_date(CalendarUtil.str2cal(entity.getWc_record_date()));
			f.setPe_count(entity.getPe_count());
			f.setPo_count(entity.getPo_count());
			f.setComment(entity.getComment());
			list.add(f);
		}
		return list;
	}
	
	@Override
	public BaseEntity getEntity() {
		return new WcRecordEntity();
	}
}
