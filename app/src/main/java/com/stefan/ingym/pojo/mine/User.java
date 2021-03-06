package com.stefan.ingym.pojo.mine;

import java.io.Serializable;

/**
 * @ClassName: User
 * @Description: 用户的实体类，用来封装用户数据
 * @Author Stefan
 * @Date 2017/9/28 9:57
 */
public class User implements Serializable {
    private String id;          // 用户ID
    private String username;    // 用户名
    private String loginPwd;    // 登录密码
    private String payPwd;      // 支付密码
    private String gender;      // 性别
    private String email;       // 邮箱
    private String tel;         // 电话
    private String integral;    // 积分
    private String headUrl;     // 用户头像地址
    private String nickname;    // 用户昵称
    private String address;     // 用户收货地址

    public User() {     // 无参构造方法

    }

    public User(String id, String name, String headUrl){
        this.id = id;
        this.nickname = name;
        this.headUrl = headUrl;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getPayPwd() {
        return payPwd;
    }

    public void setPayPwd(String payPwd) {
        this.payPwd = payPwd;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getIntegral() {
        return integral;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
