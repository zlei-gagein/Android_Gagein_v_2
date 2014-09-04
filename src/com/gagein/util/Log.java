package com.gagein.util;

/**
 * Log调试
 * @author silen
 */
public final class Log {

	private static boolean mDebug = false;

	public static void init(final boolean debug) {
		mDebug = debug;
	}
	
	public static void e(final String tag,final String msg){
		if (mDebug) {
			android.util.Log.e(tag, msg);
		}
	}
	
	public static void e(final String tag,final String msg,final Throwable tr){
		if (mDebug) {
			android.util.Log.e(tag, msg,tr);
		}
	}
	
	public static void w(final String tag,final String msg){
		if (mDebug) {
			android.util.Log.w(tag, msg);
		}
	}
	
	public static void w(final String tag,final String msg,final Throwable tr){
		if (mDebug) {
			android.util.Log.w(tag, msg,tr);
		}
	}
	
	public static void d(final String tag,final String msg){
		if (mDebug) {
			android.util.Log.d(tag, msg);
		}
	}
	
	public static void d(final String tag,final String msg,final Throwable tr){
		if (mDebug) {
			android.util.Log.d(tag, msg,tr);
		}
	}
	
	public static void v(final String tag,final String msg){
		if (mDebug) {
			android.util.Log.v(tag, msg);
		}
	}
	
	public static void v(final String tag,final String msg,final Throwable tr){
		if (mDebug) {
			android.util.Log.v(tag, msg,tr);
		}
	}
	
	public static void i(final String tag,final String msg){
		if (mDebug) {
			android.util.Log.i(tag, msg);
		}
	}
	
	public static void i(final String tag,final String msg,final Throwable tr){
		if (mDebug) {
			android.util.Log.i(tag, msg,tr);
		}
	}
}
