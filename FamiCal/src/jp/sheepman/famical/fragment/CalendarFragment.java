package jp.sheepman.famical.fragment;

import java.util.Calendar;
import java.util.Date;

import jp.sheepman.common.fragment.BaseFragment;
import jp.sheepman.common.util.CalendarUtil;
import jp.sheepman.famical.R;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CalendarFragment extends BaseFragment {
	private LayoutInflater inflator;
	private Calendar cal;

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
		View v = inflator.inflate(R.layout.fragment_calebdar, null);
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
		
		// ヘッダをセット
		LinearLayout llHeader = (LinearLayout) v.findViewById(R.id.llCalendarHeader);
		TextView tvCalPrev = (TextView) llHeader.findViewById(R.id.tvCalPrev);
		TextView tvCalNext = (TextView) llHeader.findViewById(R.id.tvCalNext);
		TextView tvCalMonth = (TextView) llHeader.findViewById(R.id.tvCalMonth);
		tvCalMonth.setText(year + "年 " + month + "月");
		tvCalPrev.setText("←");
		tvCalPrev.setOnClickListener(lsnrPrev);
		tvCalNext.setText("→");
		tvCalNext.setOnClickListener(lsnrNext);

		//データ取得
//		EventSelectModel model = new EventSelectModel();
//		List<EventCalendarForm> list = model.selectForCalendar(getActivity()
//													, String.format("%1$02d", year)
//													, String.format("%1$02d", month));
		
		// テーブル作成
		TableLayout tl = (TableLayout) v.findViewById(R.id.tlCalendar);
		
		//Cell用のLayoutParamsを作成
		TableRow.LayoutParams p = new TableRow.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,
				TableRow.LayoutParams.MATCH_PARENT, 1);
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
					View cell = inflator.inflate(R.layout.layout_calendar_cell,
							null);
					// 行にViewを追加
					tr.addView(cell, p);
					// 前月、次月の場合は対象外
					if (month == CalendarUtil.getMonth(cal)) {
						// セルの日付をセット
						((TextView) cell.findViewById(R.id.tvCellDay))
								.setText(String.valueOf(CalendarUtil.getDate(cal)));
						// 付加情報として日付をセット
						cell.setTag(CalendarUtil.cal2str(cal));
						//その日のデータがないかチェックする
//						for(EventCalendarForm f : list){
//							if(f.getEventDate().equals(CalendarUtil.cal2str(cal))){
//								((ImageView)cell.findViewById(R.id.ivCellIcon)).setImageResource(R.drawable.evereco_mark);
//								//TODO 用済みのオブジェクトは消したいが、順序がくるってエラーを吐く
//								//list.remove(f);
//							}
//						}
						//土日に色を設定する
						if(j == 6){
							cell.setBackgroundColor(getResources().getColor(R.color.crimson));
							cell.setBackgroundResource(R.drawable.calendar_cell_sat);
						} else if(j == 0) {
							cell.setBackgroundResource(R.drawable.calendar_cell_sun);
						}
					} else {
						cell.setBackgroundColor(Color.GRAY);
						countDisable ++;
					}
					// 押下時のイベントをセット
					cell.setOnTouchListener(lsnrTouch);
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
	
	OnTouchListener lsnrTouch = new OnTouchListener() {
		private final float adjust = 50;
		
		private float x1;
		private float x2;
		private float y1;
		private float y2;
		//
		private boolean flg_move = false;

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
				Bundle args = new Bundle();
				args.putString("event_date", v.getTag().toString());
//				dialog.setArguments(args);
//				dialog.setTargetFragment(EventCalendarFragment.this, 0);
//				dialog.show(getFragmentManager(), "dalog");
				flg_move = false;
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
}
