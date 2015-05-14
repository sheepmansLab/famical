package jp.sheepman.famical;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import jp.sheepman.common.activity.BaseActivity;
import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.famical.form.ActivityForm;
import jp.sheepman.famical.form.FamilyForm;
import jp.sheepman.famical.fragment.FamilySelectFragment;
import jp.sheepman.famical.fragment.WcRecordCalendarFragment;
import jp.sheepman.famical.fragment.WcRecordInputFragment;
import jp.sheepman.famical.model.FamilyModel;
import jp.sheepman.famical.util.CommonConst;
import jp.sheepman.famical.util.CommonFileUtil;
import jp.sheepman.famical.util.CommonLogUtil;
import android.app.FragmentTransaction;
import android.content.Intent;
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
		CommonLogUtil.method_start();
		super.onSaveInstanceState(outState);
		if(outState != null){
			outState.putInt(CommonConst.BUNDLE_KEY_FAMILY_ID, family_id);
			outState.putString(CommonConst.BUNDLE_KEY_WC_RECORD_DATE, CalendarUtil.cal2str(wc_record_date));
		}
		CommonLogUtil.method_end();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		CommonLogUtil.method_start();
		super.onCreate(savedInstanceState);
		this.aq = new AQuery(this);
		setContentView(R.layout.activity_main);
		
		//カレンダーフラグメント
		fragment_cal = new WcRecordCalendarFragment(); 
		//入力欄フラグメント
		fragment_inp = new WcRecordInputFragment();
		//家族選択フラグメント
		fragment_select = new FamilySelectFragment();

		//キャッシュからfamily_idを取得
		this.family_id = CommonFileUtil.readChacheFamilyId(getCacheDir());
		//当日をセット
		this.wc_record_date = CalendarUtil.getToday();
		
		//Bundleに保持していた場合再取得
		if(savedInstanceState != null){
			this.family_id = savedInstanceState.getInt(CommonConst.BUNDLE_KEY_FAMILY_ID);
			this.wc_record_date = CalendarUtil.str2cal(savedInstanceState.getString(CommonConst.BUNDLE_KEY_WC_RECORD_DATE));
		}
		
		//家族データ取得処理
		List<Integer> family_list = getFamilyIdList();
		//データが0件の場合入力画面を表示する
		if(family_list.size() == 0 || family_list.indexOf(Integer.valueOf(this.family_id)) < 0) {
			Intent intent = new Intent(this, FamilyActivity.class);
			startActivityForResult(intent, CommonConst.REQUEST_CODE_FAMILY_ACTIVITY);
		}
		
		//Fragment処理
		FragmentTransaction tran = getFragmentManager().beginTransaction();
		
		//引数を作成
		Bundle args = new Bundle();
		//family_idをキャッシュに書き込み
		CommonFileUtil.writeChacheFamilyId(getCacheDir(), this.family_id);
		
		args.putInt(CommonConst.BUNDLE_KEY_FAMILY_ID, this.family_id);
		args.putString(CommonConst.BUNDLE_KEY_WC_RECORD_DATE, CalendarUtil.cal2str(wc_record_date));
		
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
		CommonLogUtil.method_end();
	}
	
	/**
	 * 条件なしのFamilyIDのリストを返却
	 * @return family_idのリスト
	 */
	private List<Integer> getFamilyIdList(){
		CommonLogUtil.method_start();
		//家族データ取得処理
		FamilyModel selectModel = new FamilyModel(this);
		List<Integer> list = new ArrayList<Integer>();
		Iterator<BaseForm> ite = selectModel.selectAll().iterator();
		while(ite.hasNext()){
			list.add(((FamilyForm)ite.next()).getFamily_id());
		}
		CommonLogUtil.method_end();
		return list;
	}

	/**
	 * startActivityForResultでの結果を受け取る
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		CommonLogUtil.method_end();
		//リクエストコード別に処理をする
		switch (requestCode) {
		//画像選択の場合
		case CommonConst.REQUEST_CODE_FAMILY_ACTIVITY:
			//正常終了の場合
			if(resultCode == RESULT_OK){
				//IDを受け取る
				this.family_id = data.getIntExtra(CommonConst.BUNDLE_KEY_FAMILY_ID, family_id);
				reload();
			}
			break;
		default:
			break;
		}
		CommonLogUtil.method_end();
	}
	
	/**
	 * 画面上の表示をリロードする
	 */
	private void reload(){
		CommonLogUtil.method_start();
		if(fragment_cal != null && fragment_inp != null){
			fragment_cal.changeDisplay(family_id, wc_record_date, true);
			fragment_inp.changeDisplay(family_id, wc_record_date);
			fragment_select.changeDisplay(family_id);
			((DrawerLayout)aq.id(R.id.dlMainFamilySelect).getView()).closeDrawers();
			//family_idを書き込む
			CommonFileUtil.writeChacheFamilyId(getCacheDir(), family_id);
		}
		CommonLogUtil.method_end();
	}
	
	/**
	 * コールバック 画面を保持している情報でリロードする
	 */
	@Override
	public void callback() {
		CommonLogUtil.method_start();
		reload();
		CommonLogUtil.method_end();
	}

	/**
	 * コールバック 受け取ったformの情報で画面をリロードする
	 */
	@Override
	public void callback(BaseForm arg0) {
		CommonLogUtil.method_start();
		if(arg0 instanceof ActivityForm){
			this.family_id = ((ActivityForm)arg0).getFamily_id();
			if(((ActivityForm)arg0).getWc_record_date() != null){
				this.wc_record_date = ((ActivityForm)arg0).getWc_record_date();
			}
		}
		CommonLogUtil.method_end();
		this.callback();
	}
}
