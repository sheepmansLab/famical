package jp.sheepman.famical.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.sheepman.common.entity.BaseEntity;
import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.model.BaseModel;
import jp.sheepman.common.util.DatabaseUtil;
import jp.sheepman.famical.entity.ImagesEntity;
import jp.sheepman.famical.form.ImagesForm;
import android.content.Context;

public class ImagesSelectModel extends BaseModel {
	
	private Context mContext;
	private final String sql01 = "SELECT image_id, image_name, image, comment FROM images WHERE image_id = ? ";
	private final String sql02 = "SELECT image_id, image_name, image, comment FROM images ";
	private final String sql03 = "SELECT image_id, image_name, image, comment FROM images WHERE rowid = ? ";
	
	public ImagesSelectModel(Context context) {
		mContext = context;
	}
	
	/**
	 * 画像データをselectする
	 * @param form
	 */
	public List<BaseForm> selectById(ImagesForm form){
		List<BaseForm> list = new ArrayList<BaseForm>();
		DatabaseUtil dbutil = new DatabaseUtil(mContext);
		dbutil.open();
		
		List<String> params = new ArrayList<String>();
		params.add(String.valueOf(form.getImage_id()));
		
		Iterator<BaseEntity> ite = dbutil.select(sql01, params, this).iterator();
		while(ite.hasNext()){
			ImagesEntity entity = (ImagesEntity)ite.next();
			ImagesForm data = new ImagesForm();
			data.setImage_id(entity.getImage_id());
			data.setImage_name(entity.getImage_name());
			data.setImage(entity.getImage());
			data.setComment(entity.getComment());
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
			ImagesEntity entity = (ImagesEntity)ite.next();
			ImagesForm data = new ImagesForm();
			data.setImage_id(entity.getImage_id());
			data.setImage_name(entity.getImage_name());
			data.setImage(entity.getImage());
			data.setComment(entity.getComment());
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
		params.add(String.valueOf(String.valueOf(rowid)));
		
		Iterator<BaseEntity> ite = dbutil.select(sql03, params, this).iterator();
		while(ite.hasNext()){
			ImagesEntity entity = (ImagesEntity)ite.next();
			ImagesForm data = new ImagesForm();
			data.setImage_id(entity.getImage_id());
			data.setImage_name(entity.getImage_name());
			data.setImage(entity.getImage());
			data.setComment(entity.getComment());
			list.add(data);
		}
		
		dbutil.close();
		return list;
	}
	
	@Override
	public BaseEntity getEntity() {
		return new ImagesEntity();
	}
}
