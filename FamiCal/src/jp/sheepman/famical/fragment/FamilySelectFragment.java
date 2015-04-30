package jp.sheepman.famical.fragment;

import java.util.List;

import jp.sheepman.common.adapter.BaseCustomAdapter;
import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.fragment.BaseFragment;
import jp.sheepman.famical.R;
import jp.sheepman.famical.form.FamilyForm;
import jp.sheepman.famical.model.FamilySelectModel;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.androidquery.AQuery;

public class FamilySelectFragment extends BaseFragment {
	private Context mContext;
	private AQuery aq;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		aq = new AQuery(mContext);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//ベースとなるViewを生成
		View view = inflater.inflate(R.layout.fragment_family_select, null);
		//aqのルートをViewでセット
		aq.recycle(view);
		
		aq.id(R.id.btnFamSelInput).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FamilyInputDialogFragment dialog = new FamilyInputDialogFragment();
				dialog.show(getFragmentManager(), "input");
			}
		});
		
		
		//アダプタを生成してセット
		FamilySelectListAdapter adapter = new FamilySelectListAdapter();
		
		//データの取得
		FamilySelectModel model = new FamilySelectModel(mContext);
		List<BaseForm> list = model.selectAll();
		
		//アダプタにデータをセット
		adapter.setList(list);
		
		//更新
		aq.id(R.id.lvFamSel).adapter(adapter);
		adapter.notifyDataSetChanged();
		
		return view;
	}
	
	@Override
	public void callback() {
	}
	
	//リスト用のアダプタクラス
	private class FamilySelectListAdapter extends BaseCustomAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if(v == null){
				v = getActivity().getLayoutInflater().inflate(R.layout.layout_family_list_item, null);
			}
			//データ取得
			FamilyForm form = (FamilyForm)list.get(position);
			aq.recycle(v);
			//aq.id(R.id.ivFamSelItemFamilyPict).image(Bitmapなど)
			aq.id(R.id.tvFamSelItemFamilyName).text(form.getFamily_name());
			
			return v;
		}
		
	}

}
