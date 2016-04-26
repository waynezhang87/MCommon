package com.waynezhang.mcommon.notification.download;

import java.io.File;

/**
 * Created by liuxiaofeng02 on 2015/6/26.
 */
public class DownloadBean {
    /** 下载资源名称 */
    String name;
    /** 下载链接 */
    String url;
    /** 资源字节数 */
    long size;
    /** 已经下载的字节数 */
    long loadedSize;
    /** 标记是否允许下载的boolean变量 */
    boolean enable;
    /** 存储地址 */
    String savePath;
    /** 下载速度(字节/秒) */
    double downloadSpeed;

    public DownloadBean(String name, String url, String savePath){
        this.name = name;
        this.url = url;
        this.savePath = savePath;
        this.size = this.loadedSize = 0l;
        this.enable = true;
    }

    public void resetStatus(){
        File file = new File(savePath);
        if(!file.exists()){
            enable = true;
            this.loadedSize = 0l;
        }
    }
}
