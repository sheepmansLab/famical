/**
 * 
 */
package jp.sheepman.famical.fragment;

import java.util.Iterator;

import jp.sheepman.common.entity.BaseEntity;
import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.fragment.BaseDialogFragment;
import jp.sheepman.common.fragment.BaseFragment;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.famical.R;
import jp.sheepman.famical.entity.FamilyEntity;
import jp.sheepman.famical.entity.WcRecordEntity;
import jp.sheepman.famical.form.FamilyForm;
import jp.sheepman.famical.form.WcRecordForm;
import jp.sheepman.famical.model.FamilyDeleteModel;
import jp.sheepman.famical.model.FamilyInsertModel;
import jp.sheepman.famical.model.FamilySelectModel;
import jp.sheepman.famical.model.FamilyUpdateModel;
import jp.sheepman.famical.model.WcRecordDeleteModel;
import jp.sheepman.famical.model.WcRecordInsertModel;
import jp.sheepman.famical.model.WcRecordSelectModel;
import jp.sheepman.famical.model.WcRecordUpdateModel;
import jp.sheepman.famical.view.CustomNumberPicker;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidquery.AQuery;


/**
 * @author sheepman
 *
 */
public class FamilyInputDialogFragment extends BaseDialogFragment {
	private AQuery aq;
	private Context mContext;
	private LayoutInflater inflator;
	
	private FamilyForm form;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.aq = new AQuery(getActivity());
		this.mContext = getActivity();
		this.inflator = getActivity().getLayoutInflater();
		this.form = new FamilyForm();
		
		//ディスプレイ情報を取得
		final float density = getResources().getDisplayMetrics().density;  
		//ダイアログの横幅：280dpi
		final int dialogWidth = (int) (280 * density);

		//主キーを保持
		int family_id = 0;
		
		//引数を取得
		Bundle args = getArguments();
		if(args != null){
			family_id = args.getInt("family_id");
		}
		//Viewのレイアウトを取得
		View view = this.inflator.inflate(R.layout.fragment_family_input, null);
		
		//rootをDialog内のViewにセット
		aq.recycle(view);
		//各項目に初期値をセット
		aq.id(R.id.tvDialogFamily_id).text(String.valueOf(family_id));
		aq.id(R.id.btnDialogInput).clicked(lsnrBtnSubmit);
		aq.id(R.id.btnDialogClear).clicked(lsnrClickClear);
		aq.id(R.id.btnDialogDelete).clicked(lsnrClickDelete);
		aq.id(R.id.btnClose).clicked(lsnrBtnClose);
		
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
		
		return dialog;
	}
	
	/**
	 * データを取得して設定を戻す
	 */
	private void setData(){
		FamilySelectModel model = new FamilySelectModel(mContext);
		this.form.setFamily_id(Integer.valueOf(aq.id(R.id.tvDialogFamily_id).getText().toString()));
		Iterator<BaseForm> ite = model.selectById(this.form).iterator();
		if(ite.hasNext()){
			FamilyForm form = (FamilyForm)ite.next();
			
			aq.id(R.id.etDialogFamilyName).text(form.getFamily_name());
			aq.id(R.id.etDialogBirthDay).text(CalendarUtil.cal2str(form.getBirth_date()));
			
			//削除ボタンを活性化
			aq.id(R.id.btnDialogDelete).visibility(View.VISIBLE);
		} else {
				//削除ボタンを非活性化
			aq.id(R.id.btnDialogDelete).visibility(View.GONE);
		}
	}
	
	/**
	 * データをInsertする
	 */
	private void inputData(){
		FamilySelectModel checkModel = new FamilySelectModel(mContext);
		//最新の値をセット
		//Toastのメッセージ
		String msg = "";
		//件数が0以上ならUpdate、0ならInsert
		if(checkModel.selectById(this.form).size() == 0){
			FamilyInsertModel execModel = new FamilyInsertModel(mContext);
			execModel.insert(this.form);
			msg = "登録しました";
		} else {
			FamilyUpdateModel execModel = new FamilyUpdateModel(mContext);
			execModel.update(this.form);
			msg = "更新しました";
		}
		showToast(msg);
	}
	
	/**
	 * データを削除する
	 */
	private void deleteData(){
		FamilyDeleteModel model = new FamilyDeleteModel(mContext);
		model.delete(this.form);
		showToast("削除しました");
	}
	
	/**
	 * 登録ボタン押下時イベント
	 */
	private OnClickListener lsnrBtnSubmit = new OnClickListener() {
		@Override
		public void onClick(View v) {
			inputData();
			dismiss();
		}
	};
	
	/**
	 * 削除ボタン押下時イベント
	 */
	private OnClickListener lsnrClickDelete = new OnClickListener() {
		@Override
		public void onClick(View v) {
			deleteData();
			dismiss();
		}
	};
	
	/**
	 * 画面の閉じるボタン押下時のイベントリスナー
	 */
	private OnClickListener lsnrBtnClose = new OnClickListener() {
		@Override
		public void onClick(View v) {
			dismiss();
		}
	};
	
	/**
	 * クリアボタン押下時のイベント
	 */
	private OnClickListener lsnrClickClear = new OnClickListener() {
		@Override
		public void onClick(View v) {
			setData();
		}
	};
	
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
		if(getTargetFragment() != null && getTargetFragment() instanceof BaseFragment){
			BaseFragment target = (BaseFragment)getTargetFragment();
			target.callback();
		}
		super.onDismiss(dialog);
	}
	
	@Override
	public void callback() {
	}

}
