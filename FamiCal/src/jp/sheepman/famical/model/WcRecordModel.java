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
import jp.sheepman.famical.util.CommonConst;
import android.content.Context;

/**
 * @author sheepman
 *
 */
public class WcRecordModel extends BaseModel {
	private Context mContext;

	private final String tablename = CommonConst.TABLE_NAME_WC_RECORD;
	final String sql01 = "SELECT family_id ,wc_record_date, pe_count, po_count, comment "
						+ "FROM wc_record "
						+ "WHERE family_id = ? AND wc_record_date BETWEEN ? AND ? ";

	final String sql02 = "SELECT family_id ,wc_record_date, pe_count, po_count, comment "
						+ "FROM wc_record "
						+ "WHERE family_id = ? AND wc_record_date = ? ";
	public WcRecordModel(Context context){
		this.mContext = context;
	}
	
	/**
	 * 記録データを取得
	 */
	public List<BaseForm> selectByMonth(WcRecordForm form) {
		List<BaseForm> list = new ArrayList<BaseForm>();
		List<BaseEntity> data = new ArrayList<BaseEntity>();
		
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		List<String> params = new ArrayList<String>();

		Calendar wc_record_date = form.getWc_record_date();
		int family_id = form.getFamily_id();
		String start_date = CalendarUtil.cal2str(CalendarUtil.getMonthFirstDate(wc_record_date));
		String end_date = CalendarUtil.cal2str(CalendarUtil.getMonthLastDate(wc_record_date));
		
		params.add(String.valueOf(family_id));
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

	/**
	 * 件数の取得
	 * @param form
	 * @return
	 */
	public List<BaseEntity> selectByPrimary(WcRecordForm form) {
		List<BaseEntity> ret = new ArrayList<BaseEntity>();
		List<String> params = new ArrayList<String>();
		
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		
		//検索条件をセット
		params.add(String.valueOf(form.getFamily_id()));
		params.add(CalendarUtil.cal2str(form.getWc_record_date()));
		//データを取得
		ret = dbutil.select(sql02, params, this);
		dbutil.close();
		
		return ret;
	}

	/**
	 * 記録データの更新処理
	 * @param form
	 */
	public void update(WcRecordForm form){
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

	/**
	 * 記録データのInsert処理
	 */
	public void insert(WcRecordForm form){
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		//input情報をセット
		WcRecordEntity entity = new WcRecordEntity();
		entity.setFamily_id(form.getFamily_id());
		entity.setWc_record_date(CalendarUtil.cal2str(form.getWc_record_date()));
		entity.setPe_count(form.getPe_count());
		entity.setPo_count(form.getPo_count());
		entity.setComment(form.getComment());
		//Insert処理
		dbutil.insert(tablename, entity);
		dbutil.close();
	}

	/**
	 * 記録データの削除処理
	 * @param form
	 */
	public void delete(WcRecordForm form){
		final String whereClause = "family_id = ? "
								  + "AND wc_record_date = ? ";
		List<String> list = new ArrayList<String>();
		
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		//条件設定
		list.add(String.valueOf(form.getFamily_id()));
		list.add(CalendarUtil.cal2str(form.getWc_record_date()));
		//Update処理
		dbutil.delete(tablename, whereClause, list);
		dbutil.close();
	}


	/**
	 * Family_idに基づく記録データの削除処理
	 * @param family_id family_id
	 */
	public void deleteByFamilyId(int family_id){
		final String whereClause = "family_id = ? ";
		List<String> list = new ArrayList<String>();

		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		//条件設定
		list.add(String.valueOf(family_id));
		//Update処理
		dbutil.delete(tablename, whereClause, list);
		dbutil.close();
	}


	@Override
	public BaseEntity getEntity() {
		return new WcRecordEntity();
	}
}
