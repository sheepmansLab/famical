package jp.sheepman.famical;

import jp.sheepman.famical.fragment.WcRecordCalendarFragment;
import jp.sheepman.famical.fragment.WcRecordInputFragment;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		FragmentTransaction tran = getFragmentManager().beginTransaction();
		
		tran.add(R.id.frmCalendarFragment, new WcRecordCalendarFragment());
		tran.add(R.id.frmInputFragment, new WcRecordInputFragment());
		
		tran.commit();
		
	}
}
