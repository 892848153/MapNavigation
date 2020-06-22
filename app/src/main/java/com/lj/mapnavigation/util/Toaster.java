package com.lj.mapnavigation.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.text.MessageFormat;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * 显示Toast的类.
 * 
 * 不管在主线程还是在子线程中运行此类中的方法，都不会报错。因为该类的方法都将运行在主线程中.
 * 
 * @time 2014年7月31日 下午5:59:04
 * @author jie.liu
 */
public class Toaster {

	private static void show(final Activity activity, final int resId,
			final int duration) {
		if (activity == null)
			return;

		final Context context = activity.getApplication();
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, resId, duration).show();
			}
		});
	}

	private static void show(final Activity activity, final String message,
			final int duration) {
		if (activity == null)
			return;
		if (TextUtils.isEmpty(message))
			return;

		final Context context = activity.getApplication();
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, message, duration).show();
			}
		});
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
	 * 
	 * @param activity
	 * @param resId
	 */
	public static void showLong(final Activity activity, int resId) {
		show(activity, resId, LENGTH_LONG);
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
	 * 
	 * @param activity
	 * @param resId
	 */
	public static void showShort(final Activity activity, final int resId) {
		show(activity, resId, LENGTH_SHORT);
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
	 * 
	 * @param activity
	 * @param message
	 */
	public static void showLong(final Activity activity, final String message) {
		show(activity, message, LENGTH_LONG);
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
	 * 
	 * @param activity
	 * @param message
	 */
	public static void showShort(final Activity activity, final String message) {
		show(activity, message, LENGTH_SHORT);
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
	 * 
	 * @param activity
	 * @param message
	 * @param args
	 */
	public static void showLong(final Activity activity, final String message,
			final Object... args) {
		String formatted = MessageFormat.format(message, args);
		show(activity, formatted, LENGTH_LONG);
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
	 * 
	 * @param activity
	 * @param message
	 * @param args
	 */
	public static void showShort(final Activity activity, final String message,
			final Object... args) {
		String formatted = MessageFormat.format(message, args);
		show(activity, formatted, LENGTH_SHORT);
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
	 * 
	 * @param activity
	 * @param resId
	 * @param args
	 */
	public static void showLong(final Activity activity, final int resId,
			final Object... args) {
		if (activity == null)
			return;

		String message = activity.getString(resId);
		showLong(activity, message, args);
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
	 * 
	 * @param activity
	 * @param resId
	 * @param args
	 */
	public static void showShort(final Activity activity, final int resId,
			final Object... args) {
		if (activity == null)
			return;

		String message = activity.getString(resId);
		showShort(activity, message, args);
	}
}
