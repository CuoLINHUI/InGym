package com.stefan.ingym.util;

import android.os.Environment;

import static android.os.Build.HOST;

/**
 * @ClassName: ConstantValue
 * @Description: 专门用来维护常量的工具类
 * @Author Stefan
 * @Date 2017/9/28 9:26
 */
public class ConstantValue {

    /**
     *  更换用户头像业务逻辑中维护的
     */
    public static String PhotoDir = Environment.getExternalStorageDirectory() + "/PhotoDemo/image/";

    /**
     * 用户成功登陆保存用户数据的key
     */
    public static final String IDENTIFIED_USER = "identified_user";

    /**
     * 联系人号码
     */
    public static final String CONTACT_PHONE = "contact_phone";

    /**
     * 用户默认地址
     */
    public static final String DEFAULT_ADDRESS = "default_address";

    /**
     * 用户默认地址条目position
     */
    public static final String ADDRESS_POSITION = "address_position";

    /**
     * 定义访问InGym服务器端的路径192.168.134.1
     */
//    public static final String HOST = "http://182.254.137.212:8080/InGymServer";
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
    public static final String HI_ARTICLE = HOST + "/index/hi_article";

    /**
     * 用户请求资讯时访问服务器端的访问地址
     */
    public static final String HI_FOODS = HOST + "/index/hi_foods";

    /**
     * 用户请求装备商品信息时访问服务器端的访问地址
     */
    public static final String EQUIPMENT_GOODS = HOST + "/community/equipment_goods";

    /**
     * 用户请求收藏商品时访问服务器端的访问地址
     */
    public static final String COLLECTION_GOODS = HOST + "/community/collection_goods";

    /**
     * 用户请求取消收藏商品时访问服务器端的访问地址
     */
    public static final String COLLECTION_CANCEL = HOST + "/community/collection_cancel";

    /**
     * 用户请求取消收藏商品时访问服务器端的访问地址
     */
    public static final String LOAD_COLLECTION = HOST + "/community/load_collection";

    /**
     * 用户请求服务端更新数据库的用户昵称
     */
    public static final String MODIFY_NICKNAME = HOST + "/user/modify_nickname";

    /**
     * 用户请求服务端更新数据库的用户登陆密码
     */
    public static final String MODIFY_PASSWORD = HOST + "/user/modify_password";

    /**
     * 用户请求服务端绑定手机号码
     */
    public static final String BIND_PHONE = HOST + "/user/bind_phone";

    /**
     * 用户请求服务端设置支付密码
     */
    public static final String SET_PAYMENT = HOST + "/user/set_payment";

}
