package com.stefan.ingym.util;

/**
 * @ClassName: ConstantValue
 * @Description: 专门用来维护常量的工具类
 * @Author Stefan
 * @Date 2017/9/28 9:26
 */
public class ConstantValue {

    /**
     * 用户登陆名的key
     */
    public static final String LOGIN_USER = "login_user";

    /**
     * 定义访问InGym服务器端的路径192.168.134.1
     */
//    public static final String HOST = "http://192.168.134.1:8080/InGymServer";
    public static final String HOST = "http://10.0.2.2:8080/InGymServer";

    /**
     * 用户注册时访问服务器端的访问地址
     */
    public static final String USER_REGISTER = HOST + "/user/register";

    /**
     * 用户登陆时访问服务器端的访问地址
     */
    public static final String USER_ACCOUNT_LOGIN = HOST + "/user/account_login";

    /**
     * 用户请求资讯时访问服务器端的访问地址
     */
    public static final String HI_ARTICLE = HOST + "/article/hi_article";

}
