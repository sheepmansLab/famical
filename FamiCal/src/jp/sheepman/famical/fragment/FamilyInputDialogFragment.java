/**
 * 
 */
package jp.sheepman.famical.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;

import jp.sheepman.common.activity.BaseActivity;
import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.fragment.BaseDialogFragment;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.famical.R;
import jp.sheepman.famical.form.ActivityForm;
import jp.sheepman.famical.form.FamilyForm;
import jp.sheepman.famical.form.ImagesForm;
import jp.sheepman.famical.model.FamilyModel;
import jp.sheepman.famical.model.ImagesModel;
import jp.sheepman.famical.model.WcRecordModel;
import jp.sheepman.famical.util.CommonConst;
import jp.sheepman.famical.util.CommonImageUtil;
import jp.sheepman.famical.util.CommonLogUtil;

/**
 * @author sheepman
 *
 */
public class FamilyInputDialogFragment extends BaseDialogFragment {
	private AQuery aq;
	private Context mContext;
	private FamilyForm mForm;
	private boolean mIsModal = false;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		CommonLogUtil.method_start();
		this.aq = new AQuery(getActivity());
		this.mContext = getActivity();
		this.mForm = new FamilyForm();

        LayoutInflater inflater = LayoutInflater.from(mContext);
		
		//ダイアログの横幅：90%
		final int dialogWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);

		//Viewのレイアウトを取得
		View view = inflater.inflate(R.layout.fragment_family_input, null);
		
		//引数を取得
		Bundle args = getArguments();
		if(args != null){
			//IDをformにセット
			mForm.setFamily_id(args.getInt(CommonConst.BUNDLE_KEY_FAMILY_ID));
			this.mIsModal = args.getBoolean(CommonConst.BUNDLE_KEY_IS_MODAL);
		}
		//rootをDialog内のViewにセット
		aq.recycle(view);
		//各項目に初期値をセット
		aq.id(R.id.btnWcRecInputInput).clicked(lsnrBtnSubmit);
		aq.id(R.id.btnWcRecInputClear).clicked(lsnrClickClear);
		aq.id(R.id.btnWcRecInputDelete).clicked(lsnrClickDelete);
		aq.id(R.id.btnClose).clicked(lsnrBtnClose);
		aq.id(R.id.ivDialogImage).clicked(lsnrBtnImage);
        aq.id(R.id.tvDialogBirthDay).clicked(lsnrEtBirthDate);
		
		//初期値をセット
		setupDisplay();
		
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
		if(mIsModal){
			aq.id(R.id.btnClose).visibility(View.GONE);
			aq.id(R.id.btnWcRecInputDelete).visibility(View.GONE);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
		}
		CommonLogUtil.method_end();
		return dialog;
	}
	
	/**
	 * データを取得して設定を戻す
	 */
	private void setupDisplay(){
		CommonLogUtil.method_start();
		FamilyModel model = new FamilyModel(mContext);
		Iterator<BaseForm> ite = model.selectById(mForm).iterator();
		if(ite.hasNext()){
			this.mForm = (FamilyForm)ite.next();
            ImagesModel imodel = new ImagesModel(mContext);
            ImagesForm iform = new ImagesForm();
            iform.setImage_id(mForm.getImage_id());
            Iterator<BaseForm> iite = imodel.selectById(iform).iterator();
            if(iite.hasNext()){
                iform = (ImagesForm)iite.next();
            }
			
			aq.id(R.id.etDialogFamilyName).text(mForm.getFamily_name());
			aq.id(R.id.tvDialogBirthDay).text(CalendarUtil.cal2str(mForm.getBirth_date()));

            aq.id(R.id.ivDialogImage).image(CommonImageUtil.convertByte2Bitmap(iform.getImage()));
			//削除ボタンを活性化
			aq.id(R.id.btnWcRecInputDelete).visibility(View.VISIBLE);
		} else {
			//削除ボタンを非活性化
			aq.id(R.id.btnWcRecInputDelete).visibility(View.GONE);
		}
		CommonLogUtil.method_end();
	}
	
	/**
	 * データをInsertする
	 */
	private void createData(){
		CommonLogUtil.method_start();
		FamilyModel modelFamily = new FamilyModel(mContext);
		ImagesModel modelImages = new ImagesModel(mContext);
		ImagesForm imagesForm = new ImagesForm();
		//Toastのメッセージ
		String msg;
		//最新の値をセット
		mForm.setFamily_name(aq.id(R.id.etDialogFamilyName).getText().toString());
		mForm.setBirth_date(CalendarUtil.str2cal(aq.id(R.id.tvDialogBirthDay).getText().toString()));
		//画像データを取得
		BitmapDrawable bd = (BitmapDrawable) ((ImageView) aq.id(R.id.ivDialogImage).getView()).getDrawable();
		//画像をByte化
		byte[] image = CommonImageUtil.getImageByteArray(bd);

		long rowid = 0;
        //対象者のFamilyの件数が1以上ならUpdate、0ならInsert
        if (modelFamily.selectById(this.mForm).size() == 0) {
			imagesForm.setImage(image);
			rowid = modelImages.insert(imagesForm);
			imagesForm = (ImagesForm) modelImages.selectByRowId(rowid).get(0);
			this.mForm.setImage_id(imagesForm.getImage_id());
			rowid = modelFamily.insert(this.mForm);
			//Insertしたデータをformにセット
			mForm = (FamilyForm) modelFamily.selectByRowId(rowid).get(0);
			msg = "登録しました";
		} else {
			imagesForm.setImage_id(mForm.getImage_id());
			Iterator ite = modelImages.selectById(imagesForm).iterator();
			if (ite.hasNext()) {
				imagesForm = (ImagesForm) ite.next();
				imagesForm.setImage(image);
				modelImages.update(imagesForm);
			} else {
                imagesForm.setImage(image);
                rowid = modelImages.insert(imagesForm);
                imagesForm = (ImagesForm) modelImages.selectByRowId(rowid).get(0);
                mForm.setImage_id(imagesForm.getImage_id());
			}
			modelFamily.update(this.mForm);
			msg = "更新しました";

			showToast(msg);
        }
		CommonLogUtil.method_end();
	}
	
	/**
	 * データを削除する
	 */
	private void deleteData(){
		CommonLogUtil.method_start();
		FamilyModel familyModel = new FamilyModel(mContext);
        WcRecordModel wcRecordModel = new WcRecordModel(mContext);
        //対象者データの削除
		familyModel.delete(this.mForm);
        //対象者の記録を削除
        wcRecordModel.deleteByFamilyId(mForm.getFamily_id());
        //画面に保持するformを初期化
		mForm = new FamilyForm();
		showToast("削除しました");
		dismiss();
		CommonLogUtil.method_end();
	}

	/**
	 * 入力項目のValidate
	 * @return	チェック結果
	 */
	private boolean validate(){
		boolean ret = true;
		if(aq.id(R.id.etDialogFamilyName).getText().length() == 0){
			Toast.makeText(mContext, "名前を入力してください", Toast.LENGTH_SHORT).show();
			ret = false;
		}
		return ret;
	}

	/**
	 * 登録ボタン押下時イベント
	 */
	private OnClickListener lsnrBtnSubmit = new OnClickListener() {
		@Override
		public void onClick(View v) {
			CommonLogUtil.method_start();
			if(validate()) {
				createData();
				dismiss();
			}
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
			setupDisplay();
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
	 * 誕生日TextViewクリック時のイベント
	 */
    OnClickListener lsnrEtBirthDate = new OnClickListener() {
        @Override
        public void onClick(View v) {
			Calendar cal = CalendarUtil.getToday();
			new DatePickerDialog(mContext, lsnrDpDateSet
					, CalendarUtil.getYear(cal)
					, CalendarUtil.getMonth(cal) -1
					, CalendarUtil.getDate(cal))
					.show();
        }
    };

	/**
	 * DatePickerで設定をクリックした場合のイベント
	 */
    DatePickerDialog.OnDateSetListener lsnrDpDateSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            aq.id(R.id.tvDialogBirthDay).text(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
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
	 * @param msg   メッセージ
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
		ActivityForm actForm = new ActivityForm();
		actForm.setFamily_id(mForm.getFamily_id());
		if(getActivity() != null) {
			((BaseActivity)getActivity()).callback(actForm);
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
		CommonLogUtil.method_end();
	}
}
