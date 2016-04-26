/*
 * Copyright (c) 2013 Shanda Corporation. All rights reserved.
 *
 * Created on 2013-9-13.
 */

package com.waynezhang.mcommon.network;

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

import javax.net.ssl.SSLException;

/**
 * 文件下载工具类.
 * 
 * @author James Shen
 */
@SuppressLint("HandlerLeak")
public class FileDownloader {

	private String fileUrl;
	private String savePath;
	private IDownloadProgress progressOutput;
	private FileFetch fetch;

	public FileDownloader() {
	}

	public boolean isFinished() {
		if (fetch == null) {
			return true;
		}
		return fetch.isStop();
	}

	public boolean hasError() {
		if (fetch == null) {
			return false;
		}
		return fetch.hasError();
	}

	/**
	 * @param fileUrl
	 *            文件下载链接地址
	 */
	public final void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	/**
	 * @param savePath
	 *            文件保存地址，使用绝对路径
	 */
	public final void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public final String getSavePath() {
		return this.savePath;
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
		new Thread(new Runnable() {

			@Override
			public void run() {
				fetch = new FileFetch(fileUrl, savePath);
				fetch.setFileEnd(getFileSize());
				new Thread(fetch).start();
				while (!fetch.isStop()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					output.sendEmptyMessage(0);
				}
				output.sendEmptyMessage(0);
			}
		}).start();
	}

	public void stop() {
		fetch.stop();
	}

	private long getFileSize() {
		long fileLength = -1;
		try {
			HttpEntity entity = getHttpEntity(fileUrl, false);
			fileLength = entity.getContentLength();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileLength;
	}

	private void deleteFile() {
		File file = new File(savePath);
		if (file.exists()) {
			file.delete();
		}
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
				int progress = (int) ((fetch.getFileStart() * 100) / fetch.getFileEnd());
				if (fetch.isStop()) {
					isFinished = true;
					if (!fetch.hasError()) {
						progressOutput.downloadProgress(100);
						progressOutput.downloadSuccess();
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

	public interface IDownloadProgress {

		void downloadProgress(int progress);

		void downloadSuccess();

		void downloadFailure();
	}
}
