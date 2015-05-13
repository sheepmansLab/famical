package jp.sheepman.famical.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.sheepman.common.entity.BaseEntity;
import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.model.BaseModel;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.common.util.DatabaseUtil;
import jp.sheepman.famical.entity.FamilyEntity;
import jp.sheepman.famical.form.FamilyForm;
import jp.sheepman.famical.util.CommonConst;
import android.content.Context;

public class FamilyModel extends BaseModel {
	
	private Context mContext;
	private final String tablename = CommonConst.TABLE_NAME_FAMILY;

	private final String sql01 = "SELECT family_id, family_name, birth_date, image_id FROM family WHERE family_id = ? ";
	private final String sql02 = "SELECT family_id, family_name, birth_date, image_id FROM family ";
	private final String sql03 = "SELECT family_id, family_name, birth_date, image_id FROM family WHERE rowid = ? ";

	public FamilyModel(Context context) {
		mContext = context;
	}

	/**
	 * 家族データをselectする
	 * @param form
	 */
	public List<BaseForm> selectById(FamilyForm form){
		List<BaseForm> list = new ArrayList<BaseForm>();
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		
		List<String> params = new ArrayList<String>();
		params.add(String.valueOf(form.getFamily_id()));
		
		Iterator<BaseEntity> ite = dbutil.select(sql01, params, this).iterator();
		while(ite.hasNext()){
			FamilyEntity entity = (FamilyEntity)ite.next();
			FamilyForm data = new FamilyForm();
			data.setFamily_id(entity.getFamily_id());
			data.setFamily_name(entity.getFamily_name());
			data.setBirth_date(CalendarUtil.str2cal(entity.getBirth_date()));
			data.setImage_id(entity.getImage_id());
			list.add(data);
		}
		
		dbutil.close();
		return list;
	}
	
	/**
	 * 検索条件なしでデータを取得する
	 * @return
	 */
	public List<BaseForm> selectAll(){
		List<BaseForm> list = new ArrayList<BaseForm>();
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		
		List<String> params = new ArrayList<String>();
		
		Iterator<BaseEntity> ite = dbutil.select(sql02, params, this).iterator();
		while(ite.hasNext()){
			FamilyEntity entity = (FamilyEntity)ite.next();
			FamilyForm data = new FamilyForm();
			data.setFamily_id(entity.getFamily_id());
			data.setFamily_name(entity.getFamily_name());
			data.setBirth_date(CalendarUtil.str2cal(entity.getBirth_date()));
			data.setImage_id(entity.getImage_id());
			list.add(data);
		}
		
		dbutil.close();
		return list;
	}

	/**
	 * rowidからデータを取得する
	 * @param rowid
	 * @return
	 */
	public List<BaseForm> selectByRowId(long rowid){
		List<BaseForm> list = new ArrayList<BaseForm>();
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		
		List<String> params = new ArrayList<String>();
		params.add(String.valueOf(rowid));
		
		Iterator<BaseEntity> ite = dbutil.select(sql03, params, this).iterator();
		while(ite.hasNext()){
			FamilyEntity entity = (FamilyEntity)ite.next();
			FamilyForm data = new FamilyForm();
			data.setFamily_id(entity.getFamily_id());
			data.setFamily_name(entity.getFamily_name());
			data.setBirth_date(CalendarUtil.str2cal(entity.getBirth_date()));
			data.setImage_id(entity.getImage_id());
			list.add(data);
		}
		
		dbutil.close();
		return list;
	}

	/**
	 * 家族データをUpdateする
	 * @param form
	 */
	public void update(FamilyForm form){
		final String whereClause = "family_id = ? ";
		List<String> list = new ArrayList<String>();
		
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		
		FamilyEntity entity = new FamilyEntity();
		entity.setFamily_id(form.getFamily_id());
		entity.setFamily_name(form.getFamily_name());
		entity.setBirth_date(CalendarUtil.cal2str(form.getBirth_date()));
		entity.setImage_id(form.getImage_id());
		
		list.add(String.valueOf(form.getFamily_id()));
		
		dbutil.update(tablename, whereClause, entity, list);
		
		dbutil.close();
	}

	/**
	 * 家族データをInsertする
	 * @param form
	 */
	public long insert(FamilyForm form){
		long rowid = 0;
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		try {
			FamilyEntity entity = new FamilyEntity();
			//entity.setFamily_id(form.getFamily_id());	//自動採番のため不要
			if(form.getFamily_name() != null){
				entity.setFamily_name(form.getFamily_name());
			}
			if(form.getBirth_date() != null){
				entity.setBirth_date(CalendarUtil.cal2str(form.getBirth_date()));
			}
			if(form.getImage_id() == 0){
				entity.setImage_id(form.getImage_id());
			}
			//Insert処理
			rowid = dbutil.insert(tablename , entity);
		} finally {
			dbutil.close();
		}
		return rowid;
	}
	
	/**
	 * 家族データをdeleteする
	 * @param form
	 */
	public void delete(FamilyForm form){
		final String whereClause = "family_id = ? ";
		List<String> list = new ArrayList<String>();
		
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		
		list.add(String.valueOf(form.getFamily_id()));
		dbutil.delete(tablename, whereClause, list);
		
		dbutil.close();
	}
	
	@Override
	public BaseEntity getEntity() {
		return new FamilyEntity();
	}
}
