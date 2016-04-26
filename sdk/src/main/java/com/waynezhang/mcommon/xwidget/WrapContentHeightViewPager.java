package com.waynezhang.mcommon.xwidget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * http://stackoverflow.com/questions/8394681/android-i-am-unable-to-have-viewpager-wrap-content
 * http://www.tuicool.com/articles/nmIjym
 */
public class WrapContentHeightViewPager extends ViewPager {

    public WrapContentHeightViewPager(Context context) {
        super(context);
    }

    public WrapContentHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // find the first lastChild view
        View lastChild = getChildAt(getChildCount() - 1);

        if (lastChild != null) {
            // measure the first lastChild view with the specified measure spec
            lastChild.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int childHeight = lastChild.getMeasuredHeight();

            heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight,
                    MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}