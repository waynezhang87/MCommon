package com.waynezhang.mcommon.xwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.util.ResourceHelper;

/**
 * Created by liuxiaofeng02 on 2015/4/9.
 */
public class McTitleBar extends LinearLayout {

    private final static String TAG = "McTitleBar";
    private TextView mTitleView;
    private TextView mBtnLeftView;
    private TextView mBtnRightView;

    private ImageView mVLineRight;
    private ImageView mTitleIcon;

    public McTitleBar(Context context) {
        super(context);
        init(context, null);
    }

    public McTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        final TypedArray typeArray = getContext().obtainStyledAttributes(
                attrs, R.styleable.McTitleBar, R.attr.mcTitleBarStyle, R.style.defaultMcTitleBarStyle);

        String szmcLayoutResourceId = typeArray.getString(R.styleable.McTitleBar_mcLayoutResourceId);

        int mcLayoutResourceId = (szmcLayoutResourceId == null || szmcLayoutResourceId.length() == 0)?0:ResourceHelper.getId(context, szmcLayoutResourceId);
        Log.d(TAG, "init szmcLayoutResourceId=" + szmcLayoutResourceId + ", mcLayoutResourceId=" + mcLayoutResourceId);

        // int mcLayoutResourceId = typeArray.getResourceId(R.styleable.McTitleBar_mcLayoutResourceId, 0);

        if (mcLayoutResourceId == 0) {
            ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.mc_title_bar, this);
        }else{
            ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(mcLayoutResourceId, this);
        }
        mTitleView = (TextView) findViewById(R.id.mc_title);
        mBtnLeftView = (TextView) findViewById(R.id.mc_btnLeft);
        mBtnRightView = (TextView) findViewById(R.id.mc_btnRight);

        mVLineRight = (ImageView)findViewById(R.id.mc_vlineright);
        mTitleIcon = (ImageView)findViewById(R.id.mc_title_icon);



        int backgroundColor = typeArray.getColor(R.styleable.McTitleBar_android_background, getResources().getColor(R.color.mc_title_bar_background));
        setBackgroundColor(backgroundColor);

        Drawable leftButtonImage = typeArray.getDrawable(R.styleable.McTitleBar_mcLeftButtonImage);
        if (leftButtonImage != null) {
            leftButtonImage.setBounds(0, 0, leftButtonImage.getMinimumWidth(), leftButtonImage.getMinimumHeight());
            mBtnLeftView.setCompoundDrawables(leftButtonImage, null, null, null);
        } else {
            mBtnLeftView.setCompoundDrawables(null, null, null, null);
        }

        String text = typeArray.getString(R.styleable.McTitleBar_mcLeftButtonText);
        if(!TextUtils.isEmpty(text)){
            mBtnLeftView.setText(text);
        }

        text = typeArray.getString(R.styleable.McTitleBar_mcTitleText);
        if(!TextUtils.isEmpty(text)){
            mTitleView.setText(text);
        }

        int textColor = typeArray.getColor(R.styleable.McTitleBar_mcTitleTextColor, 0);
        if(textColor != 0){
            mTitleView.setTextColor(textColor);
        }

        textColor = typeArray.getColor(R.styleable.McTitleBar_android_textColor, 0);
        if(textColor != 0){
            mBtnLeftView.setTextColor(textColor);
            mBtnRightView.setTextColor(textColor);
        }

        text = typeArray.getString(R.styleable.McTitleBar_mcRightButtonText);
        if(!TextUtils.isEmpty(text)){
            mBtnRightView.setText(text);
        }

        int rightButtonBackgroundId = typeArray.getResourceId(R.styleable.McTitleBar_mcRightViewBackground, 0);
        mBtnRightView.setBackgroundResource(rightButtonBackgroundId);

        typeArray.recycle();
    }

    public CharSequence getTitle() {
        return mTitleView.getText();
    }

    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }

    public void setLeftButtonVisibility(int visibility){
        mBtnLeftView.setVisibility(visibility);
    }

    public void setRightButtonVisibility(int visibility){
        mBtnRightView.setVisibility(visibility);

        if (mVLineRight != null) mVLineRight.setVisibility(View.GONE);
    }

    public void setTitleIconVisible(int visibility){
        if (mTitleIcon != null)
            mTitleIcon.setVisibility(visibility);
    }

    public ImageView getTitleIcon(){
        return  mTitleIcon;
    }


    public void setLeftButtonClickListener(OnClickListener listener){
        mBtnLeftView.setOnClickListener(listener);
    }

    public void setRightButtonClickListener(OnClickListener listener){
        mBtnRightView.setOnClickListener(listener);
    }

    public void setLeftButtonText(CharSequence text){
        mBtnLeftView.setCompoundDrawables(null, null, null, null);
        mBtnLeftView.setText(text);
    }

    public void setRightButtonText(CharSequence text){
        mBtnRightView.setCompoundDrawables(null, null, null, null);
        mBtnRightView.setText(text);
    }

    public CharSequence getRightButtonText(){
        return mBtnRightView.getText();
    }

    public void setLeftButtonImage(Drawable drawable){
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mBtnLeftView.setCompoundDrawables(drawable, null, null, null);
        mBtnLeftView.setText("");
    }

    public void setRightButtonImage(Drawable drawable){
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mBtnRightView.setCompoundDrawables(null, null, drawable, null);
        mBtnRightView.setText("");
    }

    public void setRightButtonClickable(boolean clickable){
        if(clickable){
            mBtnRightView.setClickable(true);
        } else {
            mBtnRightView.setClickable(false);
        }
    }
}
