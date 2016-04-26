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

import java.lang.reflect.ParameterizedType;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.waynezhang.mcommon.network.Http;
import com.waynezhang.mcommon.network.ResultCode;
import com.waynezhang.mcommon.xwidget.LoadingLayout;
import com.squareup.okhttp.Request;

/**
 * Created by jc0mm0n on 10/26/14.
 */
public abstract class McJsonArrayCallback<T> extends McAbsCallback implements Http.JsonArrayCallback<T> {
    public McJsonArrayCallback(){
        super();
    }

    public McJsonArrayCallback(FragmentActivity activity) {
        super(activity);
    }

    public McJsonArrayCallback(Fragment fragment) {
        super(fragment);
    }

    public McJsonArrayCallback(FragmentActivity activity, LoadingLayout loadingLayout) {
        super(activity, loadingLayout);
    }

    public McJsonArrayCallback(Fragment fragment, LoadingLayout loadingLayout) {
        super(fragment, loadingLayout);
    }

    /**
     * 请求完获得异常结果后回调
     */
    protected void onFailure(ResultCode resultCode) {
        // empty implement
    }

    /**
     * 请求完获得正常结果后回调
     */
    protected abstract void onSuccess(List<T> resultList);

    @Override
    public void onFailure(Request request, ResultCode code, Exception e) {
        if (isAlwaysCallback() || canCallback()) {
            endLoading();

            showLoadingWithException(code);
            onFailure(code);
        }
    }

    @Override
    public final void onResponse(List<T> resultList) {
        if (isAlwaysCallback() || canCallback()) {
            endLoading();
            onSuccess(resultList);
        }
    }

    @Override
    public Class<T> getGenericType() {
        @SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) ((ParameterizedType) this.getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        return clazz;
    }
}
