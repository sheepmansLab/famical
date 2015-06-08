package jp.sheepman.famical.view;

import jp.sheepman.famical.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
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

            if(attrs != null){
//                TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomNumberPicker);
//                //幅をセット
//                int width = typedArray.getDimensionPixelSize(R.attr.width, 0);
//                setWidth(R.id.etNumpickCount, width);
//                setWidth(R.id.btnNumpickDown, width);
//                setWidth(R.id.btnNumpickUp, width);
//                //高さをセット
//                int counter_height = typedArray.getDimensionPixelSize(R.attr.counter_height, 0);
//                int button_height = typedArray.getDimensionPixelSize(R.attr.button_height, 0);
//                setHeight(R.id.etNumpickCount, counter_height);
//                setHeight(R.id.btnNumpickDown, button_height);
//                setHeight(R.id.btnNumpickUp, button_height);
            }
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
		return Integer.valueOf(aq.id(R.id.etNumpickCount).getText().toString());
	}
	
	/**
	 * 値をセットする
	 * @param value
	 */
	public void setValue(int value){
		if(value < 0){
			value = 0;
		}
		aq.id(R.id.etNumpickCount).text(String.valueOf(value));
	}
	
	/**
	 * カウンタに値を加算する
	 * @param num	加算値
	 */
	private void addNumber(int num){
		int value = Integer.valueOf(aq.id(R.id.etNumpickCount).getText().toString());
		value = value + num;
		setValue(value);
	}

    /**
     * リソースの高さをセットする
     * @param id    リソースID(R.id)
     * @param pixcel   pixcel
     */
    private void setHeight(int id, int pixcel){
        if(pixcel > 0){
            aq.id(id).height(pixcel, false);
        }
    }
    /**
     * リソースの幅をセットする
     * @param id    リソースID(R.id)
     * @param pixcel   pixcel
     */
    private void setWidth(int id, int pixcel){
        if(pixcel > 0){
            aq.id(id).width(pixcel, false);
        }
    }
}
