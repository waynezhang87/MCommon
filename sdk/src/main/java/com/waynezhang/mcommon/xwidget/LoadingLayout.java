/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 jc0mm0n
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.waynezhang.mcommon.xwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.waynezhang.mcommon.R;


public class LoadingLayout extends FrameLayout {
    public enum ViewType {
        /**
         * 请求中
         */
        Loading             (0),
        /**
         * 加载异常, 在无合适的异常类型使用的情况下使用此异常
         */
        DefaultException    (1),
        /**
         * 除无网络和网络超时意外的网络异常
         */
        NetworkException    (2),
        /**
         * 服务器接口异常, 服务器返回数据有误(比如反序列化失败)
         */
        ServerException     (3),
        /**
         * 无网络
         */
        NoNetwork           (4),
        /**
         * 网络超时
         */
        Timeout             (5),
        /**
         * 返回数据为空
         */
        NoData              (6);

        ViewType(int ni) {
            nativeInt = ni;
        }
        final int nativeInt;
    }

    private SparseIntArray defaultLayout = new SparseIntArray(6);
    private SparseArray<View> cachedLayout = new SparseArray<View>(6);
    private LayoutInflater mInflater;
    private OnClickListener mBtn1Listener;
    private OnClickListener mBtn2Listener;
    private OnClickListener mBtn3Listener;

    public LoadingLayout(Context context) {
        super(context);
        init(context, null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (isInEditMode())
            return;

        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingLayout);
            if (a != null) {
                setDefaultView(ViewType.Loading, a.getResourceId(
                        R.styleable.LoadingLayout_loadingView, R.layout.mc__loading));
                setDefaultView(ViewType.DefaultException, a.getResourceId(
                        R.styleable.LoadingLayout_defaultExceptionView, R.layout.mc__loading_default_exception));
                setDefaultView(ViewType.NetworkException, a.getResourceId(
                        R.styleable.LoadingLayout_networkExceptionView, R.layout.mc__loading_default_network_exception));
                setDefaultView(ViewType.ServerException, a.getResourceId(
                        R.styleable.LoadingLayout_serverExceptionView, R.layout.mc__loading_server_exception));
                setDefaultView(ViewType.NoNetwork, a.getResourceId(
                        R.styleable.LoadingLayout_noNetworkView, R.layout.mc__loading_no_network));
                setDefaultView(ViewType.Timeout, a.getResourceId(
                        R.styleable.LoadingLayout_timeoutView, R.layout.mc__loading_timeout));
                setDefaultView(ViewType.NoData, a.getResourceId(
                        R.styleable.LoadingLayout_noDataView, R.layout.mc__loading_no_data));
                a.recycle();
            }
        }
    }

    public void setDefaultView(ViewType viewType, int resLayout) {
        defaultLayout.put(viewType.nativeInt, resLayout);
    }

    public void showView(ViewType viewType) {
        int count = defaultLayout.size();
        for(int i = 0; i < count; i++) {
            int key = defaultLayout.keyAt(i);
            if(key==viewType.nativeInt){
                doShowView(viewType);
            }else{
                hideViewByKey(key);
            }
        }
    }

    private void hideViewByKey(int key) {
        View view = cachedLayout.get(key);

        if (view==null)
            return;

        view.setVisibility(GONE);
    }

    private void doShowView(ViewType viewType) {
        int resLayoutId = defaultLayout.get(viewType.nativeInt);
        if (resLayoutId <= 0)
            throw new IllegalStateException("layout is not set for " + viewType);

        View view = cachedLayout.get(viewType.nativeInt);

        if (view == null) {
            view = mInflater.inflate(resLayoutId, null);
            cachedLayout.put(viewType.nativeInt, view);
            addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
            initListener(view);
        }

        view.setVisibility(VISIBLE);
        view.bringToFront();
    }

    private void initListener(View view) {
        View btn1 = view.findViewById(android.R.id.button1);
        if (btn1 != null && mBtn1Listener != null) {
            btn1.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    mBtn1Listener.onClick(v);
                }
            });
        }

        View btn2 = view.findViewById(android.R.id.button2);
        if (btn2 != null && mBtn2Listener != null) {
            btn2.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    mBtn2Listener.onClick(v);
                }
            });
        }

        View btn3 = view.findViewById(android.R.id.button3);
        if (btn3 != null && mBtn3Listener != null) {
            btn3.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    mBtn3Listener.onClick(v);
                }
            });
        }
    }

    public void showLoadingView() {
        showView(ViewType.Loading);
    }

    public void hideLoadingView() {
        hideViewByKey(ViewType.Loading.nativeInt);
    }

    public void showNoDataView() {
        showView(ViewType.NoData);
    }

    public void hideNoDataView() {
        hideViewByKey(ViewType.NoData.nativeInt);
    }

    public void showTimeoutView() {
        showView(ViewType.Timeout);
    }

    public void hideTimeoutView() {
        hideViewByKey(ViewType.Timeout.nativeInt);
    }

    public void hideAllMask(){
        int count = cachedLayout.size();
        for(int i = 0; i < count; i++) {
            int key = cachedLayout.keyAt(i);
            hideViewByKey(key);
        }
    }

    public void setButton1ClickListener(OnClickListener listener){
        mBtn1Listener = listener;
    }

    public void setButton2ClickListener(OnClickListener listener){
        mBtn2Listener = listener;
    }

    public void setButton3ClickListener(OnClickListener listener){
        mBtn3Listener = listener;
    }

}

