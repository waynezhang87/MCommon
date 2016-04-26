package com.waynezhang.test.service;

import android.app.Activity;
import android.support.v4.app.Fragment;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by don on 12/15/14.
 */
public abstract class TestTestReqCallback<T> extends TestBaseCallback implements TestReqCallback<T> {

    public TestTestReqCallback() {
        super();
    }

    public TestTestReqCallback(Activity activity) {
        super(activity);
    }

    public TestTestReqCallback(Fragment fragment) {
        super(fragment);
    }

    public TestTestReqCallback(Activity activity, boolean showLoading) {
        super(activity, showLoading);
    }

    public TestTestReqCallback(Fragment fragment, boolean showLoading) {
        super(fragment, showLoading);
    }

    /**
     * 请求完获得正常结果后回调
     */
    protected abstract void onSuccess(T result);

    /**
     * 请求完获得异常结果后回调
     */
    @Override
    public void onFailure(TestServiceException testServiceException) {
//        if (testServiceException.getReturnCode() == -777 || testServiceException.getReturnCode() == TestErrorCode.ERROR_NO_LOGIN) {
//            ServiceGHomeApi.clearLoginStatus();
//        }
        if (isAlwaysCallback() || canCallback()) {
            endLoading();

            showLoadingWithException(testServiceException);
        }
    }

    @Override
    public final void onResponse(T result) {
        if (isAlwaysCallback() || canCallback()) {
            endLoading();
            onSuccess(result);
        }
    }

    @Override
    public Type getGenericType() {
        final Type[] actualTypeArguments = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        return actualTypeArguments[0];
    }
}
