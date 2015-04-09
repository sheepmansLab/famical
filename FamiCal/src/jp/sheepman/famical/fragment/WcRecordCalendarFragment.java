package jp.sheepman.famical.fragment;

import java.util.Calendar;
import java.util.Date;
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
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class WcRecordCalendarFragment extends BaseFragment {
	private LayoutInflater inflator;
	private Calendar cal;
	
	int statusBarHeight;
	View frm;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cal = Calendar.getInstance();
		cal.setTime(new Date());
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflator = inflater;
		//Viewを生成
		View v = inflator.inflate(R.layout.fragment_calebdar, null);
		//選択表示用の枠
		this.frm = v.findViewById(R.id.vSelectedFrame);
		
		createCalendarView(v, cal, false);
		
		return v;
	}

	/**
	 * カレンダーViewをセットする
	 * @param v
	 * @param cal
	 */
	@SuppressLint("InflateParams")
	private void createCalendarView(View v, Calendar cal, boolean doAmimation) {
		int year = CalendarUtil.getYear(cal);
		int month = CalendarUtil.getMonth(cal);
		
		//選択表示を消す
		this.frm.setVisibility(View.GONE);
		
		// ヘッダをセット
		LinearLayout llHeader = (LinearLayout) v.findViewById(R.id.llCalendarHeader);
		TextView tvCalPrev = (TextView) llHeader.findViewById(R.id.tvCalPrev);
		TextView tvCalNext = (TextView) llHeader.findViewById(R.id.tvCalNext);
		TextView tvCalMonth = (TextView) llHeader.findViewById(R.id.tvCalMonth);
		tvCalMonth.setText(year + "年 " + month + "月");
		tvCalPrev.setOnClickListener(lsnrPrev);
		tvCalNext.setOnClickListener(lsnrNext);

		// テーブル作成
		TableLayout tl = (TableLayout) v.findViewById(R.id.tlCalendar);
		
		//Cell用のLayoutParamsを作成
		TableRow.LayoutParams p = new TableRow.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,
				TableRow.LayoutParams.MATCH_PARENT, 1);
		
		//データ取得
		WcRecordSelectModel model = new WcRecordSelectModel(getActivity());
		WcRecordForm form = new WcRecordForm();
		form.setWc_record_date(cal);
		List<BaseForm> list = model.selectByMonth(form);
		
		// 初日にリセット
		cal.set(Calendar.DATE, 1);
		cal.add(Calendar.DATE, (-1) * (cal.get(Calendar.DAY_OF_WEEK) - 1));

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
					View cell = inflator.inflate(R.layout.layout_calendar_cell, null);
					// 行にViewを追加
					tr.addView(cell, p);
					// 前月、次月の場合は対象外
					if (month == CalendarUtil.getMonth(cal)) {
						//初期表示のカウント0
						((TextView)cell.findViewById(R.id.tvCelPeCount)).setText("0");
						((TextView)cell.findViewById(R.id.tvCelPoCount)).setText("0");
						
						// セルの日付をセット
						((TextView) cell.findViewById(R.id.tvCellDay))
								.setText(String.valueOf(CalendarUtil.getDate(cal)));
						// 付加情報として日付をセット
						cell.setTag(CalendarUtil.cal2str(cal));
						
						//その日のデータがないかチェックする
						Iterator<BaseForm> ite = list.iterator();
						while(ite.hasNext()){
							WcRecordForm f = (WcRecordForm)ite.next();
							if(CalendarUtil.cal2str(f.getWc_record_date()).equals(CalendarUtil.cal2str(cal))){
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
					} else {
						cell.setBackgroundColor(Color.GRAY);
						countDisable ++;
					}
					//全部別の月の場合行を隠す
					if(countDisable == 7){
						tr.setVisibility(View.GONE);
					}
					//次の日にする
					cal.add(Calendar.DATE, 1);
				}
			}
		}
		//処理前の月の初日に戻す
		cal.set(year, (month - 1), 1);

		if(doAmimation){
			AlphaAnimation alpha = new AlphaAnimation(0.5f, 1.0f);
			alpha.setDuration(300);
			tl.startAnimation(alpha);
		}
	}
	
	/**
	 * 選択中のセルの色を変える
	 */
	private void setSelectedColor(View cell){
		frm.setVisibility(View.GONE);
		//画面情報を取得
		Rect rect = new Rect();
		//ステータスバーの高さ
		getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		this.statusBarHeight = rect.top;
		//セルの座標取得
		int[] location = new int[2];
		cell.getLocationInWindow(location);
		//フレームの座標をセット
		frm.setX(location[0]);
		frm.setY(location[1] - this.statusBarHeight);
		//フレームの大きさをセルにあわせる
		frm.getLayoutParams().width = cell.getWidth();
		frm.getLayoutParams().height = cell.getHeight();
		//セルを表示する
		frm.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 前月ボタン押下
	 */
	private OnClickListener lsnrPrev = new OnClickListener() {
		@Override
		public void onClick(View v) {
			cal.add(Calendar.MONTH, -1);
			createCalendarView(getView(), cal, true);
		}
	};
	
	/**
	 * 翌月ボタン押下
	 */
	private OnClickListener lsnrNext = new OnClickListener() {
		@Override
		public void onClick(View v) {
			cal.add(Calendar.MONTH, 1);
			createCalendarView(getView(), cal, true);
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
							cal.add(Calendar.MONTH, -1);
						//左移動の場合
						} else {
							cal.add(Calendar.MONTH, 1);
						}
						//カレンダー再構築
						createCalendarView(getView(), cal, true);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				//TODO 入力用Fragmentに渡す処理を実装
				if(getTargetFragment() instanceof WcRecordInputFragment){ 
					((WcRecordInputFragment)getTargetFragment()).changeDate(1, CalendarUtil.str2cal(v.getTag().toString()));
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
		createCalendarView(getView(), cal, false);
	}
	
	/**
	 * 
	 * @param family_id
	 * @param wc_record_date
	 */
	public void changeDate(int family_id, Calendar wc_record_date){
		//TODO family_idの連携方法を検討
		boolean doAnimation = false;
		if(CalendarUtil.getMonth(wc_record_date) != CalendarUtil.getMonth(cal)){
			doAnimation = true;
		}
		createCalendarView(getView(), wc_record_date, doAnimation);
	}
}
