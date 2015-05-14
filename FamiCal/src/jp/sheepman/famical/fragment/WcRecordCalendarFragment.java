package jp.sheepman.famical.fragment;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.fragment.BaseFragment;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.famical.R;
import jp.sheepman.famical.form.FamilyForm;
import jp.sheepman.famical.form.WcRecordForm;
import jp.sheepman.famical.model.FamilyModel;
import jp.sheepman.famical.model.WcRecordModel;
import jp.sheepman.famical.util.CommonConst;
import jp.sheepman.famical.util.CommonLogUtil;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.androidquery.AQuery;

public class WcRecordCalendarFragment extends BaseFragment {
	private AQuery aq;

	private LayoutInflater mInflator;	//Infrator
	private Calendar mCalendarBase;		//カレンダの月を決める基準日
	private Calendar wc_record_date;	//指定日
	private int family_id;				//表示している対象者ID
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d("famical","onSaveInstanceState Start");
		super.onSaveInstanceState(outState);
		//Bundleに画面情報を保持
		if(outState != null){
			Log.d("famical", "Data set to Bundle");
			outState.putInt(CommonConst.BUNDLE_KEY_FAMILY_ID, family_id);
			outState.putString(CommonConst.BUNDLE_KEY_WC_RECORD_DATE, CalendarUtil.cal2str(wc_record_date));
			outState.putString(CommonConst.BUNDLE_KEY_M_CALENDAR_BASE, CalendarUtil.cal2str(mCalendarBase));
		}
		Log.d("famical","onSaveInstanceState End");
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("famical","onCreate Start");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		aq = new AQuery(getActivity());
		//カレンダーの基準日にデフォルト値として当日をセット
		mCalendarBase = CalendarUtil.getToday();
		//指定日にデフォルト値として当日をセット
		wc_record_date = CalendarUtil.getToday();
		
		Log.d("famical","onCreate End");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("famical","onCreateView Start");
		this.mInflator = inflater;

		//引数を受け取る
		if(getArguments() != null){
			family_id = getArguments().getInt(CommonConst.BUNDLE_KEY_FAMILY_ID);
			wc_record_date = CalendarUtil.str2cal(getArguments().getString(CommonConst.BUNDLE_KEY_WC_RECORD_DATE));
			mCalendarBase = CalendarUtil.getMonthFirstDate(wc_record_date);
		}
		
		//Bundleのデータ復帰
		if(savedInstanceState != null){
			Log.d("famical", "Data restore from Bundle");
			wc_record_date = CalendarUtil.str2cal(savedInstanceState.getString(CommonConst.BUNDLE_KEY_WC_RECORD_DATE));
			mCalendarBase = CalendarUtil.str2cal(savedInstanceState.getString(CommonConst.BUNDLE_KEY_M_CALENDAR_BASE));
			family_id = savedInstanceState.getInt(CommonConst.BUNDLE_KEY_FAMILY_ID);
		}
		
		//Viewを生成
		View v = mInflator.inflate(R.layout.fragment_calebdar, null);
		//AQueryを初期化
		aq.recycle(v);
		//カレンダを作成
		createCalendarView();
		//セルのデータ表示
		setCellDetail(false);

