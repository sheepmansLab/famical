package jp.sheepman.famical;

import jp.sheepman.famical.fragment.FamilySelectFragment;
import android.app.Activity;
import android.os.Bundle;

public class FamilyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_family);
		
		getFragmentManager()
			.beginTransaction()
			.add(R.id.frmActFamFragment, new FamilySelectFragment(), "select")
			.commit();
	}
}
