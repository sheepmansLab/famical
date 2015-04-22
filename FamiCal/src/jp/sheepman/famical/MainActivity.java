package jp.sheepman.famical;

import java.util.Calendar;
import java.util.Locale;

import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.famical.fragment.WcRecordCalendarFragment;
import jp.sheepman.famical.fragment.WcRecordInputFragment;
import jp.sheepman.famical.util.CommonConst;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//カレンダーを作成
		Calendar cal = Calendar.getInstance(Locale.JAPANESE);
		
		//カレンダーフラグメント
		Fragment fragment_cal = new WcRecordCalendarFragment(); 
		//入力欄フラグメント
		Fragment fragment_input = new WcRecordInputFragment(); 
		
		//引数をセット
		Bundle args = new Bundle();
		args.putInt("family_id", 1);	//TODO 家族登録処理待ち
		args.putString("wc_record_date", CalendarUtil.cal2str(cal));		
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
}
