package jp.sheepman.famical;

import jp.sheepman.common.activity.BaseActivity;
import jp.sheepman.common.form.BaseForm;
import jp.sheepman.famical.form.ActivityForm;
import jp.sheepman.famical.fragment.FamilyInputDialogFragment;
import jp.sheepman.famical.fragment.FamilySelectFragment;
import jp.sheepman.famical.util.CommonConst;
import jp.sheepman.famical.util.CommonLogUtil;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class FamilyActivity extends BaseActivity {
	
	FamilySelectFragment fragment_sel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		CommonLogUtil.method_start();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_family);
		
		FragmentManager mgr = getFragmentManager();
		FragmentTransaction tran = mgr.beginTransaction();
		
		if(mgr.findFragmentByTag(CommonConst.FRAGMENT_TAG_FAMILY_SELECT) == null){
			fragment_sel = new FamilySelectFragment();
			tran.replace(R.id.frmFamilyActivity, fragment_sel);
		}
		tran.commit();
		CommonLogUtil.method_end();
	}

	@Override
	public void callback() {
		CommonLogUtil.method_start();
		CommonLogUtil.method_end();
	}
	
	@Override
	public void callback(BaseForm form) {
		CommonLogUtil.method_start();
		if(form instanceof ActivityForm){
			Intent intent = new  Intent();
			intent.putExtra(CommonConst.BUNDLE_KEY_FAMILY_ID, ((ActivityForm)form).getFamily_id());
			setResult(RESULT_OK, intent);
			finish();
		}
		CommonLogUtil.method_end();
	}
}
