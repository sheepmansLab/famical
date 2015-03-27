/**
 * 
 */
package jp.sheepman.famical.fragment;

import jp.sheepman.common.fragment.BaseDialogFragment;
import jp.sheepman.famical.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * @author sheepman
 *
 */
public class InputDialogFragment extends BaseDialogFragment {
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
