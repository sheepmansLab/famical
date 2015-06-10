/**
 * 
 */
package jp.sheepman.famical.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.AQuery;

import java.util.Calendar;
import java.util.Iterator;

import jp.sheepman.common.activity.BaseActivity;
import jp.sheepman.common.entity.BaseEntity;
import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.fragment.BaseFragment;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.famical.R;
import jp.sheepman.famical.entity.WcRecordEntity;
import jp.sheepman.famical.form.MainActivityForm;
import jp.sheepman.famical.form.WcRecordForm;
import jp.sheepman.famical.model.WcRecordModel;
import jp.sheepman.famical.util.CommonConst;
import jp.sheepman.famical.util.CommonLogUtil;
import jp.sheepman.famical.view.CustomNumberPicker;


/**
 * @author sheepman
 *
 */
public class WcRecordInputFragment extends BaseFragment {
	private AQuery aq;
	private Context mContext;
	
	private WcRecordForm mForm;
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
        CommonLogUtil.method_start();
		super.onSaveInstanceState(outState);
		if(outState != null){
			if(mForm != null){
				outState.putString(CommonConst.BUNDLE_KEY_WC_RECORD_DATE, CalendarUtil.cal2str(mForm.getWc_record_date()));
				outState.putInt(CommonConst.BUNDLE_KEY_FAMILY_ID, mForm.getFamily_id());
			}
		}
        CommonLogUtil.method_end();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        CommonLogUtil.method_start();
		this.mContext = getActivity();
		this.mForm = new WcRecordForm();
		//引数を取得
		Bundle args = getArguments();
		if(args != null){
			this.mForm.setFamily_id(args.getInt("family_id"));
			this.mForm.setWc_record_date(CalendarUtil.str2cal(args.getString("wc_record_date")));
		}
        CommonLogUtil.method_end();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CommonLogUtil.method_start();
		this.aq = new AQuery(getActivity());
		
		//Viewのレイアウトを取得
		View view = inflater.inflate(R.layout.fragment_wcrec_input, null);
		
		if(savedInstanceState != null){
			//Bundleの保持情報を取得
			mForm.setFamily_id(savedInstanceState.getInt(CommonConst.BUNDLE_KEY_FAMILY_ID));
			mForm.setWc_record_date(CalendarUtil.str2cal(savedInstanceState.getString(CommonConst.BUNDLE_KEY_WC_RECORD_DATE)));
		}

		//rootをDialog内のViewにセット
		aq.recycle(view);
		//各項目に初期値をセット
		aq.id(R.id.btnWcRecInputInput).clicked(lsnrClickSubmit);
		aq.id(R.id.btnWcRecInputClear).clicked(lsnrClickClear);
		aq.id(R.id.btnWcRecInputDelete).clicked(lsnrClickDelete);
		aq.id(R.id.btnWcRecInputPrevDay).clicked(lsnrClickAddDay);
		aq.id(R.id.btnWcRecInputNextDay).clicked(lsnrClickAddDay);
		
		//初期値をセット
		setContents();

