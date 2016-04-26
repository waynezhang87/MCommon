package com.waynezhang.mcommon.xwidget;

import android.os.Handler;
import android.text.Html;
import android.widget.TextView;

import com.waynezhang.mcommon.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liuyagang on 14-12-26.
 */
public class TimeRemainingUtil {
    private Handler handler;
    private TextView view;
    private String textFormat;
    private int timeInterval = 800;
    private TimeoutListener listener;
    private long startTimestamp;
    private long timeoutMillis;
    private boolean showSecondsOnly;
    private boolean isStop = true;

    public TimeRemainingUtil(TextView view, String textFormat) {
        this(view, textFormat, false);
    }

    public TimeRemainingUtil(TextView view, String textFormat, boolean showSecondsOnly) {
        this.handler = new Handler(view.getContext().getMainLooper());
        this.view = view;
        this.textFormat = textFormat;
        this.showSecondsOnly = showSecondsOnly;
    }

    public TimeRemainingUtil(TextView view) {
        this(view, null);
    }

    public void start(final int timeout, TimeoutListener listener) {
        this.listener = listener;
        startTimestamp = System.currentTimeMillis();
        timeoutMillis = timeout * 1000;

        if (timeout <= 0) {
            updateView(startTimestamp);
            timeout();
            return;
        }

        isStop = false;
        handler.postDelayed(timerRunnable, timeInterval);

        // 启动时先刷新一次，否则界面需要等待timeInterval之后才能刷新
        updateView(startTimestamp);
    }

    public void stop() {
        cancel();
    }

    private void cancel() {
        isStop = true;

        if (listener != null) {
            listener.onCancel();
        }
    }

    private void timeout() {
        isStop = true;

        if (listener != null) {
            listener.onTimeout();
        }
    }

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isStop) {
                return;
            }
            long currentTimestamp = System.currentTimeMillis();
            // timeout
            if (currentTimestamp - startTimestamp >= timeoutMillis) {
                // updateView(startTimestamp);
                timeout();
            } else {
                updateView(currentTimestamp);
                handler.postDelayed(timerRunnable, timeInterval);
            }
        }
    };

    private void updateView(long currentTimestamp) {
        long timeRemaining = (timeoutMillis - (currentTimestamp - startTimestamp)) / 1000;

        String htmlText;
        if (showSecondsOnly) {
            if (StringUtil.isEmpty(textFormat)) {
                htmlText = String.format("%d秒", timeRemaining);
            } else {
                htmlText = String.format(textFormat, String.format("%d秒", timeRemaining));
            }
        } else {
            Date date = new Date(0, 0, 0, 0, 0, (int) (timeRemaining));
            SimpleDateFormat dateFormat = new SimpleDateFormat(autoFormat(timeRemaining));

            if (StringUtil.isEmpty(textFormat)) {
                htmlText = dateFormat.format(date);
            } else {
                htmlText = String.format(textFormat, dateFormat.format(date));
            }
        }
        CharSequence text = Html.fromHtml(htmlText);
        view.setText(text);
    }

    String autoFormat(long time) {
        final long SecondsOfMinute = 60;
        final long SecondsOfHour = 60 * 60;
        final long SecondsOfDay = 60 * 60 * 24;
        final long SecondsOfMonth = 60 * 60 * 24 * 30;

        String format;
        if (time >= SecondsOfMonth) {// 如果够一个月，按月显示
            format = "MM个月dd天";
        } else if (time >= SecondsOfDay) {// 如果够一天，按天显示
            format = "dd天HH小时";
        } else if (time >= SecondsOfHour) {// 如果够一个小时，按小时显示
            format = "HH小时mm分钟";
        } else if (time >= SecondsOfMinute) {// 如果够一分钟，按分钟显示
            format = "mm分ss秒";
        } else {// 不够一分钟按秒显示
            format = "s秒";
        }
        return format;
    }

    public interface TimeoutListener {
        void onTimeout();

        void onCancel();
    }

    public static class NPTimeoutHandler implements TimeoutListener {
        @Override
        public void onTimeout() {

        }

        @Override
        public void onCancel() {

        }
    }


}
