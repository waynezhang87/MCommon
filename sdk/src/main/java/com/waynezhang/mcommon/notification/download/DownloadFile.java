package com.waynezhang.mcommon.notification.download;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLException;

/**
 * Created by liuxiaofeng02 on 2015/6/25.
 */
public class DownloadFile {
    private boolean stop = false;
    private boolean hasError = false;
    private DownloadBean downloadBean;
    private IDownloadProgress progressOutput;

    public DownloadFile(DownloadBean bean) {
        this.downloadBean = bean;
    }

    /**
     * @param progressOutput
     *            下载进度输出类
     */
    public final void setProgressOutput(IDownloadProgress progressOutput) {
        if (progressOutput != null) {
            this.progressOutput = progressOutput;
        }
    }

    public void start() {
        final ProgressOutput output = new ProgressOutput(Looper.getMainLooper());
        RandomAccessFile randomFile = null;
        FileOutputStream fos = null;
        while (!this.stop) {
            InputStream input = null;
            int responseCode = 0;
            try {
                if(downloadBean.size <= 0){
                    downloadBean.size = getFileSize();
                }
                HttpGet httpGet = new HttpGet(downloadBean.url);
                HttpClient httpClient = new DefaultHttpClient();
                if (downloadBean.loadedSize > 0) {
                    //断点续传方式
                    String property = "bytes=" + downloadBean.loadedSize + "-" + (downloadBean.size - 1);
                    httpGet.addHeader("Range", property);
                    if(randomFile == null){
                        randomFile = new RandomAccessFile(downloadBean.savePath, "rw");
                        randomFile.setLength(downloadBean.size);
                    }
                } else if(fos == null){
                    // 采用普通的下载方式
                    File file = new File(downloadBean.savePath);
                    if (file.getParentFile().exists() == false) {
                        file.getParentFile().mkdirs();
                    }
                    if (file.exists() == false) {
                        file.createNewFile();
                    }
                    fos = new FileOutputStream(file);
                }

                HttpResponse response = httpClient.execute(httpGet);
                responseCode = response.getStatusLine().getStatusCode();
                switch (responseCode) {
                    case HttpStatus.SC_OK:
                    case HttpStatus.SC_CREATED:
                    case HttpStatus.SC_ACCEPTED:
                    case HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION:
                    case HttpStatus.SC_NO_CONTENT:
                    case HttpStatus.SC_RESET_CONTENT:
                    case HttpStatus.SC_PARTIAL_CONTENT:
                    case HttpStatus.SC_MULTI_STATUS:
                        input = response.getEntity().getContent();
                        break;
                    default:
                        this.stop = true;
                        this.hasError = true;
                        output.sendEmptyMessage(0);
                        break;
                }
                if (this.stop) {
                    break;
                }
                if (input == null) {
                    continue;
                }
                int previousProgress = 0;
                long downloadSizeInSpeedReport = 0;
                long previousReportSpeedTime = System.currentTimeMillis();
                int size;
                byte[] buffer = new byte[1024];
                do {
                    size = input.read(buffer, 0, buffer.length);

                    //下载速度统计，每间隔3秒计算1次
                    long nowTime = System.currentTimeMillis();
                    double intervalTime = (nowTime - previousReportSpeedTime)/(double)1000;
                    if(size != -1){
                        downloadSizeInSpeedReport += size;
                    }
                    if(intervalTime >= 2){
                        downloadBean.downloadSpeed = downloadSizeInSpeedReport / intervalTime;
                        downloadSizeInSpeedReport = 0;
                        previousReportSpeedTime = nowTime;
                    }

                    if (size != -1) {
                        if(fos != null){
                            fos.write(buffer, 0, size);
                        } else if(randomFile != null){
                            randomFile.seek(downloadBean.loadedSize);
                            randomFile.write(buffer, 0, size);
                        }
                        downloadBean.loadedSize += size;
                        int progress = (int)(downloadBean.loadedSize * 100 / downloadBean.size);
                        if(progress != previousProgress){
                            previousProgress = progress;
                            output.sendEmptyMessage(0);
                        }
                    }
                } while (size > -1 && !stop && downloadBean.enable);
                this.stop = true;
                output.sendEmptyMessage(0);
            } catch (SocketTimeoutException e) {
                this.stop = true;
                this.hasError = true;
                output.sendEmptyMessage(0);
            } catch (IOException e) {
                this.stop = true;
                this.hasError = true;
                output.sendEmptyMessage(0);
            } catch (Exception e) {
                this.stop = true;
                this.hasError = true;
                output.sendEmptyMessage(0);
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                    if (randomFile != null) {
                        randomFile.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private long getFileSize() {
        long fileLength = -1;
        try {
            HttpEntity entity = getHttpEntity(downloadBean.url, false);
            fileLength = entity.getContentLength();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileLength;
    }

    public static HttpEntity getHttpEntity(String netAddress, boolean isZip) throws Exception {
        try {
            HttpGet httpGet = new HttpGet(netAddress);
            HttpClient httpClient = new DefaultHttpClient();
            if (isZip) {
                httpGet.addHeader("Accept-Encoding", "gzip");
            }
            HttpResponse response = httpClient.execute(httpGet);
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                return entity;
            } else {
                throw new Exception("Network Exception, ErrorCode: " + code);
            }
        } catch (SSLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class ProgressOutput extends Handler {
        private boolean isFinished = false;

        @SuppressLint("HandlerLeak")
        public ProgressOutput(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (progressOutput == null || isFinished) {
                return;
            }
            try {
                int progress = (int) ((downloadBean.loadedSize * 100) / downloadBean.size);
                if (isStop()) {
                    isFinished = true;
                    if (!hasError()) {
                        //进度100则为完成，否则是用户暂停，这时不给回调，不用刷新通知状态
                        if(progress == 100){
                            progressOutput.downloadSuccess();
                        }
                    } else if (progress > 100) {
                        deleteFile();
                        progressOutput.downloadFailure();
                    } else {
                        progressOutput.downloadFailure();
                    }
                } else {
                    progressOutput.downloadProgress(progress);
                }
            } catch (Exception e) {
                progressOutput.downloadFailure();
            }
        }
    }

    private void deleteFile() {
        File file = new File(downloadBean.savePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public final boolean isStop() {
        return this.stop;
    }

    public final void stop() {
        stop = true;
        this.hasError = true;
    }

    public final boolean hasError() {
        return hasError;
    }

    public interface IDownloadProgress {

        void downloadProgress(int progress);

        void downloadSuccess();

        void downloadFailure();
    }
}
