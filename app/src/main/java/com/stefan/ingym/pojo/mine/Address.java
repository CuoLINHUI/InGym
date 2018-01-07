/**
 * @ClassName:
 * @Description:
 * @Author Stefan
 * @Date
 */
package com.stefan.ingym.pojo.mine;

import java.io.Serializable;

/**
 * @ClassName:
 * @Description: 用户地址的实体类，用来封装用户地址数据
 * @Author Stefan
 * @Date
 */

public class Address implements Serializable {
    private String id;              // 地址ID
    private String userID;          // 用户ID
    private String consignee;       // 收件人名
    private String phone;           // 收件人联系电话
    private String province;        // 收件人省市地址
    private String detail;          // 收件人详细地址

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id='" + id + '\'' +
                ", userID='" + userID + '\'' +
                ", consignee='" + consignee + '\'' +
                ", phone='" + phone + '\'' +
                ", province='" + province + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
