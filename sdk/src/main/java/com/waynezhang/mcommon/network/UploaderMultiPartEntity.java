package com.waynezhang.mcommon.network;
import android.util.Log;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by liuzimao.sanders on 2015/8/11.
 */
public class UploaderMultiPartEntity extends MultipartEntity {

    private final static String TAG = "UploaderMultiPartEntity";
    private final ProgressListener listener;

    public UploaderMultiPartEntity(final ProgressListener listener) {
        super();
        this.listener = listener;
        Log.d(TAG, "UploaderMultiPartEntity");
    }

    public UploaderMultiPartEntity(final HttpMultipartMode mode, final ProgressListener listener) {
        super(mode);
        Log.d(TAG, "UploaderMultiPartEntity 01");
        this.listener = listener;
    }

    public UploaderMultiPartEntity(HttpMultipartMode mode, final String boundary, final Charset charset, final ProgressListener listener) {

        super(mode, boundary, charset);
        this.listener = listener;
        Log.d(TAG, "UploaderMultiPartEntity 02");

    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        Log.d(TAG, "writeTo");
        super.writeTo(new CountingOutputStream(outstream, this.listener));
    }

    public static interface ProgressListener {
        void transferred(long num);
    }

    public static class CountingOutputStream extends FilterOutputStream {

        private final ProgressListener listener;
        private long transferred;

        public CountingOutputStream(final OutputStream out, final ProgressListener listener) {
            super(out);
            this.listener = listener;
            this.transferred = 0;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            Log.d(TAG, "writeTo len=" + len);

            out.write(b, off, len);
            this.transferred += len;
            this.listener.transferred(this.transferred);
        }

        public void write(int b) throws IOException {
            Log.d(TAG, "writeTo b=" + b);
            out.write(b);
            this.transferred++;
            this.listener.transferred(this.transferred);
        }
    }
}
