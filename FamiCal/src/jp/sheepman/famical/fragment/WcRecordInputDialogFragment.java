/**
 * 
 */
package jp.sheepman.famical.fragment;

import jp.sheepman.common.fragment.BaseDialogFragment;
import jp.sheepman.common.fragment.BaseFragment;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.famical.R;
import jp.sheepman.famical.form.WcRecordForm;
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

import com.androidquery.AQuery;


/**
 * @author sheepman
 *
 */
public class WcRecordInputDialogFragment extends BaseDialogFragment {
	private AQuery aq;
	private Context mContext;
	private LayoutInflater inflator;
	
	//TEST
	String wc_record_date = "";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.aq = new AQuery(getActivity());
		this.mContext = getActivity();
		this.inflator = getActivity().getLayoutInflater();

		//ディスプレイ情報を取得
		final float density = getResources().getDisplayMetrics().density;  
		//ダイアログの横幅：280dpi
		final int dialogWidth = (int) (280 * density);  
		//引数を取得
		Bundle args = getArguments();
		if(args != null){
			wc_record_date = args.getString("wc_record_date");
		}
		//Viewのレイアウトを取得
		View view = this.inflator.inflate(R.layout.fragment_input_dialog, null);
		
		//rootをDialog内のViewにセット
		aq.recycle(view);
		//各項目に初期値をセット
		aq.id(R.id.tvDialogDate).text(wc_record_date).clicked(lsnrBtnClose);
		aq.id(R.id.btnDialogInput).clicked(lsnrBtnSubmit);
		aq.id(R.id.btnClose).clicked(lsnrBtnClose);
		((CustomNumberPicker)aq.id(R.id.cnpDialogPeCount).getView()).setValue(0);
		((CustomNumberPicker)aq.id(R.id.cnpDialogPoCount).getView()).setValue(0);
		
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
	 * データをInsertする
	 */
	private void inputData(){
		WcRecordForm form = new WcRecordForm();
		WcRecordSelectModel checkModel = new WcRecordSelectModel(mContext);
		
		//TODO family_id実装時に差し替え
		form.setFamily_id(1);
		form.setWc_record_date(CalendarUtil.str2cal(aq.id(R.id.tvDialogDate).getText().toString()));
		form.setPe_count(((CustomNumberPicker)aq.id(R.id.cnpDialogPeCount).getView()).getValue());
		form.setPo_count(((CustomNumberPicker)aq.id(R.id.cnpDialogPoCount).getView()).getValue());
		
		//件数が0以上ならUpdate、0ならInsert
		if(checkModel.selectCountByPrimary(form) == 0){
			WcRecordInsertModel execModel = new WcRecordInsertModel(mContext);
			execModel.execute(form);
		} else {
			WcRecordUpdateModel execModel = new WcRecordUpdateModel(mContext);
			execModel.execute(form);
		}
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
	 * 画面の閉じるボタン押下時のイベントリスナー
	 */
	private OnClickListener lsnrBtnClose = new OnClickListener() {
		@Override
		public void onClick(View v) {
			dismiss();
		}
	};
	
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
