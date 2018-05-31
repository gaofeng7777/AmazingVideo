package com.amazing.video.gp.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazing.video.gp.R;
import com.amazing.video.gp.MyApplication;

public class AmazingToastUtils {

	public static void showAmazingToast(int resID) {
		showAmazingToast(MyApplication.getContext(), Toast.LENGTH_SHORT,
				resID);
	}

	public static void showAmazingToast(String text) {
		showAmazingToast(MyApplication.getContext(), Toast.LENGTH_SHORT, text);
	}

	public static void showAmazingToast(Context ctx, int resID) {
		showAmazingToast(ctx, Toast.LENGTH_SHORT, resID);
	}

	public static void showAmazingToast(Context ctx, String text) {
		showAmazingToast(ctx, Toast.LENGTH_SHORT, text);
	}

	public static void showAmazingLongToast(Context ctx, int resID) {
		showAmazingToast(ctx, Toast.LENGTH_LONG, resID);
	}

	public static void showAmazingLongToast(int resID) {
		showAmazingToast(MyApplication.getContext(), Toast.LENGTH_LONG, resID);
	}

	public static void showAmazingLongToast(Context ctx, String text) {
		showAmazingToast(ctx, Toast.LENGTH_LONG, text);
	}

	public static void showAmazingLongToast(String text) {
		showAmazingToast(MyApplication.getContext(), Toast.LENGTH_LONG, text);
	}

	public static void showAmazingToast(Context ctx, int duration, int resID) {
		showAmazingToast(ctx, duration, ctx.getString(resID));
	}

	/** Toast一个图片 */
	public static Toast showAmazingToastImage(Context ctx, int resID) {
		final Toast toast = Toast.makeText(ctx, "", Toast.LENGTH_SHORT);
		View mNextView = toast.getView();
		if (mNextView != null)
			mNextView.setBackgroundResource(resID);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
		return toast;
	}

	public static void showAmazingToast(final Context ctx, final int duration,
										final String text) {
		final Toast toast = Toast.makeText(ctx, text, duration);
		View view = RelativeLayout.inflate(ctx, R.layout.amazing_toast_layout, null);
		TextView mNextView = (TextView) view.findViewById(R.id.toast_name);
		toast.setView(view);
		mNextView.setText(text);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/** 在UI线程运行弹出 */
	public static void showAmazingToastOnUiThread(final Activity ctx, final String text) {
		if (ctx != null) {
			ctx.runOnUiThread(new Runnable() {
				public void run() {
					showAmazingToast(ctx, text);
				}
			});
		}
	}

}
