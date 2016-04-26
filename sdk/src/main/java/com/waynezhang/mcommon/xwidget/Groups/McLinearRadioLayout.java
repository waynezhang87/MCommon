package com.waynezhang.mcommon.xwidget.Groups;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by liuzimao.sanders on 2015/12/22.
 */
public class McLinearRadioLayout extends LinearLayout {

    final static String TAG = McLinearRadioLayout.class.getSimpleName();

    public McLinearRadioLayout() {
        super(null, null);
    }

    public McLinearRadioLayout(Context context) {
        super(context, null);
    }

    public McLinearRadioLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public McLinearRadioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent action=" + event.getAction() + ",ax=" + (int)event.getX() + ",ay=" + (int)event.getY());

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            int count = this.getChildCount();
            for (int i = 0; i < count; i ++){
                View view = this.getChildAt(i);
                Rect rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                boolean flag = rect.contains((int) event.getX(), (int) event.getY());
                view.setSelected(flag);
            }
        }

        return super.onTouchEvent(event);
    }
}
