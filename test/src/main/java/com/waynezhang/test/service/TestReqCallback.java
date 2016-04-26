package com.waynezhang.test.service;

import java.lang.reflect.Type;

/**
 * Created by don on 12/15/14.
 */
public interface TestReqCallback<T>{
    public Type getGenericType();

    public void onFailure(TestServiceException testServiceException);

    public void onResponse(T result);
}
