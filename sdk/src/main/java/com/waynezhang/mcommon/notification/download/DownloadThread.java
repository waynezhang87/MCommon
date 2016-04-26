package com.waynezhang.mcommon.notification.download;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.concurrent.Future;

public class DownloadThread implements Runnable, DownloadFile.IDownloadProgress{
    public static final String RECEIVER_ACTION = "android.intent.action.DOWNLOAD_RECEIVER_ACTION";
    public static final String DOWNLOAD_STATUS = "DOWNLOAD_STATUS";
	private DownloadBean bean;
	private Context context;
	private DownloadNotification notification;
	private Future<?> result;
    /** 下载状态
     * {@link com.waynezhang.mcommon.notification.download.DownloadStatus}
     * */
    int status;
	
	public DownloadThread(DownloadBean bean, Context context, DownloadNotification notification){
		this.bean = bean;
		this.context = context;
		this.notification = notification;
	}

	@Override
	public void run() {
		DownloadFile downloadFile = new DownloadFile(bean);
        downloadFile.setProgressOutput(this);
        downloadFile.start();
    }

    public DownloadBean getBean() {
        return bean;
    }

    public void resetStatus(){
        bean.resetStatus();
    }

    public boolean isFinished(){
        File apkFile = new File(bean.savePath);
        if(apkFile.exists() && bean.loadedSize == bean.size && bean.loadedSize > 0){
            return true;
        }
        return  false;
    }
	
	public void pause(){
		bean.enable = false;
        status = DownloadStatus.PAUSE;
		notification.showContinueStatus();

        sendBroadcast(new DownloadStatus(DownloadStatus.PAUSE, "暂停下载", bean.url, 0));
	}
	
	public void resume(){
        bean.enable = true;
        status = DownloadStatus.DOWNLOADING;
		notification.showPauseStatus();

        sendBroadcast(new DownloadStatus(DownloadStatus.CONTINUE, "继续下载", bean.url, 0));
	}
	
	public void cancel(){
		bean.enable = false;
        status = DownloadStatus.CANCEL;
		result.cancel(true);
		notification.changeNotificationText("已取消下载！");

        sendBroadcast(new DownloadStatus(DownloadStatus.CANCEL, "取消下载", bean.url, 0));
	}

	public Future<?> getResult() {
		return result;
	}

	public void setResult(Future<?> result) {
		this.result = result;
	}

    @Override
    public void downloadProgress(int progress) {
        status = DownloadStatus.DOWNLOADING;
        notification.changeProgressStatus(progress);

        DownloadStatus status = new DownloadStatus(DownloadStatus.DOWNLOADING ,"下载中...", bean.url, progress);
        status.setDownloadSpeed(bean.downloadSpeed);
        sendBroadcast(status);
    }

    @Override
    public void downloadSuccess() {
        status = DownloadStatus.SUCCESS;
        File apkFile = new File(bean.savePath);
        Uri uri = Uri.fromFile(apkFile);
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        PendingIntent updatePendingIntent = PendingIntent.getActivity(context, 0, installIntent, 0);
        notification.changeContentIntent(updatePendingIntent);
        notification.changeNotificationText("下载完成，请点击安装！");

        if(bean.savePath.endsWith(".apk")){
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(installIntent);
        }

//        Intent intent=new Intent(context, DownloadService.class);
//        intent.putExtra(DownloadService.KEY_URL, bean.url);
//        intent.putExtra(DownloadService.KEY_DOWNLOAD_ACTION, DownloadAction.SUCCESS);
//        context.startService(intent);

        sendBroadcast(new DownloadStatus(DownloadStatus.SUCCESS ,"下载完成", bean.url, 100));
    }

    @Override
    public void downloadFailure() {
        bean.enable = false;
        status = DownloadStatus.FAIL;
        String msg = "网络故障，文件下载失败！";
        notification.changeNotificationText(msg);
        sendBroadcast(new DownloadStatus(DownloadStatus.FAIL ,"下载失败", bean.url, 0));
    }

    /**
     * 发送特定action的广播
     * @param status
     */
    private void sendBroadcast(DownloadStatus status){
        status.setSize(bean.size);
        status.setDownloadedSize(bean.loadedSize);

        Intent intent = new Intent();
        intent.setAction(RECEIVER_ACTION);
        intent.putExtra(DOWNLOAD_STATUS, status);
        intent.setPackage(context.getApplicationContext().getPackageName());
        context.sendBroadcast(intent);
    }
}
