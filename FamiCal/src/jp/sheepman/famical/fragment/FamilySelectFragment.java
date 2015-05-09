package jp.sheepman.famical.fragment;

import java.util.ArrayList;
import java.util.List;

import jp.sheepman.common.activity.BaseActivity;
import jp.sheepman.common.adapter.BaseCustomAdapter;
import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.fragment.BaseFragment;
import jp.sheepman.famical.MainActivity;
import jp.sheepman.famical.R;
import jp.sheepman.famical.form.FamilyForm;
import jp.sheepman.famical.form.MainActivityForm;
import jp.sheepman.famical.model.FamilySelectModel;
import jp.sheepman.famical.util.CommonConst;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.androidquery.AQuery;

public class FamilySelectFragment extends BaseFragment {
	private Context mContext;
	private AQuery aq;
	private FamilySelectListAdapter mAdapter;
	
	private int family_id;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		aq = new AQuery(mContext);
		if(getArguments() != null){
			this.family_id = getArguments().getInt(CommonConst.BUNDLE_KEY_FAMILY_ID);
		}
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
				dialog.setTargetFragment(FamilySelectFragment.this, 0);
				dialog.show(getFragmentManager(), "input");
			}
		});

		((ListView)aq.id(R.id.lvFamSel).getView()).setOnItemClickListener(lsnrItemClick);
		
		//アダプタを生成してセット
		mAdapter = new FamilySelectListAdapter();

		//画面の反映
		reload();
		
		//アダプタをセット
		aq.id(R.id.lvFamSel).adapter(mAdapter);
		return view;
	}
	
	/**
	 * 画面のリロード
	 */
	private void reload(){
		mAdapter.clear();
		//データの取得
		FamilySelectModel model = new FamilySelectModel(mContext);
		List<BaseForm> list = model.selectAll();
		
		//アダプタにデータをセット
		mAdapter.setList(list);
		mAdapter.notifyDataSetChanged();
	}
	
	/**
	 * アイテムをクリックしたときのイベントリスナ
	 */
	OnItemClickListener lsnrItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			Log.d("famical", "item");
			MainActivityForm form = new MainActivityForm();
			if(arg1.getTag() instanceof Integer){
				FamilySelectFragment.this.family_id = Integer.valueOf(arg1.getTag().toString());
			}
			form.setFamily_id(FamilySelectFragment.this.family_id);
			((BaseActivity)getActivity()).callback(form);
			reload();
		}
	};
	
	//リスト用のアダプタクラス
	private class FamilySelectListAdapter extends BaseCustomAdapter {
		public FamilySelectListAdapter() {
			setList(new ArrayList<BaseForm>());
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if(v == null){
				v = getActivity().getLayoutInflater().inflate(R.layout.layout_family_list_item, null);
			}
			//データ取得
			FamilyForm form = (FamilyForm)list.get(position);
			//Viewにfamily_idをタグ付けする
			v.setTag(form.getFamily_id());
			aq.recycle(v);
			//項目にデータをセット
			//aq.id(R.id.ivFamSelItemFamilyPict).image(Bitmapなど)
			aq.id(R.id.tvFamSelItemFamilyName).text(form.getFamily_name() +"_"+String.valueOf(form.getFamily_id()));
			aq.id(R.id.btnFamSelItemEdit).clicked(lsnrClickEdit);
			aq.id(R.id.btnFamSelItemDel).clicked(null);
			//選択中のIDと同じならば背景色を設定
			if(form.getFamily_id() == FamilySelectFragment.this.family_id){
				aq.recycle(v).backgroundColorId(R.color.SELECTED);
			} else {
				aq.recycle(v).backgroundColorId(R.color.white);
			}
				
			return v;
		}
		
		/**
		 * 編集ボタン押下時のイベントリスナ
		 */
		OnClickListener lsnrClickEdit = new OnClickListener() {
			@Override
			public void onClick(View v) {
				FamilyInputDialogFragment fragment = new FamilyInputDialogFragment();
				Bundle args = new Bundle();
				if(v.getTag() instanceof Integer){
					args.putInt(CommonConst.BUNDLE_KEY_FAMILY_ID, Integer.valueOf(v.getTag().toString()));
				}
				fragment.setArguments(args);
				fragment.setTargetFragment(FamilySelectFragment.this, 0);
				fragment.show(getChildFragmentManager(), "");
			}
		};
	}
	
	@Override
	public void callback() {
		//再描画
		reload();
	}

	/**
	 * formを受け取って再描画する
	 */
	@Override
	public void callback(BaseForm arg0) {
		//引数がFamilyFormであればIDを保持する
		if(arg0 instanceof FamilyForm){
			this.family_id = ((FamilyForm)arg0).getFamily_id();
		}
		reload();
	}
}
