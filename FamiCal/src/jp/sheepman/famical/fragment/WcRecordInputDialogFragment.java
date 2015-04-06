/**
 * 
 */
package jp.sheepman.famical.fragment;

import java.util.Iterator;

import jp.sheepman.common.entity.BaseEntity;
import jp.sheepman.common.fragment.BaseDialogFragment;
import jp.sheepman.common.fragment.BaseFragment;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.famical.R;
import jp.sheepman.famical.entity.WcRecordEntity;
import jp.sheepman.famical.form.WcRecordForm;
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
public class WcRecordInputDialogFragment extends BaseDialogFragment {
	private AQuery aq;
	private Context mContext;
	private LayoutInflater inflator;
	
	private WcRecordForm form;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.aq = new AQuery(getActivity());
		this.mContext = getActivity();
		this.inflator = getActivity().getLayoutInflater();
		this.form = new WcRecordForm();
		
		//ディスプレイ情報を取得
		final float density = getResources().getDisplayMetrics().density;  
		//ダイアログの横幅：280dpi
		final int dialogWidth = (int) (280 * density);

		//主キーを保持
		int family_id = 0;
		String wc_record_date = "";
		
		//引数を取得
		Bundle args = getArguments();
		if(args != null){
			family_id = args.getInt("family_id");
			wc_record_date = args.getString("wc_record_date");
		}
		//Viewのレイアウトを取得
		View view = this.inflator.inflate(R.layout.fragment_input_dialog, null);
		
		//rootをDialog内のViewにセット
		aq.recycle(view);
		//各項目に初期値をセット
		aq.id(R.id.tvDialogDate).text(wc_record_date);
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
		WcRecordSelectModel model = new WcRecordSelectModel(mContext);
		this.form.setFamily_id(Integer.valueOf(aq.id(R.id.tvDialogFamily_id).getText().toString()));
		this.form.setWc_record_date(CalendarUtil.str2cal(aq.id(R.id.tvDialogDate).getText().toString()));
		Iterator<BaseEntity> ite = model.selectByPrimary(this.form).iterator();
		if(ite.hasNext()){
			WcRecordEntity entity = (WcRecordEntity)ite.next();
			((CustomNumberPicker)aq.id(R.id.cnpDialogPeCount).getView()).setValue(entity.getPe_count());
			((CustomNumberPicker)aq.id(R.id.cnpDialogPoCount).getView()).setValue(entity.getPo_count());
			//取得したデータをformにセット
			this.form.setPe_count(entity.getPe_count());
			this.form.setPo_count(entity.getPo_count());
			this.form.setComment(entity.getComment());
			//削除ボタンを活性化
			aq.id(R.id.btnDialogDelete).visibility(View.VISIBLE);
		} else {
			((CustomNumberPicker)aq.id(R.id.cnpDialogPeCount).getView()).setValue(0);
			((CustomNumberPicker)aq.id(R.id.cnpDialogPoCount).getView()).setValue(0);
			//formの初期値をセット
			this.form.setPe_count(0);
			this.form.setPo_count(0);
			this.form.setComment("");
			//削除ボタンを活性化
			aq.id(R.id.btnDialogDelete).visibility(View.GONE);
		}
	}
	
	/**
	 * データをInsertする
	 */
	private void inputData(){
		WcRecordSelectModel checkModel = new WcRecordSelectModel(mContext);
		//最新の値をセット
		this.form.setPe_count(((CustomNumberPicker)aq.id(R.id.cnpDialogPeCount).getView()).getValue());
		this.form.setPo_count(((CustomNumberPicker)aq.id(R.id.cnpDialogPoCount).getView()).getValue());
		//Toastのメッセージ
		String msg = "";
		//件数が0以上ならUpdate、0ならInsert
		if(checkModel.selectByPrimary(this.form).size() == 0){
			WcRecordInsertModel execModel = new WcRecordInsertModel(mContext);
			execModel.insert(this.form);
			msg = "登録しました";
		} else {
			WcRecordUpdateModel execModel = new WcRecordUpdateModel(mContext);
			execModel.update(this.form);
			msg = "更新しました";
		}
		showToast(msg);
	}
	
	/**
	 * データを削除する
	 */
	private void deleteData(){
		WcRecordDeleteModel model = new WcRecordDeleteModel(mContext);
		model.execute(this.form);
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
