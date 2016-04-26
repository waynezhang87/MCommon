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
package com.waynezhang.mcommon.support;

import java.lang.ref.WeakReference;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.waynezhang.mcommon.network.NetworkChecker;
import com.waynezhang.mcommon.network.ResultCode;
import com.waynezhang.mcommon.util.L;
import com.waynezhang.mcommon.xwidget.LoadingLayout;

public abstract class McAbsCallback {
    private boolean alwaysCallback = false;

    private WeakReference<FragmentActivity> mActivityRef;
    private WeakReference<LoadingLayout> mLoadingLayoutRef;
    private WeakReference<Fragment> mFragmentRef;

    public McAbsCallback(){
        alwaysCallback = true;
    }

    public McAbsCallback(FragmentActivity activity) {
        init(activity, null);
    }

    public McAbsCallback(Fragment fragment) {
        init(fragment, null);
    }

    public McAbsCallback(FragmentActivity activity, LoadingLayout loadingLayout) {
        init(activity, loadingLayout);
    }

    public McAbsCallback(Fragment fragment, LoadingLayout loadingLayout) {
        init(fragment, loadingLayout);
    }

    private void init(Fragment fragment, LoadingLayout loadingLayout) {
        if (fragment == null)
            throw new RuntimeException("fragment should not be null when init ReqCallback");

        mFragmentRef = new WeakReference<Fragment>(fragment);
        init(fragment.getActivity(), loadingLayout);
    }

    private void init(FragmentActivity activity, LoadingLayout loadingLayout) {
        if (activity == null)
            L.w("JcAbsCallback", "activity should not be null when init ReqCallback");
        mActivityRef = new WeakReference<FragmentActivity>(activity);
        mLoadingLayoutRef = new WeakReference<LoadingLayout>(
                loadingLayout);
        startLoading();
    }

    /**
     * 如果相应的Activity都不存在时也要进行回调
     */
    protected boolean isAlwaysCallback() {
        return alwaysCallback;
    }

    protected boolean canCallback() {
        if(mFragmentRef != null){
            //如果由Fragment发起回调，当回调中调用getActivity时有些情况会出现Activity为null
            //此处对这些情况进行过滤
            if(exists(mFragmentRef) && mFragmentRef.get().getActivity() != null){
                return !Safeguard.ignorable(mFragmentRef.get());
            }
        }else if(mActivityRef != null && !Safeguard.ignorable(mActivityRef.get())){
            return !Safeguard.ignorable(mActivityRef.get());
        }

        return false;
    }

    protected boolean needShowLoading(){
        return true;
    }

    private <T> boolean exists(WeakReference<T> ref) {
        if (ref == null) {
            return false;
        }
        T obj = ref.get();
        return obj!= null;
    }

    protected void startLoading() {
        if(!needShowLoading())
            return;

        if (exists(mLoadingLayoutRef)) {
            LoadingLayout loadingLayout = mLoadingLayoutRef.get();
            loadingLayout.showLoadingView();
        }
    }

    protected void endLoading() {
        if(!needShowLoading())
            return;

        if (exists(mLoadingLayoutRef)) {
            LoadingLayout loadingLayout = mLoadingLayoutRef.get();
            loadingLayout.hideLoadingView();
        }
    }

    @SuppressWarnings("incomplete-switch")
	protected void showLoadingWithException(ResultCode code) {
        if (exists(mLoadingLayoutRef)) {
            LoadingLayout loadingLayout = mLoadingLayoutRef.get();
            switch (code){
                case NoNetwork:
                    loadingLayout.showView(LoadingLayout.ViewType.NoNetwork);
                    break;
                case NetworkException:
                    if(NetworkChecker.isOffline(loadingLayout.getContext())){
                        loadingLayout.showView(LoadingLayout.ViewType.NoNetwork);
                    } else {
                        loadingLayout.showView(LoadingLayout.ViewType.NetworkException);
                    }
                    break;
                case RequestTimeout:
                    loadingLayout.showView(LoadingLayout.ViewType.Timeout);
                    break;
                case ServerException:
                    loadingLayout.showView(LoadingLayout.ViewType.ServerException);
                    break;
                case DefaultException:
                    loadingLayout.showView(LoadingLayout.ViewType.DefaultException);
                    break;
            }
        }
    }
}
