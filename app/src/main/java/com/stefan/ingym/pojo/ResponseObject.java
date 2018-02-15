package com.stefan.ingym.pojo;

/**
 * @ClassName: ResponseObject
 * @Description: 对数据的二次封装，主要用于数据的请求结果状态，和各种状态提示
 * @Author Stefan
 * @Date 2017/9/29 15:50
 */
public class ResponseObject<T> {
    private String msg;			// 各种状态的消息
    private int state = 1;		// 0:失败        1：成功
    private T datas;			// 存放我真正需要解析的数据
    private int page;			// 当前请求的页码
    private int size;			// 每页的条数
    private int pageCount;		// 总共的页数

    // 该构造方法用于，请求成功后，获取数据并提示相应消息
    public ResponseObject(String msg, int state, T datas) {
        this.msg = msg;
        this.state = state;
        this.datas = datas;
    }

    // 该构造方法用于，请求成功后，直接获取数据，不提示相应消息
    public ResponseObject(int state, T datas) {
        this.state = state;
        this.datas = datas;
    }

    // 该构造方法用于，请求失败后，提示相应的消息
    public ResponseObject(String msg, int state) {
        this.msg = msg;
        this.state = state;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public T getDatas() {
        return datas;
    }

    public void setDatas(T datas) {
        this.datas = datas;
    }
}