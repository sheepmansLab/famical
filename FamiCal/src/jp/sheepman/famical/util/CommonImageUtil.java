package jp.sheepman.famical.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import jp.sheepman.famical.R;

public class CommonImageUtil {

	/**
	 * ImageViewの画像データをbyte配列にして返却する
	 * @return byte配列
	 */
	public static byte[] getImageByteArray(BitmapDrawable bitmapDrawable){
		CommonLogUtil.method_start();
		byte[] data = null;
		if(bitmapDrawable != null && bitmapDrawable.getBitmap() != null){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos);
			data = baos.toByteArray();
		}
		CommonLogUtil.method_end();
		return data;
	}

	/**
	 * byte配列をBitmapにして返却
	 * @param data byte配列
	 * @return Bitmapデータ
	 */
	public static Bitmap convertByte2Bitmap(byte[] data){
		Bitmap bmp = null;
		if(data != null && data.length > 0){
			bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		}
		return bmp;
	}
}
