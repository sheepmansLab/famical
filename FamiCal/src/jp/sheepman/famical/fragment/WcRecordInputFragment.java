/**
 * 
 */
package jp.sheepman.famical.fragment;

import java.util.Calendar;
import java.util.Iterator;

import jp.sheepman.common.entity.BaseEntity;
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
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.AQuery;


/**
 * @author sheepman
 *
 */
public class WcRecordInputFragment extends BaseFragment {
	private AQuery aq;
	private Context mContext;
	
	private WcRecordForm form;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.aq = new AQuery(getActivity());
		this.mContext = getActivity();
		this.form = new WcRecordForm();
		
		//Viewのレイアウトを取得
		View view = inflater.inflate(R.layout.fragment_wcrec_input, null);

		//引数を取得
		Bundle args = getArguments();
		if(args != null){
			this.form.setFamily_id(args.getInt("family_id"));
			this.form.setWc_record_date(CalendarUtil.str2cal(args.getString("wc_record_date")));
		}
		
		//rootをDialog内のViewにセット
		aq.recycle(view);
		//各項目に初期値をセット
		aq.id(R.id.btnDialogInput).clicked(lsnrClickSubmit);
		aq.id(R.id.btnDialogClear).clicked(lsnrClickClear);
		aq.id(R.id.btnDialogDelete).clicked(lsnrClickDelete);
		aq.id(R.id.btnPrevDay).clicked(lsnrClickAddDay);
		aq.id(R.id.btnNextDay).clicked(lsnrClickAddDay);
		
		//初期値をセット
		setContents();
		
		return view;
	}

	/**
	 * 日付を変更する
	 * @param family_id
	 * @param wc_record_date
	 */
	public void changeDate(int family_id, Calendar wc_record_date){
		//formのPK情報を変更する
		this.form.setFamily_id(family_id);
		this.form.setWc_record_date(wc_record_date);
		//画面情報を変更
		setContents();
	}
	
	/**
	 * データを取得して設定を戻す
	 */
	private void setContents(){
		//SelectModel作成
		WcRecordSelectModel model = new WcRecordSelectModel(mContext);
		//当日のデータを検索し存在していたら画面上とformに反映
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
		//データが無い場合は初期値をセット
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
		//ラベルに日付を表示
		aq.id(R.id.tvDialogDate).text(CalendarUtil.cal2str(form.getWc_record_date()));
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
		//メッセージの表示
		showToast(msg);
	}
	
	/**
	 * カレンダーに反映する
	 */
	private void refrectCalendar(boolean reload){
		//カレンダーに反映
		if(getTargetFragment() instanceof WcRecordCalendarFragment){
			((WcRecordCalendarFragment)getTargetFragment()).changeDate(
					this.form.getFamily_id()
					, (Calendar)(this.form.getWc_record_date()).clone()
					, reload);
		}
	}
	
	/**
	 * データを削除する
	 */
	private void deleteData(){
		WcRecordDeleteModel model = new WcRecordDeleteModel(mContext);
		model.execute(this.form);
		showToast("削除しました");
	}
	
	/////////////////////////// Listener ///////////////////////// 
	
	private OnClickListener lsnrClickAddDay = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btnPrevDay) {
				form.getWc_record_date().add(Calendar.DATE, -1);
			} else if(v.getId() == R.id.btnNextDay) {
				form.getWc_record_date().add(Calendar.DATE, 1);
			}
			setContents();
			refrectCalendar(false);
		}
	};
	
	/**
	 * 登録ボタン押下時イベント
	 */
	private OnClickListener lsnrClickSubmit = new OnClickListener() {
		@Override
		public void onClick(View v) {
			inputData();
			//カレンダーに反映する
			refrectCalendar(true);
			setContents();
		}
	};
	
	/**
	 * 削除ボタン押下時イベント
	 */
	private OnClickListener lsnrClickDelete = new OnClickListener() {
		@Override
		public void onClick(View v) {
			deleteData();
			//カレンダーに反映する
			refrectCalendar(true);
			setContents();
		}
	};
	/**
	 * クリアボタン押下時のイベント
	 */
	private OnClickListener lsnrClickClear = new OnClickListener() {
		@Override
		public void onClick(View v) {
			setContents();
		}
	};
	
	/**
	 * Toast表示
	 * @param msg
	 */
	private void showToast(String msg){
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void callback() {
	}

}
