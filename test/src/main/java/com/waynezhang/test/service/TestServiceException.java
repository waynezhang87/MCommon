package com.waynezhang.test.service;


import android.text.TextUtils;

import com.waynezhang.mcommon.network.ResultCode;

/**
 * Created by don on 12/15/14.
 */
public class TestServiceException extends RuntimeException {
    private final long returnCode;
    private final String returnMessage;

    public TestServiceException(long returnCode, String returnMessage){
        this.returnCode = returnCode;
        this.returnMessage = returnMessage;
    }

    public TestServiceException(int errorCode){
        this.returnCode = errorCode;
        this.returnMessage = TestErrorCode.getErrorMsg(errorCode);
    }

    public TestServiceException(ResultCode exceptionCode){
        if(exceptionCode == ResultCode.RequestTimeout){
            returnCode = TestErrorCode.ERROR_NETWORK_TIMEOUT;
        }else if(exceptionCode == ResultCode.ServerException){
            returnCode = TestErrorCode.ERROR_INVALID_RESPONSE;
        }else if(exceptionCode == ResultCode.NetworkException){
            returnCode = TestErrorCode.ERROR_NETWORK_EXCEPTION;
        }else if(exceptionCode == ResultCode.NoNetwork){
            returnCode = TestErrorCode.ERROR_NO_NETWORK;
        }else {
            returnCode = TestErrorCode.ERROR_UNKNOWN;
        }
        returnMessage = TestErrorCode.getErrorMsg((int)returnCode);
    }

    public long getReturnCode() {
        return returnCode;
    }

    public String getReturnMessage() {
        String msg = "未知错误";
        if(!TextUtils.isEmpty(returnMessage)){
           msg = returnMessage;
        }
        return msg;
    }
}
