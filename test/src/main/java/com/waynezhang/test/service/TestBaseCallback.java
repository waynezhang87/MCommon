package com.waynezhang.test.service;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.waynezhang.mcommon.support.Safeguard;

import java.lang.ref.WeakReference;

/**
 * Created by don on 12/15/14.
 */
public class TestBaseCallback {
    private boolean alwaysCallback = false;

    private boolean showLoading;
    private WeakReference<Activity> mActivityRef;
    private WeakReference<Fragment> mFragmentRef;

    public TestBaseCallback() {
        alwaysCallback = true;
    }

    public TestBaseCallback(Activity activity) {
        init(activity, true);
    }

    public TestBaseCallback(Fragment fragment) {
        init(fragment, true);
    }

    public TestBaseCallback(Activity activity, boolean showLoading) {
        init(activity, showLoading);
    }

    public TestBaseCallback(Fragment fragment, boolean showLoading) {
        init(fragment, showLoading);
    }

    private void init(Fragment fragment, boolean showLoading) {
        if (fragment == null)
            throw new RuntimeException("fragment should not be null when init TestReqCallback");

        mFragmentRef = new WeakReference<Fragment>(fragment);
        init((Activity)fragment.getActivity(), showLoading);
    }

    private void init(Activity activity, boolean showLoading) {
        if (activity == null)
            throw new RuntimeException("activity should not be null when init TestReqCallback");

        if (!(activity instanceof Activity)) {
            throw new RuntimeException("activity should be subclass of Activity for loading");
        }
        mActivityRef = new WeakReference<Activity>((Activity)activity);
        this.showLoading = showLoading;
        startLoading();
    }

    /**
     * 如果相应的Activity都不存在时也要进行回调
     */
    protected boolean isAlwaysCallback() {
        return alwaysCallback;
    }

    protected boolean canCallback() {
        if (mFragmentRef != null) {
            //如果由Fragment发起回调，当回调中调用getActivity时有些情况会出现Activity为null
            //此处对这些情况进行过滤
            if (exists(mFragmentRef) && mFragmentRef.get().getActivity() != null) {
                return !Safeguard.ignorable(mFragmentRef.get());
            }
        } else if (mActivityRef != null && !Safeguard.ignorable(mActivityRef.get())) {
            return !Safeguard.ignorable(mActivityRef.get());
        }

        return false;
    }

    protected boolean needShowLoading() {
        return showLoading;
    }

    private <T> boolean exists(WeakReference<T> ref) {
        if (ref == null) {
            return false;
        }
        T obj = ref.get();
        return obj != null;
    }

    public void startLoading() {
        if (!needShowLoading())
            return;

        if (exists(mActivityRef)) {
//            mActivityRef.get().showLoadingView();
        }
    }

    public void endLoading() {
        if (!needShowLoading())
            return;

        if (exists(mActivityRef)) {
//            mActivityRef.get().hideLoadingView();
        }
    }

    @SuppressWarnings("incomplete-switch")
    protected void showLoadingWithException(TestServiceException testServiceException) {
        if (!exists(mActivityRef))
            return;

//        TestLoadProgress loadProgress = mActivityRef.get();

//        if (exists(mActivityRef)) {
//            if (NetworkChecker.isOffline(mActivityRef.get().getApplicationContext())) {
//                loadProgress.showMessage(ResultCode.NoNetwork.msg);
//                return;
//            }
//        }
//
//        loadProgress.showMessage(testServiceException.getReturnMessage());
    }

}
