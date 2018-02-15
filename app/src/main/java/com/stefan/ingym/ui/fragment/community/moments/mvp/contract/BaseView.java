package com.stefan.ingym.ui.fragment.community.moments.mvp.contract;

/**
 * Created by yiwei on 16/4/1.
 */
public interface BaseView {
    void showLoading(String msg);
    void hideLoading();
    void showError(String errorMsg);

}
