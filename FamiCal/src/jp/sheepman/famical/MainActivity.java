package jp.sheepman.famical;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import com.androidquery.AQuery;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import jp.sheepman.common.activity.BaseActivity;
import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.famical.form.MainActivityForm;
import jp.sheepman.famical.form.FamilyForm;
import jp.sheepman.famical.fragment.FamilySelectFragment;
import jp.sheepman.famical.fragment.WcRecordCalendarFragment;
import jp.sheepman.famical.fragment.WcRecordInputFragment;
import jp.sheepman.famical.model.FamilyModel;
import jp.sheepman.famical.util.CommonConst;
import jp.sheepman.famical.util.CommonFileUtil;
import jp.sheepman.famical.util.CommonLogUtil;

public class MainActivity extends BaseActivity {
	private AQuery aq;
	
	private int mFamily_id;
	private Calendar mWc_record_date;

	private WcRecordCalendarFragment mFragment_cal;	//カレンダーフラグメント
	private WcRecordInputFragment mFragment_inp;		//入力欄フラグメント
	private FamilySelectFragment mFragment_select;	//家族選択フラグメント

    /**
     * 画面情報保存
     * @param outState  Bundle
     */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		CommonLogUtil.method_start();
        outState.putInt(CommonConst.BUNDLE_KEY_FAMILY_ID, mFamily_id);
        outState.putString(CommonConst.BUNDLE_KEY_WC_RECORD_DATE, CalendarUtil.cal2str(mWc_record_date));
		CommonLogUtil.method_end();
	}

    /**
     * Activity作成時
     * @param savedInstanceState    Bundle
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		CommonLogUtil.method_start();
		setContentView(R.layout.activity_main);

		super.onCreate(savedInstanceState);
		this.aq = new AQuery(this);

		//キャッシュからfamily_idを取得
		this.mFamily_id = CommonFileUtil.readChacheFamilyId(getCacheDir());
		//Bundleに保持していた場合再取得
		if(savedInstanceState != null){
			this.mFamily_id = savedInstanceState.getInt(CommonConst.BUNDLE_KEY_FAMILY_ID);
			this.mWc_record_date = CalendarUtil.str2cal(savedInstanceState.getString(CommonConst.BUNDLE_KEY_WC_RECORD_DATE));
		}
        //family_idをキャッシュに書き込み
        CommonFileUtil.writeChacheFamilyId(getCacheDir(), this.mFamily_id);

        //対象日付
		if (mWc_record_date == null) {
			//当日をセット
			this.mWc_record_date = CalendarUtil.getToday();
		}
		
		//Fragment処理
		FragmentTransaction tran = getFragmentManager().beginTransaction();
		//カレンダーフラグメント
		mFragment_cal = new WcRecordCalendarFragment();
        //入力欄フラグメント
        mFragment_inp = new WcRecordInputFragment();
        //家族選択フラグメント
        mFragment_select = new FamilySelectFragment();

        //引数をセット
        Bundle args = getArgs();
		mFragment_cal.setArguments(args);
        mFragment_inp.setArguments(args);
        mFragment_select.setArguments(args);

		//targetフラグメントをセット
		mFragment_cal.setTargetFragment(mFragment_inp, 0);
        mFragment_inp.setTargetFragment(mFragment_cal, 0);

        //フラグメントをセット
        tran.replace(R.id.frmCalendarFragment
                , mFragment_cal, CommonConst.FRAGMENT_TAG_CALENDAR);
		tran.replace(R.id.frmInputFragment
				, mFragment_inp, CommonConst.FRAGMENT_TAG_WCREC_INPUT);
        tran.replace(R.id.frmDrawerLayout
                , mFragment_select, CommonConst.FRAGMENT_TAG_FAMILY_SELECT);
        tran.commit();
        //ドロアーのロック有無をチェック
        setDrawerLockMode();

        //広告の設定
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
		CommonLogUtil.method_end();
	}

    /**
     * Fragmentに渡すBundleを作成する
     * @return
     */
    private Bundle getArgs(){
        //引数を作成
        Bundle args = new Bundle();
        args.putInt(CommonConst.BUNDLE_KEY_FAMILY_ID, mFamily_id);
        args.putString(CommonConst.BUNDLE_KEY_WC_RECORD_DATE, CalendarUtil.cal2str(mWc_record_date));
        return args;
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
     * ドロアーの状態をチェックして設定
     */
    private void setDrawerLockMode(){
        //家族データ取得処理
        List<Integer> family_list = getFamilyIdList();
        //データが0件の場合入力画面を表示する
        if(family_list.size() == 0 || family_list.indexOf(Integer.valueOf(this.mFamily_id)) < 0) {
            //ドロアーを閉じなくさせる
            ((DrawerLayout)aq.id(R.id.dlMainFamilySelect).getView())
                    .setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        } else {
            //ドロアーのロックを解除
            ((DrawerLayout)aq.id(R.id.dlMainFamilySelect).getView())
                    .setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
	
	/**
	 * 画面上の表示をリロードする
	 */
	private void reload(){
		CommonLogUtil.method_start();
		if(mFragment_cal != null) {
            mFragment_cal.changeDisplay(mFamily_id, mWc_record_date, true);
        }
        if(mFragment_inp != null) {
            mFragment_inp.changeDisplay(mFamily_id, mWc_record_date);
        }
        if(mFragment_select != null) {
            mFragment_select.changeDisplay(mFamily_id);
            ((DrawerLayout) aq.id(R.id.dlMainFamilySelect).getView()).closeDrawers();
        }
        //ドロアーのロックをチェック
        setDrawerLockMode();
        //family_idを書き込む
        CommonFileUtil.writeChacheFamilyId(getCacheDir(), mFamily_id);
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
        //パラメータがActivityFormだった場合
		if(arg0 instanceof MainActivityForm){
            MainActivityForm form = (MainActivityForm)arg0;
			this.mFamily_id = form.getFamily_id();
			if(form.getWc_record_date() != null){
				mWc_record_date = form.getWc_record_date();
			}
            if(form.getReloadFlg()){
                reload();
            }
		}
		CommonLogUtil.method_end();
	}
}
