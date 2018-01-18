package com.stefan.ingym.pojo.community;

import java.io.Serializable;

/**
 * @ClassName: Collections
 * @Description:  封装收藏信息
 * @Author Stefan
 * @Date 2018/1/17 17:17
 */
public class Collections implements Serializable {
	private String collectionID;             // 收藏ID
	private String userID;                   // 该收藏对应的用户ID
	private String goodsID;       // 被收藏的商品ID
	private Goods goods;

	public String getCollectionID() {
		return collectionID;
	}

	public void setCollectionID(String collectionID) {
		this.collectionID = collectionID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getGoodsID() {
		return goodsID;
	}

	public void setGoodsID(String goodsID) {
		this.goodsID = goodsID;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}


}
