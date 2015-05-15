/**
 * 
 */
package jp.sheepman.famical.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import jp.sheepman.common.activity.BaseActivity;
import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.fragment.BaseDialogFragment;
import jp.sheepman.common.fragment.BaseFragment;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.famical.R;
import jp.sheepman.famical.form.ActivityForm;
import jp.sheepman.famical.form.FamilyForm;
import jp.sheepman.famical.form.ImagesForm;
import jp.sheepman.famical.model.FamilyModel;
import jp.sheepman.famical.model.ImagesModel;
import jp.sheepman.famical.util.CommonConst;
import jp.sheepman.famical.util.CommonLogUtil;

/**
 * @author sheepman
 *
 */
public class FamilyInputDialogFragment extends BaseDialogFragment {
	private AQuery aq;
	private Context mContext;
	private LayoutInflater inflator;
	
	private FamilyForm form;
	private boolean isModal = false;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		CommonLogUtil.method_start();
		this.aq = new AQuery(getActivity());
		this.mContext = getActivity();
		this.inflator = LayoutInflater.from(mContext);
		this.form = new FamilyForm();
		
		//ディスプレイ情報を取得
		final float density = getResources().getDisplayMetrics().density;  
		//ダイアログの横幅：280dpi
		final int dialogWidth = (int) (400 * density);

		//Viewのレイアウトを取得
		View view = this.inflator.inflate(R.layout.fragment_family_input, null);
		
		//引数を取得
		Bundle args = getArguments();
		if(args != null){
			//IDをformにセット
			form.setFamily_id(args.getInt(CommonConst.BUNDLE_KEY_FAMILY_ID));
			this.isModal = args.getBoolean(CommonConst.BUNDLE_KEY_IS_MODAL);
		}
		//rootをDialog内のViewにセット
		aq.recycle(view);
		//各項目に初期値をセット
		aq.id(R.id.btnDialogInput).clicked(lsnrBtnSubmit);
		aq.id(R.id.btnDialogClear).clicked(lsnrClickClear);
		aq.id(R.id.btnDialogDelete).clicked(lsnrClickDelete);
		aq.id(R.id.btnClose).clicked(lsnrBtnClose);
		aq.id(R.id.ivDialogImage).clicked(lsnrBtnImage);
		
		//初期値をセット
		setData();
		
		//Dialogを生成
		Dialog dialog = new Dialog(mContext);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//Viewをセット
		dialog.setContentView(view);
		//Dialogの横幅を指定
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();  
		lp.width = dialogWidth;
		dialog.getWindow().setAttributes(lp);
		
