package jp.sheepman.famical.model;

import java.util.ArrayList;
import java.util.List;

import jp.sheepman.common.entity.BaseEntity;
import jp.sheepman.common.model.BaseModel;
import jp.sheepman.common.util.DatabaseUtil;
import jp.sheepman.famical.entity.FamilyEntity;
import jp.sheepman.famical.form.FamilyForm;
import jp.sheepman.famical.util.CommonConst;
import android.content.Context;

public class FamilyDeleteModel extends BaseModel {
	
	private Context mContext;
	private final String tablename = CommonConst.TABLE_NAME_FAMILY;
	
	public FamilyDeleteModel(Context context) {
		mContext = context;
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
