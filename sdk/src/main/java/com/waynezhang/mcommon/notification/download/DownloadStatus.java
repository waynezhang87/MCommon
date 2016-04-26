package com.waynezhang.mcommon.notification.download;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;

/**
 * Created by liuxiaofeng on 2015/11/9.
 */
public class DownloadStatus implements Parcelable{
    public final static int FAIL = -1;
    public final static int SUCCESS = 0;
    public final static int DOWNLOADING = 1;
    public final static int PAUSE = 2;
    public final static int CONTINUE = 3;
    public final static int CANCEL = 4;

    //下载状态码
    private int code;
    //状态描述
    private String msg;
    //下载地址
    private String url;
    //进度
    private int progress;
    //下载速度(字节/秒)
    private double downloadSpeed;
    /** 资源字节数 */
    private long size;
    /** 已经下载的字节数 */
    private long downloadedSize;

    public DownloadStatus(int code, String msg, String url, int progress){
        this.code = code;
        this.msg = msg;
        this.url = url;
        this.progress = progress;
    }

    public static final Parcelable.Creator<DownloadStatus> CREATOR = new Parcelable.Creator<DownloadStatus>()
    {
        public DownloadStatus createFromParcel(Parcel in)
        {
            return new DownloadStatus(in);
        }

        public DownloadStatus[] newArray(int size)
        {
            return new DownloadStatus[size];
        }
    };

    private DownloadStatus(Parcel in){
        code = in.readInt();
        msg = in.readString();
        url = in.readString();
        progress = in.readInt();
        downloadSpeed = in.readDouble();
        size = in.readLong();
        downloadedSize = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(code);
        out.writeString(msg);
        out.writeString(url);
        out.writeInt(progress);
        out.writeDouble(downloadSpeed);
        out.writeLong(size);
        out.writeLong(downloadedSize);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public double getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(double downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public String getDownloadSpeedString(){
        DecimalFormat df = new DecimalFormat("#.##");
        if(downloadSpeed >= 1024*1024){
            double speed = downloadSpeed /(1024*1024);
            return df.format(speed) + "MB/s";
        } else if(downloadSpeed >= 1024){
            double speed = downloadSpeed / 1024;
            return df.format(speed) + "KB/s";
        } else {
            return df.format(downloadSpeed) + "B/s";
        }
    }

    private String getFormatSize(long size){
        DecimalFormat df = new DecimalFormat("#.##");
        if(size >= 1024*1024){
            double result = (double)size /(1024*1024);
            return df.format(result) + "MB";
        } else if(size >= 1024){
            double result = (double)size / 1024;
            return df.format(result) + "KB";
        } else {
            return size + "B";
        }
    }

    public long getSize() {
        return size;
    }

    public String getSizeString(){
        return getFormatSize(size);
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDownloadedSize() {
        return downloadedSize;
    }

    public String getDownloadedSizeString(){
        return getFormatSize(downloadedSize);
    }

    public void setDownloadedSize(long downloadedSize) {
        this.downloadedSize = downloadedSize;
    }
}
