package com.waynezhang.mcommon.notification.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.waynezhang.mcommon.R;

public class DownloadNotification {
	public static final int ACTION_EVENT_COUNT = 3;
	private Context mContext;
	Notification notification; // notification
	private NotificationManager nm;
	private String titleStr;
	private PendingIntent contentIntent;
	private int notificationID;
	private String url;

	public DownloadNotification(Context context, PendingIntent contentIntent, int id, String url) {
		mContext = context;
		notificationID = id;
		this.contentIntent = contentIntent;
		this.url = url;
		this.nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public void showCustomizeNotification(int icoId, String titleStr) {
		this.titleStr = titleStr;

		RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.mc_download_notification);
		remoteView.setImageViewResource(R.id.mc_image, icoId);
		remoteView.setTextViewText(R.id.mc_tv_name, titleStr);
		remoteView.setProgressBar(R.id.mc_progressbar, 100, 0, false);

        // 如果版本号低于（3。0），那么不显示按钮
		if (getSystemVersion() <= 9) {
			remoteView.setViewVisibility(R.id.mc_iv_pause, View.GONE);
			remoteView.setViewVisibility(R.id.mc_iv_continue, View.GONE);
			remoteView.setViewVisibility(R.id.mc_iv_cancel, View.GONE);
		} else {
			remoteView.setViewVisibility(R.id.mc_iv_pause, View.VISIBLE);
			remoteView.setViewVisibility(R.id.mc_iv_continue, View.GONE);
			remoteView.setViewVisibility(R.id.mc_iv_cancel, View.VISIBLE);
		}

		Intent pauseButtonIntent = new Intent(mContext, DownloadService.class);
		pauseButtonIntent.putExtra(DownloadService.KEY_URL, url);
		pauseButtonIntent.putExtra(DownloadService.KEY_DOWNLOAD_ACTION, DownloadAction.PAUSE);
		PendingIntent pauseIntent = PendingIntent.getService(mContext, notificationID, pauseButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteView.setOnClickPendingIntent(R.id.mc_iv_pause, pauseIntent);

		Intent continueButtonIntent = new Intent(mContext, DownloadService.class);
		continueButtonIntent.putExtra(DownloadService.KEY_URL, url);
		continueButtonIntent.putExtra(DownloadService.KEY_DOWNLOAD_ACTION, DownloadAction.CONTINUE);
		PendingIntent continueIntent = PendingIntent.getService(mContext, notificationID + 1, continueButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteView.setOnClickPendingIntent(R.id.mc_iv_continue, continueIntent);

		Intent cancelButtonIntent = new Intent(mContext, DownloadService.class);
		cancelButtonIntent.putExtra(DownloadService.KEY_URL, url);
		cancelButtonIntent.putExtra(DownloadService.KEY_DOWNLOAD_ACTION, DownloadAction.CANCEL);
		PendingIntent cancelIntent = PendingIntent.getService(mContext, notificationID + 2, cancelButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteView.setOnClickPendingIntent(R.id.mc_iv_cancel, cancelIntent);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
		builder.setWhen(System.currentTimeMillis())
				.setContent(remoteView)
				.setContentIntent(getDefaultIntent(0))
				.setPriority(Notification.PRIORITY_DEFAULT)
				.setOngoing(true)
				.setSmallIcon(icoId);
		notification = builder.build();
		notification.icon = icoId;
		notification.contentView = remoteView;

		nm.notify(notificationID, notification);
	}

    /**
     * 获取当前系统SDK版本号
     */
    public static int getSystemVersion(){
		/*获取当前系统的android版本号*/
        int version= android.os.Build.VERSION.SDK_INT;
        return version;
    }

	public void changeProgressStatus(int p) {
		if (notification.contentView != null) {
			if (p == DownloadAction.FAIL)
				notification.contentView.setTextViewText(R.id.mc_tv_progress, "下载失败！");
			else if (p == 100)
				notification.contentView.setTextViewText(R.id.mc_tv_progress, "下载完成，请点击安装！");
			else
				notification.contentView.setTextViewText(R.id.mc_tv_progress, "进度（" +p + "%）");
			notification.contentView.setProgressBar(R.id.mc_progressbar, 100, p, false);
		}
		nm.notify(notificationID, notification);
	}

	public void showPauseStatus() {
		if (notification.contentView != null) {
			notification.contentView.setViewVisibility(R.id.mc_iv_pause, View.VISIBLE);
			notification.contentView.setViewVisibility(R.id.mc_iv_continue, View.GONE);
		}
		nm.notify(notificationID, notification);
	}

	public void showContinueStatus() {
		if (notification.contentView != null) {
			notification.contentView.setViewVisibility(R.id.mc_iv_continue, View.VISIBLE);
			notification.contentView.setViewVisibility(R.id.mc_iv_pause, View.GONE);
		}
		nm.notify(notificationID, notification);
	}

	public void changeContentIntent(PendingIntent intent) {
		this.contentIntent = intent;
		notification.contentIntent = intent;
	}

	public void changeNotificationText(String content) {
        Notification n = notification.clone();
        n.flags = Notification.FLAG_AUTO_CANCEL & ~Notification.FLAG_ONGOING_EVENT;
        n.defaults = Notification.DEFAULT_SOUND;
        n.setLatestEventInfo(mContext, titleStr, content, contentIntent);

		nm.notify(notificationID, n);
	}

	public void removeNotification() {
		nm.cancel(notificationID);
	}

	public PendingIntent getDefaultIntent(int flags) {
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1, new Intent(), flags);
		return pendingIntent;
	}
}
