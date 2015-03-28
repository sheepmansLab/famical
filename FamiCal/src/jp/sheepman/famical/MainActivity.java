package jp.sheepman.famical;

import jp.sheepman.famical.fragment.WcRecordCalendarFragment;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getFragmentManager().beginTransaction().add(R.id.frmMainActivity, new WcRecordCalendarFragment()).commit();
	}
}
