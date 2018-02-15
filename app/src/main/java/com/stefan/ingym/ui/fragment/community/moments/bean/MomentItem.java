package com.stefan.ingym.ui.fragment.community.moments.bean;

import android.text.TextUtils;

import com.stefan.ingym.pojo.mine.User;

import java.util.List;

public class MomentItem extends BaseBean{

	public final static String TYPE_URL = "1";
	public final static String TYPE_IMG = "2";
	public final static String TYPE_VIDEO = "3";

	private static final long serialVersionUID = 1L;

	private String id;
	private String content;					// 动态内容
	private String createTime;				// 动态发表时间
	private String type;					//1:链接  2:图片 3:视频
	private String linkImg;
	private String linkTitle;
	private List<PhotoInfo> photos;			// 动态图片
	private List<LikeItem> favorters;
	private List<CommentItem> comments;		// 动态评论
	private User user;
	private String videoUrl;
	private String videoImgUrl;

	private boolean isExpand;				// 判断当动态内容过多时的展开或者收起的状态
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<LikeItem> getFavorters() {
		return favorters;
	}
	public void setFavorters(List<LikeItem> favorters) {
		this.favorters = favorters;
	}
	public List<CommentItem> getComments() {
		return comments;
	}
	public void setComments(List<CommentItem> comments) {
		this.comments = comments;
	}
	public String getLinkImg() {
		return linkImg;
	}
	public void setLinkImg(String linkImg) {
		this.linkImg = linkImg;
	}
	public String getLinkTitle() {
		return linkTitle;
	}
	public void setLinkTitle(String linkTitle) {
		this.linkTitle = linkTitle;
	}
	public List<PhotoInfo> getPhotos() {
		return photos;
	}
	public void setPhotos(List<PhotoInfo> photos) {
		this.photos = photos;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getVideoImgUrl() {
		return videoImgUrl;
	}

	public void setVideoImgUrl(String videoImgUrl) {
		this.videoImgUrl = videoImgUrl;
	}

	public void setExpand(boolean isExpand){
		this.isExpand = isExpand;
	}

	public boolean isExpand(){
		return this.isExpand;
	}

	public boolean hasFavort(){
		if(favorters!=null && favorters.size()>0){
			return true;
		}
		return false;
	}
	
	public boolean hasComment(){
		if(comments!=null && comments.size()>0){
			return true;
		}
		return false;
	}
	
	public String getCurUserFavortId(String curUserId){
		String favortid = "";
		if(!TextUtils.isEmpty(curUserId) && hasFavort()){
			for(LikeItem item : favorters){
				if(curUserId.equals(item.getUser().getId())){
					favortid = item.getId();
					return favortid;
				}
			}
		}
		return favortid;
	}
}
