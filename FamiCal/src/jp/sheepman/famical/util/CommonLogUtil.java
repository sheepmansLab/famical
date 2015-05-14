package jp.sheepman.famical.util;

import android.util.Log;

public class CommonLogUtil{
	public static void method_start(){
		Throwable t = new Throwable();
		StackTraceElement e = t.getStackTrace()[2];
		Log.d(e.getClassName(), e.getMethodName() + ": start");
	}
	
	public static void method_end(){
		Throwable t = new Throwable();
		StackTraceElement e = t.getStackTrace()[2];
		Log.d(e.getClassName(), e.getMethodName() + ": end");
	}
}
