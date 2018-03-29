package com.dbd.cms.core;

/**
 * Created by yuhaihui8913 on 2016/12/6.
 */
public class DBDException extends Exception {
    private String code;
    private String msg;

    public DBDException(String msg){
        super(msg);
        this.msg=msg;
    }

    public DBDException(String code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
