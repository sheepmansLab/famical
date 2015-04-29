package jp.sheepman.famical.fragment;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import jp.sheepman.common.form.BaseForm;
import jp.sheepman.common.fragment.BaseFragment;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.famical.R;
import jp.sheepman.famical.form.WcRecordForm;
import jp.sheepman.famical.model.WcRecordSelectModel;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class WcRecordCalendarFragment extends BaseFragment {
	private LayoutInflater mInflator;
	private Calendar mCalendarBase;
	
	private int family_id;
	private Calendar wc_record_date;
	
	private View ｍFrmSelectedCell;
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d("famical","onSaveInstanceState");
		super.onSaveInstanceState(outState);
		if(outState != null){
			outState.putInt("family_id", family_id);
			outState.putString("wc_record_date", CalendarUtil.cal2str(wc_record_date));
		}
	}
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("famical","onCreate");
		super.onCreate(savedInstanceState);
		mCalendarBase = CalendarUtil.getToday();
		wc_record_date = CalendarUtil.getToday();
		
		//引数を受け取る
		if(getArguments() != null){
			family_id = getArguments().getInt("family_id");
			wc_record_date = CalendarUtil.str2cal(getArguments().getString("wc_record_date"));
		}
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("famical","onCreateView");
		this.mInflator = inflater;
		//Viewを生成
		View v = mInflator.inflate(R.layout.fragment_calebdar, null);
		//選択表示用の枠
		this.ｍFrmSelectedCell = v.findViewById(R.id.vSelectedFrame);
		
		createCalendarView(v, mCalendarBase, false);
		
		return v;
	}
	

	/**
	 * カレンダーViewをセットする
	 * @param v
	 * @param vCal
	 */
	@SuppressLint("InflateParams")
	private void createCalendarView(View v, Calendar vCal, boolean doAmimation) {
		//処理用にカレンダーを複製する
		Calendar vCalTemp = CalendarUtil.clone(vCal);
		
		int year = CalendarUtil.getYear(vCalTemp);
		int month = CalendarUtil.getMonth(vCalTemp);
		
		//選択表示を消す
		this.ｍFrmSelectedCell.setVisibility(View.GONE);
		
		// ヘッダをセット
		LinearLayout llHeader = (LinearLayout) v.findViewById(R.id.llCalendarHeader);
		TextView tvCalPrev = (TextView) llHeader.findViewById(R.id.tvCalPrev);
		TextView tvCalNext = (TextView) llHeader.findViewById(R.id.tvCalNext);
		TextView tvCalMonth = (TextView) llHeader.findViewById(R.id.tvCalMonth);
		
		TextView tvCalFamilyName = (TextView) v.findViewById(R.id.tvCalFamilyName);
		ImageView ivCalFamily = (ImageView) v.findViewById(R.id.ivCalFamily);
		
		tvCalMonth.setText(year + "年 " + month + "月");
		tvCalPrev.setOnClickListener(lsnrPrev);
		tvCalNext.setOnClickListener(lsnrNext);
		tvCalFamilyName.setText(String.valueOf(family_id));
		
		// テーブル作成
		TableLayout tl = (TableLayout) v.findViewById(R.id.tlCalendar);
		
		//Cell用のLayoutParamsを作成
		TableRow.LayoutParams p = new TableRow.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,
				TableRow.LayoutParams.MATCH_PARENT, 1);
		
		//データ取得
		WcRecordSelectModel model = new WcRecordSelectModel(getActivity());
		WcRecordForm form = new WcRecordForm();
		form.setWc_record_date(vCalTemp);
		List<BaseForm> list = model.selectByMonth(form);
		
		// 初日にリセット
		vCalTemp.set(Calendar.DATE, 1);
		vCalTemp.add(Calendar.DATE, (-1) * (vCalTemp.get(Calendar.DAY_OF_WEEK) - 1));

		// カレンダーを作成
		for (int i = 0; i < tl.getChildCount(); i++) {
			// テーブル行のループ
			if (tl.getChildAt(i) instanceof TableRow) {
				TableRow tr = (TableRow) tl.getChildAt(i);
				tr.removeAllViews();
				tr.setVisibility(View.VISIBLE);
				// 日にち列のループ
				int countDisable = 0;
				for (int j = 0; j < 7; j++) {
					// 日付セルのViewを生成
					final View cell = mInflator.inflate(R.layout.layout_calendar_cell, null);
					// 行にViewを追加
					tr.addView(cell, p);
					// 前月、次月の場合は対象外
					if (month == CalendarUtil.getMonth(vCalTemp)) {
						//当月のみマークを表示
						((TextView)cell.findViewById(R.id.tvCelPeCircle)).setText(R.string.lblMarkCircle);
						((TextView)cell.findViewById(R.id.tvCelPoCircle)).setText(R.string.lblMarkCircle);
						//初期表示のカウント0
						((TextView)cell.findViewById(R.id.tvCelPeCount)).setText("0");
						((TextView)cell.findViewById(R.id.tvCelPoCount)).setText("0");
						
						// セルの日付をセット
						((TextView) cell.findViewById(R.id.tvCellDay))
								.setText(String.valueOf(CalendarUtil.getDate(vCalTemp)));
						// 付加情報として日付をセット
						cell.setTag(CalendarUtil.cal2str(vCalTemp));
						
						//その日のデータがないかチェックする
						Iterator<BaseForm> ite = list.iterator();
						while(ite.hasNext()){
							WcRecordForm f = (WcRecordForm)ite.next();
							if(CalendarUtil.cal2str(f.getWc_record_date()).equals(CalendarUtil.cal2str(vCalTemp))){
								((TextView)cell.findViewById(R.id.tvCelPeCount)).setText(String.valueOf(f.getPe_count()));
								((TextView)cell.findViewById(R.id.tvCelPoCount)).setText(String.valueOf(f.getPo_count()));
								//色を付ける
								((TextView)cell.findViewById(R.id.tvCelPeCircle)).setTextColor(getResources().getColor(R.color.powderblue));
								((TextView)cell.findViewById(R.id.tvCelPoCircle)).setTextColor(getResources().getColor(R.color.mustard));
								((TextView)cell.findViewById(R.id.tvCelPeCount)).setTextColor(getResources().getColor(R.color.black));
								((TextView)cell.findViewById(R.id.tvCelPoCount)).setTextColor(getResources().getColor(R.color.black));

								ite.remove();
							}
						}
						//土日に色を設定する
						if(j == 6){
							cell.setBackgroundResource(R.drawable.calendar_cell_sat);
							//赤文字
							((TextView)cell.findViewById(R.id.tvCellDay)).setTextColor(getResources().getColor(R.color.steelblue));
						} else if(j == 0) {
							cell.setBackgroundResource(R.drawable.calendar_cell_sun);
							//赤文字
							((TextView)cell.findViewById(R.id.tvCellDay)).setTextColor(getResources().getColor(R.color.crimson));
						}
						// 押下時のイベントをセット
						cell.setOnTouchListener(lsnrTouch);
						
						//指定日だった場合色をつける
						if(vCalTemp.compareTo(wc_record_date) == 0){
							setSelectedColor(cell);
						}
					} else {
						cell.setBackgroundColor(Color.GRAY);
						countDisable ++;
					}
					//全部別の月の場合行を隠す
					if(countDisable == 7){
						tr.setVisibility(View.GONE);
					}
					//次の日にする
					vCalTemp.add(Calendar.DATE, 1);
				}
			}
		}

		if(doAmimation){
			AlphaAnimation alpha = new AlphaAnimation(0.5f, 1.0f);
			alpha.setDuration(300);
			tl.startAnimation(alpha);
		}
	}
	
	/**
	 * 選択中のセルの色を変える
	 */
	private void setSelectedColor(final View cell){
		Log.d("famical", "setSelectedColor");
		//画面のレイアウトが決まった段階でレイアウトの計算をする
		cell.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				Log.d("famical", "onGlobalLayout");
				//不要になるのでリスナは削除する
				cell.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				
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
			}
		});
	}
	
	/**
	 * 前月ボタン押下
	 */
	private OnClickListener lsnrPrev = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mCalendarBase.add(Calendar.MONTH, -1);
			createCalendarView(getView(), mCalendarBase, true);
		}
	};
	
	/**
	 * 翌月ボタン押下
	 */
	private OnClickListener lsnrNext = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mCalendarBase.add(Calendar.MONTH, 1);
			createCalendarView(getView(), mCalendarBase, true);
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
						createCalendarView(getView(), mCalendarBase, true);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if(getTargetFragment() instanceof WcRecordInputFragment){ 
					((WcRecordInputFragment)getTargetFragment()).changeDate(1, CalendarUtil.str2cal(v.getTag().toString()));
					//選択したセルの日付を保持
					wc_record_date = CalendarUtil.str2cal(v.getTag().toString());
				}
				setSelectedColor(v);
				break;
			default:
				break;
			}
			return true;
		}
	};
	
	@Override
	public void callback() {
		createCalendarView(getView(), mCalendarBase, false);
	}
	
	/**
	 * 外部からのカレンダー更新指示
	 * @param family_id
	 * @param wc_record_date
	 */
	public void changeDate(int family_id, Calendar wc_record_date){
		this.family_id = family_id;
		
		//指定が異なる月であった場合カレンダーを再描画する
		if(CalendarUtil.getMonth(wc_record_date) != CalendarUtil.getMonth(this.wc_record_date)){
			createCalendarView(getView(), wc_record_date, true);
		} 
		
		//日付をFragmentに保持させる
		this.wc_record_date = wc_record_date;
		this.mCalendarBase = CalendarUtil.getMonthFirstDate(wc_record_date);
		
		//指定日付のセルを探して色付けする
		TableLayout tlCalendar = (TableLayout)getView().findViewById(R.id.tlCalendar);
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
							return;
						}
					}
				}
			}
		}
		
	}
}
