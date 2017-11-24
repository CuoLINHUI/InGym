package com.stefan.ingym.pojo.index;

import java.io.Serializable;

/**
 * @ClassName: Foods
 * @Description: 食物实体类，用于封装食物数据
 * @Author Stefan
 * @Date 2017/11/22 21:47
 */

public class Foods implements Serializable{
    private String id;                          // 食物ID
    private String food_name;                   // 食物名称
    private String food_pic;                    // 食物条目路径
    private String food_calorie;                // 食物每100克的卡路里
    private String food_weight;                 // 食物质量
    private String food_protein;                // 食物每100克的蛋白质
    private String food_carbohydrate;           // 食物每100克的碳水化合物
    private String food_fat;                    // 食物每100克的脂肪
    private String food_detail;                 // 详情描述
    private String food_recommended_types;      // 推荐类型

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getFood_pic() {
        return food_pic;
    }

    public void setFood_pic(String food_pic) {
        this.food_pic = food_pic;
    }

    public String getFood_calorie() {
        return food_calorie;
    }

    public void setFood_calorie(String food_calorie) {
        this.food_calorie = food_calorie;
    }

    public String getFood_weight() {
        return food_weight;
    }

    public void setFood_weight(String food_weight) {
        this.food_weight = food_weight;
    }

    public String getFood_protein() {
        return food_protein;
    }

    public void setFood_protein(String food_protein) {
        this.food_protein = food_protein;
    }

    public String getFood_carbohydrate() {
        return food_carbohydrate;
    }

    public void setFood_carbohydrate(String food_carbohydrate) {
        this.food_carbohydrate = food_carbohydrate;
    }

    public String getFood_fat() {
        return food_fat;
    }

    public void setFood_fat(String food_fat) {
        this.food_fat = food_fat;
    }

    public String getFood_detail() {
        return food_detail;
    }

    public void setFood_detail(String food_detail) {
        this.food_detail = food_detail;
    }

    public String getFood_recommended_types() {
        return food_recommended_types;
    }

    public void setFood_recommended_types(String food_recommended_types) {
        this.food_recommended_types = food_recommended_types;
    }
}
