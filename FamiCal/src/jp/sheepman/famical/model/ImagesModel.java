package jp.sheepman.famical.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.sheepman.common.entity.BaseEntity;
import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.model.BaseModel;
import jp.sheepman.common.util.DatabaseUtil;
import jp.sheepman.famical.entity.ImagesEntity;
import jp.sheepman.famical.form.ImagesForm;
import jp.sheepman.famical.util.CommonConst;

public class ImagesModel extends BaseModel {
	
	private Context mContext;
	private final String tablename = CommonConst.TABLE_NAME_IMAGES;
	private final String sql01 = "SELECT image_id, image_name, image, comment FROM images WHERE image_id = ? ";
	private final String sql02 = "SELECT image_id, image_name, image, comment FROM images ";
	private final String sql03 = "SELECT image_id, image_name, image, comment FROM images WHERE rowid = ? ";
	
	public ImagesModel(Context context) {
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

    /**
     * 画像データをUpdateする
     * @param form
     */
    public void update(ImagesForm form){
        final String whereClause = "image_id = ? ";
        List<String> list = new ArrayList<String>();

        DatabaseUtil dbutil = new DatabaseUtil(mContext);
        dbutil.open();

        ImagesEntity entity = new ImagesEntity();
        entity.setImage_id(form.getImage_id());
        entity.setImage(form.getImage());
        entity.setImage_name(form.getImage_name());
        entity.setComment(form.getComment());

        list.add(String.valueOf(form.getImage_id()));

        dbutil.update(tablename, whereClause, entity, list);

        dbutil.close();
    }
	@Override
	public BaseEntity getEntity() {
		return new ImagesEntity();
	}
}
