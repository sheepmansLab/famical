package jp.sheepman.famical;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import jp.sheepman.common.activity.BaseActivity;
import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.famical.form.FamilyForm;
import jp.sheepman.famical.form.MainActivityForm;
import jp.sheepman.famical.fragment.FamilyInputDialogFragment;
import jp.sheepman.famical.fragment.FamilySelectFragment;
import jp.sheepman.famical.fragment.WcRecordCalendarFragment;
import jp.sheepman.famical.fragment.WcRecordInputFragment;
import jp.sheepman.famical.model.FamilySelectModel;
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
		this.aq = new AQuery(this);
		setContentView(R.layout.activity_main);
		//引数を作成
		Bundle args = new Bundle();
		
		//Fragment処理
		FragmentTransaction tran = getFragmentManager().beginTransaction();
		//カレンダーフラグメント
		fragment_cal = new WcRecordCalendarFragment(); 
		//入力欄フラグメント
		fragment_inp = new WcRecordInputFragment();
		//家族選択フラグメント
		fragment_select = new FamilySelectFragment();
		
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
		} else {
			//キャッシュからfamily_idを取得
			this.family_id = readChacheFamilyId();
		}
		
		//家族データ取得処理
		List<Integer> family_list = getFamilyIdList();
		//データが0件の場合入力画面を表示する
		if(family_list.size() == 0){
			FamilyInputDialogFragment fragment_dlg = new FamilyInputDialogFragment();
			//modalにするフラグをセット
			args.putBoolean(CommonConst.BUNDLE_KEY_IS_MODAL, true);
			fragment_dlg.setArguments(args);
			fragment_dlg.show(getFragmentManager(), CommonConst.FRAGMENT_TAG_WCREC_INPUT);
		} else if(family_list.indexOf(Integer.valueOf(this.family_id)) < 0) {
			//リスト内にない場合は選択させる
			
		}
		//family_idをキャッシュに書き込み
		this.writeChacheFamilyId(this.family_id);
		
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
	}
	
	/**
	 * 条件なしのFamilyIDのリストを返却
	 * @return family_idのリスト
	 */
	private List<Integer> getFamilyIdList(){
		//家族データ取得処理
		FamilySelectModel selectModel = new FamilySelectModel(this);
		List<Integer> list = new ArrayList<Integer>();
		Iterator<BaseForm> ite = selectModel.selectAll().iterator();
		while(ite.hasNext()){
			list.add(((FamilyForm)ite.next()).getFamily_id());
		}
		return list;
	}
	
	/**
	 * キャッシュファイルを読み取ってfamily_idを返す
	 * @return family_id
	 */
	private int readChacheFamilyId(){
		int ret = 0;

		File chache = new File(getCacheDir(), CommonConst.CHACHE_FILE);
		if(chache.exists()){
			byte[] buffer = new byte[256];
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(chache);
				fis.read(buffer);
				try{
					ret = Integer.valueOf(new String(buffer, CommonConst.ENCODE));
				} catch(ClassCastException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	/**
	 * キャッシュファイルにfamily_idを書き込む
	 */
	private void writeChacheFamilyId(int family_id){
		File chache = new File(getCacheDir(), CommonConst.CHACHE_FILE);
		FileOutputStream fos = null;
		try {
			//存在している場合削除
			if(chache.exists()){
				chache.delete();
			}
			fos = new FileOutputStream(chache);
			fos.write(String.valueOf(family_id).getBytes());
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void callback() {
		if(fragment_cal != null && fragment_inp != null){
			fragment_cal.changeDisplay(family_id, wc_record_date, true);
			fragment_inp.changeDisplay(family_id, wc_record_date);
			((DrawerLayout)aq.id(R.id.dlMainFamilySelect).getView()).closeDrawers();
			//family_idを書き込む
			writeChacheFamilyId(family_id);
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
