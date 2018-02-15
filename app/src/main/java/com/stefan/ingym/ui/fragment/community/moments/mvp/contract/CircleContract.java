package com.stefan.ingym.ui.fragment.community.moments.mvp.contract;

import com.stefan.ingym.ui.fragment.community.moments.bean.CommentConfig;
import com.stefan.ingym.ui.fragment.community.moments.bean.CommentItem;
import com.stefan.ingym.ui.fragment.community.moments.bean.LikeItem;
import com.stefan.ingym.ui.fragment.community.moments.bean.MomentItem;

import java.util.List;

/**
 * Created by suneee on 2016/7/15.
 */
public interface CircleContract {

    interface View extends BaseView{
        void update2DeleteCircle(String circleId);
        void update2AddFavorite(int circlePosition, LikeItem addItem);
        void update2DeleteFavort(int circlePosition, String favortId);
        void update2AddComment(int circlePosition, CommentItem addItem);
        void update2DeleteComment(int circlePosition, String commentId);
        void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig);
        void update2loadData(int loadType, List<MomentItem> datas);
    }

    interface Presenter extends BasePresenter{
        void loadData(int loadType);
        void deleteCircle(final String circleId);
        void addFavort(final int circlePosition);
        void deleteFavort(final int circlePosition, final String favortId);
        void deleteComment(final int circlePosition, final String commentId);

    }
}