		//モーダル時の処理
		if(isModal){
			aq.id(R.id.btnClose).visibility(View.GONE);
			aq.id(R.id.btnDialogDelete).visibility(View.GONE);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
		}
		CommonLogUtil.method_end();
		return dialog;
	}
	
	/**
	 * データを取得して設定を戻す
	 */
	private void setData(){
		CommonLogUtil.method_start();
		FamilyModel model = new FamilyModel(mContext);
		Iterator<BaseForm> ite = model.selectById(form).iterator();
		if(ite.hasNext()){
			this.form = (FamilyForm)ite.next();
            ImagesModel imodel = new ImagesModel(mContext);
            ImagesForm iform = new ImagesForm();
            iform.setImage_id(form.getImage_id());
            Iterator<BaseForm> iite = imodel.selectById(iform).iterator();
            if(iite.hasNext()){
                iform = (ImagesForm)iite.next();
            }
			
			aq.id(R.id.etDialogFamilyName).text(form.getFamily_name());
			aq.id(R.id.etDialogBirthDay).text(CalendarUtil.cal2str(form.getBirth_date()));

            aq.id(R.id.ivDialogImage).image(convertByte2Bitmap(iform.getImage()));
			//削除ボタンを活性化
			aq.id(R.id.btnDialogDelete).visibility(View.VISIBLE);
		} else {
			//削除ボタンを非活性化
			aq.id(R.id.btnDialogDelete).visibility(View.GONE);
		}
		CommonLogUtil.method_end();
	}
	
	/**
	 * データをInsertする
	 */
	private void inputData(){
		CommonLogUtil.method_start();
		FamilyModel modelFamily = new FamilyModel(mContext);
		ImagesModel modelImages = new ImagesModel(mContext);
		ImagesForm formImages = new ImagesForm();
		//Toastのメッセージ
		String msg = "";
		//最新の値をセット
		form.setFamily_name(aq.id(R.id.etDialogFamilyName).getText().toString());
		form.setBirth_date(CalendarUtil.str2cal(aq.id(R.id.etDialogBirthDay).getText().toString()));
		//画像データを取得
		BitmapDrawable bd = (BitmapDrawable)((ImageView)aq.id(R.id.ivDialogImage).getView()).getDrawable();
		if(bd != null){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bd.getBitmap().compress(CompressFormat.PNG, 100, baos);
			//formにセット
			formImages.setImage(baos.toByteArray());
		}
		
		//件数が0以上ならUpdate、0ならInsert
		long rowid = 0;
		if(modelFamily.selectById(this.form).size() == 0){
			rowid = modelImages.insert(formImages);
			formImages = (ImagesForm)modelImages.selectByRowId(rowid).get(0);
			this.form.setImage_id(formImages.getImage_id());
			
			rowid = modelFamily.insert(this.form);
			//Insertしたデータをformにセット
			form = (FamilyForm)modelFamily.selectByRowId(rowid).get(0);
			msg = "登録しました";
		} else {
            formImages.setImage_id(form.getImage_id());
            Iterator ite = modelImages.selectById(formImages).iterator();
            if(ite.hasNext()){
                formImages = (ImagesForm)ite.next();
                modelImages.update(formImages);
            } else {
                rowid = modelImages.insert(formImages);
                formImages = (ImagesForm)modelImages.selectByRowId(rowid).get(0);
                form.setImage_id(formImages.getImage_id());
            }
			modelFamily.update(this.form);
			msg = "更新しました";
		}
		showToast(msg);
		CommonLogUtil.method_end();
	}
	
	/**
	 * データを削除する
	 */
	private void deleteData(){
		CommonLogUtil.method_start();
		FamilyModel model = new FamilyModel(mContext);
		model.delete(this.form);
		showToast("削除しました");
		CommonLogUtil.method_end();
	}
	
	/**
	 * ImageViewの画像データをbyte配列にして返却する
	 * @return byte配列
	 */
	private byte[] getImageByteArray(){
		CommonLogUtil.method_start();
		byte[] data = null;
		BitmapDrawable bd = (BitmapDrawable)((ImageView)aq.id(R.id.ivDialogImage).getView()).getDrawable();
		if(bd != null){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bd.getBitmap().compress(CompressFormat.PNG, 100, baos);
			data = baos.toByteArray();
		}
		CommonLogUtil.method_end();
		return data;
	}

    /**
     * byte配列をBitmapにして返却
     * @param data byte配列
     * @return Bitmapデータ
     */
    private Bitmap convertByte2Bitmap(byte[] data){
        Bitmap bmp = null;
        if(data != null && data.length > 0){
            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        return bmp;
    }
	
	/**
	 * 登録ボタン押下時イベント
	 */
	private OnClickListener lsnrBtnSubmit = new OnClickListener() {
		@Override
		public void onClick(View v) {
			CommonLogUtil.method_start();
			inputData();
			dismiss();
			CommonLogUtil.method_end();
		}
	};
	
	/**
	 * 削除ボタン押下時イベント
	 */
	private OnClickListener lsnrClickDelete = new OnClickListener() {
		@Override
		public void onClick(View v) {
			CommonLogUtil.method_start();
			deleteData();
			dismiss();
			CommonLogUtil.method_end();
		}
	};
	
	/**
	 * 画面の閉じるボタン押下時のイベントリスナー
	 */
	private OnClickListener lsnrBtnClose = new OnClickListener() {
		@Override
		public void onClick(View v) {
			CommonLogUtil.method_start();
			dismiss();
			CommonLogUtil.method_end();
		}
	};
	
	/**
	 * クリアボタン押下時のイベント
	 */
	private OnClickListener lsnrClickClear = new OnClickListener() {
		@Override
		public void onClick(View v) {
			CommonLogUtil.method_start();
			setData();
			CommonLogUtil.method_end();
		}
	};
	
	/**
	 * 画像押下時のイベント
	 */
	OnClickListener lsnrBtnImage = new OnClickListener() {
		@Override
		public void onClick(View v) {
			CommonLogUtil.method_start();
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			startActivityForResult(intent, CommonConst.REQUEST_CODE_GALLERY);
			CommonLogUtil.method_end();
		}
	};
	
	/**
	 * startActovityForResultの結果を受け取る
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
		CommonLogUtil.method_start();
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CommonConst.REQUEST_CODE_GALLERY:
			if(resultCode == Activity.RESULT_OK){
				try {
					Bitmap bmp = Media.getBitmap(mContext.getContentResolver(), data.getData());
					aq.id(R.id.ivDialogImage).image(bmp);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
		CommonLogUtil.method_end();
	}
		
	/**
	 * Toast表示
	 * @param msg
	 */
	private void showToast(String msg){
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * ダイアログのクローズ
	 */
	@Override
	public void onDismiss(DialogInterface dialog) {
		CommonLogUtil.method_start();
		if(getTargetFragment() instanceof BaseFragment){
			((BaseFragment)getTargetFragment()).callback(form);
		}else if(isModal){
			ActivityForm mainForm = new ActivityForm();
			mainForm.setFamily_id(form.getFamily_id());
			((BaseActivity)getActivity()).callback(mainForm);
		}
		super.onDismiss(dialog);
		CommonLogUtil.method_end();
	}
	
	@Override
	public void callback() {
		CommonLogUtil.method_start();
		CommonLogUtil.method_end();
	}

	@Override
	public void callback(BaseForm arg0) {
		CommonLogUtil.method_start();
		this.callback();
		CommonLogUtil.method_end();
	}
}
