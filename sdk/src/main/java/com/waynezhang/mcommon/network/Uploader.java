package com.waynezhang.mcommon.network;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by liuzimao.sanders on 2015/8/11.
 */
public class Uploader extends AsyncTask<HttpResponse, Integer, String> {

    private final static String TAG = "Uploader";
    private String filePath;
    private long totalSize;
    private String requestUrl;
    private ProgressCallback _callback;

    // 保存在下载线程的状态，只可在下载线程写，在UI线程读
    private ResultCode _resultCode;
    private Exception _ex;

    public static interface OnErrorCallback {
        public void onFailure(ResultCode code, Exception e);
    }

    public static interface StringCallback extends OnErrorCallback {
        public void onResponse(String result);
    }

    public static interface ProgressCallback extends StringCallback {
        public void onProgressUpdate(Integer progress);
    }

    public static <T> void post(String filePath, String requestUrl, ProgressCallback callback) {
        Uploader post = new Uploader(filePath, requestUrl, callback);
        post.execute();
    }

    public Uploader(String filePath, String requestUrl, ProgressCallback callback) {
        this.filePath = filePath;
        this.requestUrl = requestUrl;
        _callback = callback;
        Log.d(TAG, "Uploader filePath=" + filePath + ", requestUrl=" + requestUrl);
    }

    @Override
    protected void onPreExecute() {
        // 更新前
        Log.d(TAG, "onPreExecute status=" + this.getStatus().toString());
    }

    @Override
    protected String doInBackground(HttpResponse... arg0) {
        Log.d(TAG, "doInBackground");
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext httpContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(requestUrl);

        try {
            UploaderMultiPartEntity multipartContent = new UploaderMultiPartEntity(new UploaderMultiPartEntity.ProgressListener() {
                @Override
                public void transferred(long num) {
                    publishProgress((int) ((num / (float) totalSize) * 100));
                }
            });

            // We use FileBody to transfer an image
            File fTmp = new File(filePath);
            Log.d(TAG, "doInBackground filePath=" + filePath + ",name" + fTmp.getName());
            multipartContent.addPart("file", new FileBody(fTmp, "image/jpeg", "utf-8"));

            totalSize = multipartContent.getContentLength();

            // Send it
            httpPost.setEntity(multipartContent);
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            String serverResponse = EntityUtils.toString(response.getEntity());

            Log.d(TAG, "doInBackground serverResponse=" + serverResponse);
            return serverResponse;
        } catch (UnknownHostException e) {
            Log.d(TAG, "doInBackground UnknownHostException ex=" + e.toString());
            _resultCode = ResultCode.NetworkException;
            _ex = e;
        } catch (ClientProtocolException e) {
            Log.d(TAG, "doInBackground ClientProtocolException ex=" + e.toString());
            _resultCode = ResultCode.DefaultException;
            _ex = e;
        } catch (IOException e) {
            Log.d(TAG, "doInBackground IOException ex=" + e.toString());
            _resultCode = ResultCode.NetworkException;
            _ex = e;
        } catch (Exception e) {
            Log.d(TAG, "doInBackground Exception ex=" + e.toString());
            _resultCode = ResultCode.DefaultException;
            _ex = e;
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        // 更新过程中...
        Log.d(TAG, "onProgressUpdate progress=" + progress[0] + ", status=" + this.getStatus().toString());
        if (_callback != null) {
            _callback.onProgressUpdate(progress[0]);
        }

        // pd.setProgress((int) (progress[0]));
    }

    // 更新结束
    @Override
    protected void onPostExecute(String ui) {

        if (_resultCode != null && _ex != null) {// 处理在下载线程内的错误
            _callback.onFailure(_resultCode, _ex);
            return;
        }

        if (ui.length() == 0) {
            _callback.onFailure(ResultCode.ServerException, new Exception("return length is zero."));
        } else {
            _callback.onResponse(ui);
        }

        Log.d(TAG, "onPostExecute ui=" + ui);
    }

    @Override
    protected void onCancelled() {
        // 取消更新
        Log.d(TAG, "onCancelled status=" + this.getStatus().toString());
    }
}
