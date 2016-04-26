package com.waynezhang.mcommon.xwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * 带有文本进度的水平进度条
 *
 * Created by panhongchao.payne on 2015/1/25.
 */
public class ProgressWithTextBar extends ProgressBar {
    // 加值
    public static final int ADD = 0;
    // 减值
    public static final int MINIS = 1;

    private static Paint paint;
    // 灰色背景下文字的颜色
    private static int GREY_BG = Color.rgb(178, 178, 178);
    // 绿色背景下文字的颜色
    private static int GREEN_BG = Color.rgb(255, 255, 255);
    private String text;

    public ProgressWithTextBar(Context context) {
        this(context, null);
    }

    public ProgressWithTextBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressWithTextBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        // setProgress是在构造函数中调用的
        if(paint == null){
            initPaint();
        }
        setText(progress);
        setTextColor(progress);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        paint.getTextBounds(this.text, 0, this.text.length(), rect);
        int x = (getWidth() / 2) - rect.centerX();
        int y = (getHeight() / 2) - rect.centerY();
        canvas.drawText(text, x, y, paint);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        setTextColor(this.getProgress());
        paint.setTextSize(20);
    }

    private void setText(int progress) {
        int i = (progress * 100) / this.getMax();
        if(i > 100){
            i = 100;
        }
        text = String.valueOf(i) + "%";
    }

    private void setTextColor(int progress) {
        if(progress>=this.getMax()/2) {
            paint.setColor(GREEN_BG);
        } else {
            paint.setColor(GREY_BG);
        }
    }

    /**
     * 根据实际需要设置进度条的值
     * @param i
     */
    public void setProgress(int i, int unit) {
        int cur = getProgress();
        if(i==ADD){
            setProgress(cur+unit);
        } else {
            setProgress(cur-unit);
        }
    }
}
