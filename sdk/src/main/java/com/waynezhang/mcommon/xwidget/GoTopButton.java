package com.waynezhang.mcommon.xwidget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * Created by don on 1/28/15.
 */
public class GoTopButton extends ImageView {
    private boolean isHiding = false;

    public GoTopButton(Context context) {
        this(context, null);
    }

    public GoTopButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoTopButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GoTopButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {
        if (!isInEditMode())
            this.setVisibility(INVISIBLE);
    }

    public void show() {
        if (getVisibility() == View.VISIBLE)
            return;

        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, getHeight() + getBottomOffset(), 0);
        translateAnimation.setDuration(500);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        translateAnimation.setInterpolator(getContext(), android.R.anim.decelerate_interpolator);
        startAnimation(translateAnimation);
    }

    public void hide() {
        if (getVisibility() == View.INVISIBLE || isHiding) {
            return;
        }

        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, getHeight() + getBottomOffset());
        translateAnimation.setDuration(500);
        translateAnimation.setInterpolator(getContext(), android.R.anim.accelerate_interpolator);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isHiding = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.INVISIBLE);
                isHiding = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(translateAnimation);
    }

    private float getBottomOffset() {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        return dm.heightPixels - getBottom() + getPaddingBottom();
    }
}
