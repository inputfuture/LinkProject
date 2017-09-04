package com.letv.leauto.ecolink.ui.leradio_interface.dataprovider;

public class GetDataBackFlags {

    private int code;
    private String msg;
    private String errorCode;

    public GetDataBackFlags() {
        super();
    }

    public GetDataBackFlags(int code, String msg, String errorCode) {
        super();
        this.code = code;
        this.msg = msg;
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "FailedReason [code=" + this.code + ", msg=" + this.msg
                + ", errorCode=" + this.errorCode + "]";
    }

}
