package com.stefan.ingym.ui.fragment.community.moments.bean;

/**
 * moment 图片 bean 类
 */
public class PhotoInfo {

    private String id;                      // 图片ID
    private String userId;                  // 所属的用户ID
    private String momentId;                // 所属的动态ID
    public String url;                      // 图片路径
    // 宽
    public int w;
    // 高
    public int h;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMomentId() {
        return momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