		//家族データの表示
		setFamilyData();
		Log.d("famical","onCreateView End");
		return v;
	}
	

	/**
	 * カレンダーViewをセットする
	 */
	@SuppressLint("InflateParams")
	private void createCalendarView() {
        CommonLogUtil.method_start();
		// テーブル作成
		TableLayout tl = (TableLayout)aq.id(R.id.tlCalendar).getView();
		
		//Cell用のLayoutParamsを作成
		TableRow.LayoutParams p = new TableRow.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,
				TableRow.LayoutParams.MATCH_PARENT, 1);
		
		// カレンダーを作成
		for (int i = 0; i < tl.getChildCount(); i++) {
			// テーブル行のループ
			if (tl.getChildAt(i) instanceof TableRow) {
				TableRow tr = (TableRow) tl.getChildAt(i);
				tr.removeAllViews();
				tr.setVisibility(View.VISIBLE);
				// 日にち列のループ
				for (int j = 0; j < 7; j++) {
					// 日付セルのViewを生成
					final View cell = mInflator.inflate(R.layout.layout_calendar_cell, null);
					// 行にViewを追加
					tr.addView(cell, p);
				}
			}
		}
        CommonLogUtil.method_end();
	}
	
	/**
	 * 対象者のデータをセットする
	 */
	private void setFamilyData(){
        CommonLogUtil.method_start();
		FamilyModel model = new FamilyModel(getActivity());
		
		FamilyForm form = new FamilyForm();
		form.setFamily_id(family_id);
		
		Iterator<BaseForm> ite = model.selectById(form).iterator();
		if(ite.hasNext()){
			form = (FamilyForm)ite.next();
		}
		
		aq.id(R.id.tvCalFamilyName).text(form.getFamily_name() + "(" + form.getFamily_id() +")");
		//aq.id(R.id.ivCalFamily).image(Bitmapなど);
        CommonLogUtil.method_end();
	}
	
	/**
	 * セルに内容をセットする
	 */
	private void setCellDetail(boolean doAnimation){
        CommonLogUtil.method_start();
		//選択表示を消す
		aq.id(R.id.vSelectedFrame).visibility(View.GONE);
						
		//1日を取得し作業用にコピーする
		Calendar vCalTemp = CalendarUtil.getMonthFirstDate(CalendarUtil.clone(mCalendarBase));
		//表示用に年月を取得
		int year = CalendarUtil.getYear(vCalTemp);
		int month = CalendarUtil.getMonth(vCalTemp);

		//データ取得
		WcRecordModel model = new WcRecordModel(getActivity());
		WcRecordForm form = new WcRecordForm();
		form.setWc_record_date(vCalTemp);
		form.setFamily_id(family_id);
		List<BaseForm> list = model.selectByMonth(form);
		
		// ヘッダをセット
		aq.id(R.id.tvCalMonth).text(year + "年 " + month + "月");
		aq.id(R.id.tvCalPrev).clicked(lsnrPrev);
		aq.id(R.id.tvCalNext).clicked(lsnrNext);
		
		// 初日にリセット
		vCalTemp.set(Calendar.DATE, 1);
		vCalTemp.add(Calendar.DATE, (-1) * (vCalTemp.get(Calendar.DAY_OF_WEEK) - 1));
		
		//TableLayoutを取得
		TableLayout tl = (TableLayout)aq.id(R.id.tlCalendar).getView();

		//Cell用のAQueryを作成
		AQuery aqCell = new AQuery(getActivity());
		//カレンダーのセルをセット
		for(int i = 0; i < tl.getChildCount(); i ++){
			//型チェック
			if(tl.getChildAt(i) instanceof TableRow){
				TableRow tr = (TableRow)tl.getChildAt(i);
				//列を表示設定にする(月切り替えでたたまれてる可能性があるため)
				tr.setVisibility(View.VISIBLE);
				//非表示セル数
				int disableCount = 0;
				//日付分繰り返す
				for(int j = 0; j < tr.getChildCount(); j ++){
					View cell = tr.getChildAt(j);
					aqCell.recycle(cell);
					//セルの初期化
					cell.setTag(null);
					cell.setOnTouchListener(null);
					cell.setBackgroundColor(getResources().getColor(R.color.BLANK));
					//当月のデータのみ処理する
					if(month == CalendarUtil.getMonth(vCalTemp)){
						//セルを初期化する
						aqCell.id(R.id.tvCellDay).text(String.valueOf(CalendarUtil.getDate(vCalTemp)));
						aqCell.id(R.id.tvCelPeCircle).text(R.string.lblMarkCircle).textColorId(R.color.lightgrey);
						aqCell.id(R.id.tvCelPoCircle).text(R.string.lblMarkCircle).textColorId(R.color.lightgrey);
						aqCell.id(R.id.tvCelPeCount).text("0").textColorId(R.color.lightgrey);
						aqCell.id(R.id.tvCelPoCount).text("0").textColorId(R.color.lightgrey);
						if(j == 6){	//土曜日
							aqCell.id(R.id.tvCellDay).textColorId(R.color.steelblue);
							cell.setBackgroundResource(R.drawable.calendar_cell_sat);
						} else if(j == 0){	//日曜日
							aqCell.id(R.id.tvCellDay).textColorId(R.color.crimson);
							cell.setBackgroundResource(R.drawable.calendar_cell_sun);
						} else {	//その他
							aqCell.id(R.id.tvCellDay).textColorId(R.color.black);
							cell.setBackgroundResource(R.drawable.calendar_cell);
						}
						//データの注入
						Iterator<BaseForm> ite = list.iterator();
						while(ite.hasNext()){
							WcRecordForm f = (WcRecordForm)ite.next();
							if(CalendarUtil.cal2str(f.getWc_record_date()).equals(CalendarUtil.cal2str(vCalTemp))){
								//マークの色付け
								aqCell.id(R.id.tvCelPeCircle).textColorId(R.color.powderblue);
								aqCell.id(R.id.tvCelPoCircle).textColorId(R.color.mustard);
								//取得した値を表示する
								aqCell.id(R.id.tvCelPeCount).text(String.valueOf(f.getPe_count())).textColorId(R.color.black);
								aqCell.id(R.id.tvCelPoCount).text(String.valueOf(f.getPo_count())).textColorId(R.color.black);
								ite.remove();
							}
						}
						//当日の場合色付け
						if(vCalTemp.compareTo(wc_record_date) == 0){
							setSelectedColor(cell);
						}
						//その日の文字列をタグにセット
						cell.setTag(CalendarUtil.cal2str(vCalTemp));
						//タッチイベントをセット
						cell.setOnTouchListener(lsnrTouch);
					} else {
						//前月翌月の場合は除外する
						//セルの初期化
						aqCell.id(R.id.tvCellDay).text("");
						aqCell.id(R.id.tvCelPeCircle).text("");
						aqCell.id(R.id.tvCelPoCircle).text("");
						aqCell.id(R.id.tvCelPeCount).text("");
						aqCell.id(R.id.tvCelPoCount).text("");
						//背景をグレーにする
						aqCell.id(R.id.tvCellDay).textColorId(R.color.black);
						cell.setBackgroundColor(Color.GRAY);
						//非表示セル数をカウント
						disableCount++;
					}
					//列すべてが非表示ならその列は消す
					if(disableCount == tr.getChildCount()){
						tr.setVisibility(View.GONE);
					}
					//翌日にする
					vCalTemp.add(Calendar.DATE, 1);
				}
			}
		}
		//月が異なる場合はアニメーションする
		if(doAnimation){
			AlphaAnimation alpha = new AlphaAnimation(0.5f, 1.0f);
			alpha.setDuration(300);
			tl.startAnimation(alpha);
		}
        CommonLogUtil.method_end();
	}
	
	/**
	 * 選択中のセルの色を変える
	 */
	private void setSelectedColor(final View cell){
        CommonLogUtil.method_start();
		//画面のレイアウトが決まった段階でレイアウトの計算をする
		cell.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
                CommonLogUtil.method_start();
				//不要になるのでリスナは削除する
				cell.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				
				View ｍFrmSelectedCell = aq.id(R.id.vSelectedFrame).getView();
				//セルの非表示化
				ｍFrmSelectedCell.setVisibility(View.GONE);
				
				//画面情報を取得
				Rect rect = new Rect();
				//ステータスバーの高さ
				getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
				
				//セルの座標取得
				int[] location = new int[2];
				cell.getLocationInWindow(location);
				
				//フレームの座標をセット
				ｍFrmSelectedCell.setX(location[0]);
				ｍFrmSelectedCell.setY(location[1] - rect.top);
				//フレームの大きさをセルにあわせる
				ｍFrmSelectedCell.getLayoutParams().width = cell.getWidth();
				ｍFrmSelectedCell.getLayoutParams().height = cell.getHeight();
				
				//セルを表示する
				ｍFrmSelectedCell.setVisibility(View.VISIBLE);
                CommonLogUtil.method_end();
			}
		});
        CommonLogUtil.method_end();
	}
	
	/**
	 * 前月ボタン押下
	 */
	private OnClickListener lsnrPrev = new OnClickListener() {
		@Override
		public void onClick(View v) {
            CommonLogUtil.method_start();
			mCalendarBase.add(Calendar.MONTH, -1);
			setCellDetail(true);
            CommonLogUtil.method_end();
		}
	};
	
	/**
	 * 翌月ボタン押下
	 */
	private OnClickListener lsnrNext = new OnClickListener() {
		@Override
		public void onClick(View v) {
            CommonLogUtil.method_start();
			mCalendarBase.add(Calendar.MONTH, 1);
			setCellDetail(true);
            CommonLogUtil.method_end();
		}
	};
	
	/**
	 * セル押下時のイベント
	 */
	OnTouchListener lsnrTouch = new OnTouchListener() {
		private final float adjust = 50;
		
		private float x1;
		private float x2;
		private float y1;
		private float y2;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
            CommonLogUtil.method_start();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				x1 = event.getX();
				y1 = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				x2 = event.getX();
				y2 = event.getY();
				//横移動の場合
				if(Math.abs(x2 - x1) > Math.abs(y2 - y1)){
					//一定量を超えた場合
					if(Math.abs(x2 - x1) > adjust){
						//右移動の場合
						if(x2 > x1){
							mCalendarBase.add(Calendar.MONTH, -1);
						//左移動の場合
						} else {
							mCalendarBase.add(Calendar.MONTH, 1);
						}
						//カレンダー再構築
						setCellDetail(true);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if(getTargetFragment() instanceof WcRecordInputFragment){
					((WcRecordInputFragment)getTargetFragment()).changeDisplay(family_id, CalendarUtil.str2cal(v.getTag().toString()));
					//選択したセルの日付を保持
					wc_record_date = CalendarUtil.str2cal(v.getTag().toString());
				}
				setSelectedColor(v);
				break;
			default:
				break;
			}
            CommonLogUtil.method_end();
			return true;
		}
	};
	
	/**
	 * 外部からのカレンダー更新指示
	 * @param family_id
	 * @param wc_record_date
	 * @param reload
	 */
	public void changeDisplay(int family_id, Calendar wc_record_date, boolean reload){
        CommonLogUtil.method_start();
		this.family_id = family_id;
		
		this.mCalendarBase = CalendarUtil.getMonthFirstDate(wc_record_date);
		//指定が異なる月であった場合カレンダーを再描画する
		if(CalendarUtil.getMonth(wc_record_date) != CalendarUtil.getMonth(this.wc_record_date) || reload){
			setCellDetail(true);
		}
		//日付をFragmentに保持させる
		this.wc_record_date = wc_record_date;

		//家族データの更新
		setFamilyData();
		
		//指定日付のセルを探して色付けする
		TableLayout tlCalendar = (TableLayout)aq.id(R.id.tlCalendar).getView();
		//TableRow分回す
		for(int i = 0; i < tlCalendar.getChildCount(); i ++){
			//型チェック
			if(tlCalendar.getChildAt(i) instanceof TableRow){
				TableRow tr = (TableRow)tlCalendar.getChildAt(i);
				//TableRow内のセル分回す
				for(int j = 0; j < tr.getChildCount(); j ++){
					//型チェック＆タグ情報チェック
					if(tr.getChildAt(j) instanceof View 
					&& tr.getChildAt(j).getTag() instanceof String){
						//指定日と等しければ色づけ
						if(((String)tr.getChildAt(j).getTag()).equals(CalendarUtil.cal2str(wc_record_date))){
							setSelectedColor(tr.getChildAt(j));
							//以降の処理は不要なので終了
							Log.d("famical", "changeDate End");
							return;
						}
					}
				}
			}
		}
        CommonLogUtil.method_end();
	}
	
	@Override
	public void callback() {
        CommonLogUtil.method_start();
		setCellDetail(true);
        CommonLogUtil.method_end();
	}
	
	@Override
	public void callback(BaseForm arg0) {
        CommonLogUtil.method_start();
		this.callback();
        CommonLogUtil.method_end();
	}
}
