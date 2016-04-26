package com.waynezhang.mcommon.xwidget;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.waynezhang.mcommon.util.ThreadUtil;

public class LoadingHelper {
	private static final String TAG = LoadingHelper.class.getSimpleName();
	private static LoadingHandler loadingHandler;

	private LoadingHelper() {

	}

	public static void showLoading(final Context context, final int layoutId, final int iconViewId, final int animationId) {
		if (loadingHandler != null) {
			return;
		}
		ThreadUtil.runOnUiThread(context, new Runnable() {

			@Override
			public void run() {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View contentView = inflater.inflate(layoutId, null);
				View iconView = contentView.findViewById(iconViewId);
				iconView.setBackgroundResource(animationId);
				Animatable animation = (Animatable) iconView.getBackground();

				loadingHandler = new LoadingHandler(context, contentView, animation);
				loadingHandler.showLoading();
			}

		});
	}

	public static void showLoading(Context context, View contentView, Animatable animation) {
		if (loadingHandler != null) {
			return;
		}

		loadingHandler = new LoadingHandler(context, contentView, animation);
		loadingHandler.showLoading();
	}

	public static void hideLoading() {
		if (loadingHandler != null) {
			loadingHandler.hideLoading();
			loadingHandler = null;
		}
	}

	static class LoadingHandler extends Handler {
		Context context;
		ProgressDialog progressDialog;
		View contentView;
		Animatable animation;
		Runnable callback;

		public LoadingHandler(Context context, View contentView, Animatable animation) {
			super(context.getMainLooper());

			this.context = context;
			this.contentView = contentView;
			this.animation = animation;
		}

		public void showLoading() {

			this.postDelayed(callback = new Runnable() {

				@Override
				public void run() {
					Log.d(TAG, "showLoading");

					if (progressDialog == null) {
						progressDialog = new ProgressDialog(context);
					}
					progressDialog.show();
					progressDialog.setContentView(contentView);
					contentView.post(new Runnable() {
						@Override
						public void run() {
							animation.start();
						}
					});
				}

			}, 200);
		}

		public void hideLoading() {
			this.removeCallbacks(callback);

			if (progressDialog != null) {
				Log.d(TAG, "hideLoading");

				progressDialog.hide();
			}
		}
	}
}
