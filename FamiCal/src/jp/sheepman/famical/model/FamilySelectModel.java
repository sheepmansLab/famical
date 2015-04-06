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
import android.content.Context;

public class FamilySelectModel extends BaseModel {
	
	private Context mContext;
	private final String sql01 = "SELECT FROM family WHERE family_id = ? ";
	
	public FamilySelectModel(Context context) {
		mContext = context;
	}
	
	/**
	 * 家族データをdeleteする
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
	
	@Override
	public BaseEntity getEntity() {
		return new FamilyEntity();
	}
}
