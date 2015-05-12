package jp.sheepman.famical.model;

import jp.sheepman.common.entity.BaseEntity;
import jp.sheepman.common.model.BaseModel;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.common.util.DatabaseUtil;
import jp.sheepman.famical.entity.FamilyEntity;
import jp.sheepman.famical.entity.ImagesEntity;
import jp.sheepman.famical.form.FamilyForm;
import jp.sheepman.famical.form.ImagesForm;
import jp.sheepman.famical.util.CommonConst;
import android.content.Context;

public class ImagesInsertModel extends BaseModel {
	
	private Context mContext;
	private final String tablename = CommonConst.TABLE_NAME_IMAGES;
	
	public ImagesInsertModel(Context context) {
		mContext = context;
	}
	
	/**
	 * 家族データをInsertする
	 * @param form
	 */
	public long insert(ImagesForm form){
		long rowid = 0;
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		try {
			ImagesEntity entity = new ImagesEntity();
			if(form.getImage_name() != null){
				entity.setImage_name(form.getImage_name());
			}
			if(form.getImage() != null){
				entity.setImage(form.getImage());
			}
			if(form.getComment() != null){
				entity.setComment(form.getComment());
			}
			//Insert処理
			rowid = dbutil.insert(tablename , entity);
		} finally {
			dbutil.close();
		}
		return rowid;
	}
	
	@Override
	public BaseEntity getEntity() {
		return new ImagesEntity();
	}
}
