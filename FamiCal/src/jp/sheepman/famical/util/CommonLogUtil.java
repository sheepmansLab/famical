package jp.sheepman.famical.util;

import android.util.Log;

public class CommonLogUtil {
	
	public static void methodStart(){
		method(" : start");
	}
	
	
	private static void method(String additional){
		debug(new Throwable().getStackTrace()[0].getClassName()
			, new Throwable().getStackTrace()[0].getMethodName() + additional);
	}
	
	
	private static void debug(String tag, String msg){
		Log.d(tag, msg);
	}
}
