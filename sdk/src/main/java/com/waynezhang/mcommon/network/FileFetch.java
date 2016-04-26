/*
 * Copyright (c) 2013 Shanda Corporation. All rights reserved.
 *
 * Created on 2013-9-13.
 */

package com.waynezhang.mcommon.network;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

/**
 * 下载HTTP文件.
 * 
 * @author Liu Yagang
 */
public class FileFetch implements Runnable {

	private String fileUrl;
	private String savePath;
	private boolean stop = false;
	private boolean hasError = false;
	private long fileStart;
	private long fileEnd;

	public FileFetch(String fileUrl, String savePath) {
		this.fileUrl = fileUrl;
		this.savePath = savePath;
	}

	@Override
	public void run() {
		FileAccess fileAccess = new FileAccess();
		while (!this.stop) {
			InputStream input = null;
			int responseCode = 0;
			try {
				try {
					HttpGet httpGet = new HttpGet(fileUrl);
					HttpClient httpClient = new DefaultHttpClient();
					// if (downloader.showProgress()) {
					// String property = "bytes=" + fileStart + "-" + fileEnd;
					// httpGet.addHeader("Range", property);
					// }
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
						break;
					}
					if (this.stop) {
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (input == null) {
					continue;
				}
				int size;
				byte[] buffer = new byte[1024];
				do {
					size = input.read(buffer, 0, buffer.length);
					if (size != -1) {
						this.fileStart += fileAccess.write(buffer, 0, size);
					}
				} while (size > -1 && !stop);
				this.stop = true;
			} catch (SocketTimeoutException e) {
				this.stop = true;
				this.hasError = true;
			} catch (IOException e) {
				this.stop = true;
				this.hasError = true;
			} catch (Exception e) {
				this.stop = true;
				this.hasError = true;
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (Exception e) {
					}
				}
			}
		}
		fileAccess.close();
	}

	public final long getFileStart() {
		return fileStart;
	}

	public final void setFileStart(long fileStart) {
		this.fileStart = fileStart;
	}

	public final long getFileEnd() {
		return fileEnd;
	}

	public final void setFileEnd(long fileEnd) {
		this.fileEnd = fileEnd;
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

	final class FileAccess {

		private FileOutputStream outStream = null;

		public FileAccess() {
			try {
				// 只能保存在程序的files目录下, 如果放在files的子文件夹下, 会出现读取权限的问题
				outStream = new FileOutputStream(savePath, true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		public synchronized int write(byte[] b, int start, int len) throws IOException {
			outStream.write(b, start, len);
			return len;
		}

		public void close() {
			try {
				if (outStream != null) {
					outStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				outStream = null;
			}
		}
	}
}
