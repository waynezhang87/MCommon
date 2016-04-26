package com.waynezhang.mcommon.xwidget.spannable;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * Created by waynezhang on 7/27/15.
 */
public class MarginImageSpan extends ImageSpan {
    private int padding;

    public MarginImageSpan(Drawable d, int verticalAlignment, int padding) {
        super(d, verticalAlignment);
        d.setBounds(padding, 0, d.getIntrinsicWidth() + padding, d.getIntrinsicHeight());
        this.padding = padding;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return super.getSize(paint, text, start, end, fm) + padding;
    }
}
