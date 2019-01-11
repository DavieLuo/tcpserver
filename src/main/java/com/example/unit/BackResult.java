package com.example.unit;

public class BackResult {

    private String msg;
    private String errorCode;
    private Object result;

    public BackResult() {
    }

    public BackResult(String msg) {
        this.msg = msg;
        this.errorCode = "FAIL";
    }

    public BackResult(String msg, Object result) {
        this.msg = msg;
        this.errorCode = "SUCCESS";
        this.result = result;
    }

    public static BackResult getSuccess(String msg, Object result){
        return  new BackResult(msg,result);
    }

    public static BackResult getFail(String msg){
        return  new BackResult(msg);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
