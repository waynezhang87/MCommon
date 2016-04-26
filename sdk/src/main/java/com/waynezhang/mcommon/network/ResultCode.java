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
package com.waynezhang.mcommon.network;

/**
 * Created by jc0mm0n on 10/25/14.
 */
public enum ResultCode {
    Success(0, "成功"),//成功
    NoNetwork(1, "无网络"),//无网络
    NetworkException(2, "网络异常"),//网络请求失败
    RequestTimeout(3, "请求超时"),//请求超时
    ServerException(4, "服务器异常"),//服务器异常,返回不是接口规定的数据
    ServerResponseTimeout(5, "服务器响应超时"),//服务器响应超时
    DefaultException(6, "未知异常");//除上述异常以外的异常

    ResultCode(int ni, String msg){
        nativeInt = ni;
        this.msg = msg;
    }

    public final int nativeInt;
    public final String msg;

}
