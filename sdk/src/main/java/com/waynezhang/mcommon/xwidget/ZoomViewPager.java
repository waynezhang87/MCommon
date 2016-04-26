package com.waynezhang.mcommon.xwidget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by don on 12/31/14.
 * 跟ZoomImageView配套使用的ViewPager，
 * 主要为了解决
 * （1）两指缩放跟左右滑动的事件冲突
 * （2） 滑动的边缘时候对viewpager手指位置的重置，防止突然偏移位置过大
 */
public class ZoomViewPager extends ViewPager {
    private boolean swipeable = true;

    //是否重置intercept事件，将viewPager之前的历史手指位置记录重置
    private boolean resetIntercept = false;

    public ZoomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomViewPager(Context context) {
        super(context);
    }

    public void setSwipeable(boolean swipeable) {
        this.swipeable = swipeable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        try {
            if(event.getPointerCount() == 2 || !swipeable){
                resetIntercept = true;
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(resetIntercept){
            //重置手指点击的历史位置，解决大图缩放移动位置的偏差
            MotionEvent ev = MotionEvent.obtain(event);
            ev.setAction(MotionEvent.ACTION_DOWN);
            super.onInterceptTouchEvent(ev);
            resetIntercept = false;
        }

        boolean result = false;
        try {
            result = super.onInterceptTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
