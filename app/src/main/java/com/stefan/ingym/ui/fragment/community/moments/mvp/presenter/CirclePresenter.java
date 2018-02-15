package com.stefan.ingym.ui.fragment.community.moments.mvp.presenter;

import android.app.Activity;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stefan.ingym.pojo.ResponseObject;
import com.stefan.ingym.ui.fragment.community.moments.bean.CommentConfig;
import com.stefan.ingym.ui.fragment.community.moments.bean.CommentItem;
import com.stefan.ingym.ui.fragment.community.moments.bean.LikeItem;
import com.stefan.ingym.ui.fragment.community.moments.bean.MomentItem;
import com.stefan.ingym.ui.fragment.community.moments.listener.IDataRequestListener;
import com.stefan.ingym.ui.fragment.community.moments.mvp.contract.CircleContract;
import com.stefan.ingym.ui.fragment.community.moments.mvp.modle.CircleModel;
import com.stefan.ingym.ui.fragment.community.moments.utils.DatasUtil;
import com.stefan.ingym.util.ConstantValue;
import com.stefan.ingym.util.HttpUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 
* @ClassName: CirclePresenter 
* @Description: 通知model请求服务器和通知view更新
* @author yiw
* @date 2015-12-28 下午4:06:03 
*
 */
public class CirclePresenter implements CircleContract.Presenter{
	private Activity activity;
	private CircleModel circleModel;
	private CircleContract.View view;

	private int page = 1, size = 10;

	public CirclePresenter(CircleContract.View view){
		circleModel = new CircleModel();
		this.view = view;
		if (view == null) {
			activity = null;
		} else if(view instanceof Activity) {
			activity = (Activity) view;
		}
//		activity = (Activity) view;
	}

	public void loadData(final int loadType) {
		// todo 获取服务器文章的方法
		// 请求参数
		final Map<String, String> params = new HashMap<String ,String>() {
			{
				put("page", String.valueOf(page));
				put("size", String.valueOf(size));
			}
		};
		HttpUtils.doGet(ConstantValue.MOMENTS, params, new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
//				ToastUtils.showToast(activity, "抱歉，数据请求失败,请检查网络~");
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				/**
				 *  解析服务器端返回过来的结果
				 */
				Gson gson = new GsonBuilder().create();
				// 得到响应体
				String json = response.body().string();
				System.out.println(json);
				// 通过fromJson方法将json数据转化成实体类,用于解析
				ResponseObject<List<MomentItem>> datas = gson.fromJson(
						json, new TypeToken<ResponseObject<List<MomentItem>>>() {}.getType());

				if(view!=null){
					view.update2loadData(loadType, datas.getDatas());
				}

			}
		});

		// 注释下面的内容
//        List<MomentItem> datas = DatasUtil.createCircleDatas();
//        if(view!=null){
//            view.update2loadData(loadType, datas);
//        }
	}


	/**
	 * 
	* @Title: deleteCircle 
	* @Description: 删除动态 
	* @param  circleId     
	* @return void    返回类型 
	* @throws
	 */
	public void deleteCircle(final String circleId){
		// todo 删除动态
		// 改成circleModel.delete...
		circleModel.deleteCircle(new IDataRequestListener() {

			@Override
			public void loadSuccess(Object object) {
                if(view!=null){
                    view.update2DeleteCircle(circleId);
                }
			}
		});
	}
	/**
	 * 
	* @Title: addFavort 
	* @Description: 点赞
	* @param  circlePosition     
	* @return void    返回类型 
	* @throws
	 */
	public void addFavort(final int circlePosition){

		final Map<String, String> params = new HashMap<String ,String>() {
			{
				put("page", String.valueOf(page++));
				put("size", String.valueOf(size));
			}
		};
		HttpUtils.doGet(ConstantValue.MOMENTS, params, new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
//				ToastUtils.showToast(activity, "抱歉，数据请求失败,请检查网络~");
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				/**
				 *  解析服务器端返回过来的结果
				 */
				Gson gson = new GsonBuilder().create();
				// 得到响应体
				String json = response.body().string();
				System.out.println(json);
				// 通过fromJson方法将json数据转化成实体类,用于解析
				ResponseObject<List<MomentItem>> datas = gson.fromJson(
						json, new TypeToken<ResponseObject<List<MomentItem>>>() {}.getType());
			}
		});


		// todo 点赞
		circleModel.addFavort(new IDataRequestListener() {

			@Override
			public void loadSuccess(Object object) {
				LikeItem item = DatasUtil.createCurUserFavortItem();
                if(view != null ){
                    view.update2AddFavorite(circlePosition, item);
                }

			}
		});
	}
	/**
	 * 
	* @Title: deleteFavort 
	* @Description: 取消点赞 
	* @param @param circlePosition
	* @param @param favortId     
	* @return void    返回类型 
	* @throws
	 */
	public void deleteFavort(final int circlePosition, final String favortId){
		circleModel.deleteFavort(new IDataRequestListener() {

			@Override
			public void loadSuccess(Object object) {
                if(view !=null ){
                    view.update2DeleteFavort(circlePosition, favortId);
                }
			}
		});
	}
	
	/**
	 * 
	* @Title: addComment 
	* @Description: 增加评论
	* @param  content
	* @param  config  CommentConfig
	* @return void    返回类型 
	* @throws
	 */
	public void addComment(final String content, final CommentConfig config){
		if(config == null){
			return;
		}
		circleModel.addComment(new IDataRequestListener() {
			@Override
			public void loadSuccess(Object object) {
				CommentItem newItem = null;
				if (config.commentType == CommentConfig.Type.PUBLIC) {
					newItem = DatasUtil.createPublicComment(content);
				} else if (config.commentType == CommentConfig.Type.REPLY) {
					newItem = DatasUtil.createReplyComment(config.replyUser, content);
				}
                if(view!=null){
                    view.update2AddComment(config.circlePosition, newItem);
                }
			}
		});
	}
	
	/**
	 * 
	* @Title: deleteComment 
	* @Description: 删除评论 
	* @param @param circlePosition
	* @param @param commentId     
	* @return void    返回类型 
	* @throws
	 */
	public void deleteComment(final int circlePosition, final String commentId){
		circleModel.deleteComment(new IDataRequestListener(){

			@Override
			public void loadSuccess(Object object) {
                if(view!=null){
                    view.update2DeleteComment(circlePosition, commentId);
                }
			}
			
		});
	}

	/**
	 *
	 * @param commentConfig
	 */
	public void showEditTextBody(CommentConfig commentConfig){
        if(view != null){
            view.updateEditTextBodyVisible(View.VISIBLE, commentConfig);
        }
	}

    /**
     * 清除对外部对象的引用，反正内存泄露。
     */
    public void recycle(){
        this.view = null;
    }
}
