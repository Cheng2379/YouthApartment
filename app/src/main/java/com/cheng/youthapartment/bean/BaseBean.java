package com.cheng.youthapartment.bean;

public class BaseBean<T> {
    private int code;
    private String message;
    private T data;

    public BaseBean() {

    }

    public BaseBean(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseBean: {" +
                "\n        code=" + code +
                ",\n        message='" + message + '\'' +
                ",\n        data=" + data +
                "\n}";
    }
}
