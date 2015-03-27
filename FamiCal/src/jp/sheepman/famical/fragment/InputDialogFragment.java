/**
 * 
 */
package jp.sheepman.famical.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import jp.sheepman.common.fragment.BaseFragment;
import jp.sheepman.famical.R;


/**
 * @author sheepman
 *
 */
public class InputDialogFragment extends BaseFragment {
	private LayoutInflater inflator;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflator = inflater;
		return this.inflator.inflate(R.layout.input_dialog_fragment, null);
	}
	
	@Override
	public void callback() {
	}

}
