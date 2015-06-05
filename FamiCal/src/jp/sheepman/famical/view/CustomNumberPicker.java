package jp.sheepman.famical.view;

import jp.sheepman.famical.R;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.androidquery.AQuery;

public class CustomNumberPicker extends LinearLayout {
	private AQuery aq;
	
	/**
	 * 初期化処理
	 * @param context
	 * @param attrs
	 */
	public CustomNumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(!isInEditMode()){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			View view = inflater.inflate(R.layout.layout_numpick_view, null);
			this.aq = new AQuery(view);
			aq.id(R.id.btnNumpickUp).clicked(lsnrButtonUp);
			aq.id(R.id.btnNumpickDown).clicked(lsnrButtonDown);
			setValue(0);
			addView(view);
		}
	}
	
	/**
	 * Upボタン押下時
	 */
	OnClickListener lsnrButtonUp = new OnClickListener() {
		@Override
		public void onClick(View v) {
			addNumber(1);
		}
	};
	
	/**
	 * Downボタン押下時イベント
	 */
	OnClickListener lsnrButtonDown = new OnClickListener() {
		@Override
		public void onClick(View v) {
			addNumber(-1);
		}
	};

	/**
	 * 値を取得する
	 * @return
	 */
	public int getValue(){
		return Integer.valueOf(aq.id(R.id.etCount).getText().toString());
	}
	
	/**
	 * 値をセットする
	 * @param value
	 */
	public void setValue(int value){
		if(value < 0){
			value = 0;
		}
		aq.id(R.id.etCount).text(String.valueOf(value));
	}
	
	/**
	 * カウンタに値を加算する
	 * @param num	加算値
	 */
	private void addNumber(int num){
		int value = Integer.valueOf(aq.id(R.id.etCount).getText().toString());
		value = value + num;
		setValue(value);
	}
}
