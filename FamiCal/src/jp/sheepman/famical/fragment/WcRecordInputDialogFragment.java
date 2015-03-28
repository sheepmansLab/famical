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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


/**
 * @author sheepman
 *
 */
public class WcRecordInputDialogFragment extends BaseDialogFragment {
	private LayoutInflater inflator;
	
	//TEST
	String wc_record_date = "";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.inflator = getActivity().getLayoutInflater();
		View view = this.inflator.inflate(R.layout.fragment_input_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
														.setView(view)
														.setPositiveButton("閉じる", lsnrBtnClose);
		Bundle args = getArguments();
		if(args != null){
			wc_record_date = args.getString("wc_record_date");
		}
		((TextView)view.findViewById(R.id.tvDialogDate)).setText(wc_record_date);
		
		return builder.create();
	}
	
	/**
	 * データをInsertする
	 */
	private void insertData(){
		WcRecordInsertModel model = new WcRecordInsertModel(getActivity());
		WcRecordForm form = new WcRecordForm();
		form.setFamily_id(1);
		form.setWc_record_date(CalendarUtil.str2cal(wc_record_date));
		form.setPe_count(1);
		form.setPo_count(2);
		model.execute(form);
	}
	
	/**
	 * 登録ボタン押下時イベント
	 */
	private OnClickListener lsnrBtnSubmit = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			insertData();
		}
	};
	
	/**
	 * 画面の閉じるボタン押下時のイベントリスナー
	 */
	private OnClickListener lsnrBtnClose = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			insertData();
			onDismiss(getDialog());
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
