package jp.sheepman.famical;

import java.util.Calendar;

import jp.sheepman.common.fragment.BaseDialogFragment;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.famical.fragment.FamilyInputDialogFragment;
import jp.sheepman.famical.fragment.WcRecordCalendarFragment;
import jp.sheepman.famical.fragment.WcRecordInputFragment;
import jp.sheepman.famical.model.FamilySelectModel;
import jp.sheepman.famical.util.CommonConst;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class MainActivity extends Activity {

	private int family_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Calendar wc_record_date = CalendarUtil.getToday();
		
		//カレンダーフラグメント
		Fragment fragment_cal = new WcRecordCalendarFragment(); 
		//入力欄フラグメント
		Fragment fragment_input = new WcRecordInputFragment(); 

		//TODO 家族ID処理待ち
		this.family_id = 1;
		//家族選択処理
		FamilySelectModel familyModel = new FamilySelectModel(MainActivity.this);
		BaseDialogFragment fragment_family = null;
		
		if(familyModel.selectAll().size() == 0){
			//TODO 家族登録画面表示
			fragment_family = new FamilyInputDialogFragment();
		} else {
			//TODO 家族登録画面表示
			fragment_family = new FamilyInputDialogFragment();
			//TODO 家族選択画面表示
		}
		fragment_family.show(getFragmentManager(), "family");
		//TODO family_idを受け取る
		
		//引数をセット
		Bundle args = new Bundle();
		args.putInt("family_id", family_id);
		args.putString("wc_record_date", CalendarUtil.cal2str(wc_record_date));
		fragment_cal.setArguments(args);
		fragment_input.setArguments(args);
		
		//targetフラグメントをセット
		fragment_cal.setTargetFragment(fragment_input, 0);
		fragment_input.setTargetFragment(fragment_cal, 0);
		
		FragmentTransaction tran = getFragmentManager().beginTransaction();
		tran.add(R.id.frmCalendarFragment
				, fragment_cal
				, CommonConst.FRAGMENT_TAG_CALENDAR);
		tran.add(R.id.frmInputFragment
				, fragment_input
				, CommonConst.FRAGMENT_TAG_WCREC_INPUT);
		
		tran.commit();
		
	}
	
	/**
	 * 外部からfamily_idをセットする
	 * @param family_id
	 */
	public void callbackSetFamilyId(int family_id){
		this.family_id = family_id;
	}
}
