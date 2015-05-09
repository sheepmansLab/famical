package jp.sheepman.famical;

import java.util.Calendar;

import jp.sheepman.common.activity.BaseActivity;
import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.famical.form.MainActivityForm;
import jp.sheepman.famical.fragment.FamilySelectFragment;
import jp.sheepman.famical.fragment.WcRecordCalendarFragment;
import jp.sheepman.famical.fragment.WcRecordInputFragment;
import jp.sheepman.famical.util.CommonConst;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import com.androidquery.AQuery;

public class MainActivity extends BaseActivity {
	private AQuery aq;
	private int family_id;
	private Calendar wc_record_date;
	
	//カレンダーフラグメント
	WcRecordCalendarFragment fragment_cal; 
	//入力欄フラグメント
	WcRecordInputFragment fragment_inp;
	//家族選択フラグメント
	FamilySelectFragment fragment_select;
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(outState != null){
			outState.putInt(CommonConst.BUNDLE_KEY_FAMILY_ID, family_id);
			outState.putString(CommonConst.BUNDLE_KEY_WC_RECORD_DATE, CalendarUtil.cal2str(wc_record_date));
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		aq = new AQuery(this);
		
		setContentView(R.layout.activity_main);
		
		//TODO 家族ID処理待ち
		this.family_id = 1;
		//当日をセット
		this.wc_record_date = CalendarUtil.getToday();
		
		//Bundleに保持していた場合再取得
		if(savedInstanceState != null){
			this.family_id = savedInstanceState.getInt(CommonConst.BUNDLE_KEY_FAMILY_ID);
			this.wc_record_date = CalendarUtil.str2cal(savedInstanceState.getString(CommonConst.BUNDLE_KEY_WC_RECORD_DATE));
		} else if(getIntent() != null){
			this.family_id = getIntent().getIntExtra(CommonConst.BUNDLE_KEY_FAMILY_ID, 0);
			Calendar tmpDate = CalendarUtil.str2cal(getIntent().getStringExtra(CommonConst.BUNDLE_KEY_WC_RECORD_DATE));
			if(tmpDate != null){
				this.wc_record_date = tmpDate;
			}
		}
		
		//引数をセット
		Bundle args = new Bundle();
		args.putInt(CommonConst.BUNDLE_KEY_FAMILY_ID, family_id);
		args.putString(CommonConst.BUNDLE_KEY_WC_RECORD_DATE, CalendarUtil.cal2str(wc_record_date));

		//Fragment処理
		FragmentTransaction tran = getFragmentManager().beginTransaction();
		//カレンダーフラグメント
		fragment_cal = new WcRecordCalendarFragment(); 
		//入力欄フラグメント
		fragment_inp = new WcRecordInputFragment();
		//家族選択フラグメント
		fragment_select = new FamilySelectFragment();
		
		//カレンダがセットされていなければセット
		if(getFragmentManager().findFragmentByTag(CommonConst.FRAGMENT_TAG_CALENDAR) == null){
			fragment_cal.setArguments(args);
			//targetフラグメントをセット
			fragment_cal.setTargetFragment(fragment_inp, 0);
			tran.replace(R.id.frmCalendarFragment
					, fragment_cal
					, CommonConst.FRAGMENT_TAG_CALENDAR);
		}
		//入力欄がセットされていなければセット
		if(getFragmentManager().findFragmentByTag(CommonConst.FRAGMENT_TAG_CALENDAR) == null){
			fragment_inp.setArguments(args);
			//targetフラグメントをセット
			fragment_inp.setTargetFragment(fragment_cal, 0);
			tran.replace(R.id.frmInputFragment
					, fragment_inp
					, CommonConst.FRAGMENT_TAG_WCREC_INPUT);
		}
		//DrawerLayoutがなければ追加する
		if(getFragmentManager().findFragmentByTag(CommonConst.FRAGMENT_TAG_FAMILY_SELECT) == null){
			fragment_select.setArguments(args);
			tran.replace(R.id.frmDrawerLayout
					, fragment_select
					, CommonConst.FRAGMENT_TAG_FAMILY_SELECT);
		}
		tran.commit();
	}

	@Override
	public void callback() {
		if(fragment_cal != null && fragment_inp != null){
			fragment_cal.changeDisplay(family_id, wc_record_date, true);
			fragment_inp.changeDisplay(family_id, wc_record_date);
			((DrawerLayout)aq.id(R.id.dlMainFamilySelect).getView()).closeDrawers();
		}
	}

	@Override
	public void callback(BaseForm arg0) {
		if(arg0 instanceof MainActivityForm){
			this.family_id = ((MainActivityForm)arg0).getFamily_id();
			if(((MainActivityForm)arg0).getWc_record_date() != null){
				this.wc_record_date = ((MainActivityForm)arg0).getWc_record_date();
			}
		}
		this.callback();
	}
}
