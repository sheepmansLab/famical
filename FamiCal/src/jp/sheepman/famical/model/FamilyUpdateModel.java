package jp.sheepman.famical.model;

import java.util.ArrayList;
import java.util.List;

import jp.sheepman.common.entity.BaseEntity;
import jp.sheepman.common.model.BaseModel;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.common.util.DatabaseUtil;
import jp.sheepman.famical.entity.FamilyEntity;
import jp.sheepman.famical.form.FamilyForm;
import jp.sheepman.famical.util.CommonConst;
import android.content.Context;

public class FamilyUpdateModel extends BaseModel {
	
	private Context mContext;
	private final String tablename = CommonConst.TABLE_NAME_FAMILY;
	
	public FamilyUpdateModel(Context context) {
		mContext = context;
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
	
	@Override
	public BaseEntity getEntity() {
		return new FamilyEntity();
	}
}
