package com.stefan.ingym.pojo.community;

import java.io.Serializable;

/**
 * @ClassName: Goods
 * @Description: 商品实体类，用于封装装备商品数据
 * @Author Stefan
 * @Date 2017/11/6 15:39
 */
public class Goods implements Serializable {    // 继承可序列类，使得Goods对象能可序列化，可以用于传递该对象
    private String id;                      // 商品ID
    private String category_id;             // 商品分类ID
    private String title;                   // 商品标题
    private String sort_title;              // 标题描述
    private String imageURL;                // 条目展示图片路径
    private String bigPicURL;               // 商品详情大图展示
    private double value;                   // 商品原价（价值）
    private double price;                   // 商品现价（价格）
    private int payments;                   // 付款人数（商品销量）
    private String stock;                   // 商品库存量
    private String details;                 // 商品详情
    private int integral;                // 商品所需的积分

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSort_title() {
        return sort_title;
    }

    public void setSort_title(String sort_title) {
        this.sort_title = sort_title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getBigPicURL() {
        return bigPicURL;
    }

    public void setBigPicURL(String bigPicURL) {
        this.bigPicURL = bigPicURL;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPayments() {
        return payments;
    }

    public void setPayments(int payments) {
        this.payments = payments;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }
}