        CommonLogUtil.method_end();
		return view;
	}

	/**
	 * 日付を変更する
	 * @param family_id
	 * @param wc_record_date
	 */
	public void changeDisplay(int family_id, Calendar wc_record_date){
        CommonLogUtil.method_start();
		//formのPK情報を変更する
		this.mForm.setFamily_id(family_id);
		this.mForm.setWc_record_date(wc_record_date);
		//画面情報を変更
		setContents();
        //Activityに反映
        if(getActivity() instanceof BaseActivity){
            MainActivityForm mainActivityForm = new MainActivityForm();
            mainActivityForm.setFamily_id(mForm.getFamily_id());
            mainActivityForm.setWc_record_date((Calendar) mForm.getWc_record_date().clone());
            mainActivityForm.setReloadFlg(false);
            ((BaseActivity) getActivity()).callback(mainActivityForm);
        }
        CommonLogUtil.method_end();
	}
	
	/**
	 * データを取得して設定を戻す
	 */
	private void setContents(){
        CommonLogUtil.method_start();
		//SelectModel作成
		WcRecordModel model = new WcRecordModel(mContext);
		//当日のデータを検索し存在していたら画面上とformに反映
		Iterator<BaseEntity> ite = model.selectByPrimary(this.mForm).iterator();
		if(ite.hasNext()){
			WcRecordEntity entity = (WcRecordEntity)ite.next();
			((CustomNumberPicker)aq.id(R.id.cnpWcRecInputPeCount).getView()).setValue(entity.getPe_count());
			((CustomNumberPicker)aq.id(R.id.cnpWcRecInputPoCount).getView()).setValue(entity.getPo_count());
			//取得したデータをformにセット
			this.mForm.setPe_count(entity.getPe_count());
			this.mForm.setPo_count(entity.getPo_count());
			this.mForm.setComment(entity.getComment());
			//削除ボタンを活性化
			aq.id(R.id.btnWcRecInputDelete).visibility(View.VISIBLE);
		//データが無い場合は初期値をセット
		} else {
			((CustomNumberPicker)aq.id(R.id.cnpWcRecInputPeCount).getView()).setValue(0);
			((CustomNumberPicker)aq.id(R.id.cnpWcRecInputPoCount).getView()).setValue(0);
			//formの初期値をセット
			this.mForm.setPe_count(0);
			this.mForm.setPo_count(0);
			this.mForm.setComment("");
			//削除ボタンを活性化
			aq.id(R.id.btnWcRecInputDelete).visibility(View.INVISIBLE);
		}
		//ラベルに日付を表示
		int year = CalendarUtil.getYear(mForm.getWc_record_date());
		int month = CalendarUtil.getMonth(mForm.getWc_record_date());
		int day = CalendarUtil.getDate(mForm.getWc_record_date());
        aq.id(R.id.tvWcRecInputDateYear).text(String.format("%1$4d",year));
        aq.id(R.id.tvWcRecInputDateMonth).text(String.format("%1$02d", month));
        aq.id(R.id.tvWcRecInputDateDay).text(String.format("%1$02d", day));
        CommonLogUtil.method_end();
	}
	
	/**
	 * データをInsertする
	 */
	private void inputData(){
        CommonLogUtil.method_start();
		WcRecordModel model = new WcRecordModel(mContext);
		//最新の値をセット
		this.mForm.setPe_count(((CustomNumberPicker) aq.id(R.id.cnpWcRecInputPeCount).getView()).getValue());
		this.mForm.setPo_count(((CustomNumberPicker) aq.id(R.id.cnpWcRecInputPoCount).getView()).getValue());
		//Toastのメッセージ
		String msg = "";
		//件数が0以上ならUpdate、0ならInsert
		if(model.selectByPrimary(this.mForm).size() == 0){
			model.insert(this.mForm);
			msg = "登録しました";
		} else {
			model.update(this.mForm);
			msg = "更新しました";
		}
		//メッセージの表示
		showToast(msg);
        CommonLogUtil.method_end();
	}
	
	/**
	 * カレンダーに反映する
	 */
	private void refrectCalendar(boolean reload){
        CommonLogUtil.method_start();
        //カレンダーに反映
        if(getTargetFragment() instanceof WcRecordCalendarFragment){
            ((WcRecordCalendarFragment)getTargetFragment())
                    .changeDisplay(mForm.getFamily_id(), mForm.getWc_record_date(), reload);
        }
        CommonLogUtil.method_end();
	}
	
	/**
	 * データを削除する
	 */
	private void deleteData(){
        CommonLogUtil.method_start();
		WcRecordModel model = new WcRecordModel(mContext);
		model.delete(this.mForm);
		showToast("削除しました");
        CommonLogUtil.method_end();
	}
	
	/////////////////////////// Listener ///////////////////////// 
	
	private OnClickListener lsnrClickAddDay = new OnClickListener() {
		@Override
		public void onClick(View v) {
            CommonLogUtil.method_start();
			if (v.getId() == R.id.btnWcRecInputPrevDay) {
				mForm.getWc_record_date().add(Calendar.DATE, -1);
			} else if(v.getId() == R.id.btnWcRecInputNextDay) {
				mForm.getWc_record_date().add(Calendar.DATE, 1);
			}
			setContents();
			refrectCalendar(false);
            CommonLogUtil.method_end();
		}
	};
	
	/**
	 * 登録ボタン押下時イベント
	 */
	private OnClickListener lsnrClickSubmit = new OnClickListener() {
		@Override
		public void onClick(View v) {
            CommonLogUtil.method_start();
			inputData();
			//カレンダーに反映する
			refrectCalendar(true);
			setContents();
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
			//カレンダーに反映する
			refrectCalendar(true);
			setContents();
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
			setContents();
            CommonLogUtil.method_end();
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
