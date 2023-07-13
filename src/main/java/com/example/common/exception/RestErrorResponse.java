package com.example.common.exception;

/**
 * 和前端约定返回的异常信息模型
 */
public class RestErrorResponse {
    private String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
