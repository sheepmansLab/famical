package jp.sheepman.famical.model;

import jp.sheepman.common.entity.BaseEntity;
import jp.sheepman.common.model.BaseModel;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.common.util.DatabaseUtil;
import jp.sheepman.famical.entity.FamilyEntity;
import jp.sheepman.famical.form.FamilyForm;
import jp.sheepman.famical.util.CommonConst;
import android.content.Context;

public class FamilyInsertModel extends BaseModel {
	
	private Context mContext;
	private final String tablename = CommonConst.TABLE_NAME_FAMILY;
	
	public FamilyInsertModel(Context context) {
		mContext = context;
	}
	
	/**
	 * 家族データをInsertする
	 * @param form
	 */
	public void insert(FamilyForm form){
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		
		FamilyEntity entity = new FamilyEntity();
		//entity.setFamily_id(form.getFamily_id());	//自動採番のため不要
		entity.setFamily_name(form.getFamily_name());
		entity.setBirth_date(CalendarUtil.cal2str(form.getBirth_date()));
		entity.setImage_id(form.getImage_id());
		//Insert処理
		dbutil.insert(tablename , entity );
		
		dbutil.close();
	}
	
	@Override
	public BaseEntity getEntity() {
		return new FamilyEntity();
	}
}
