package com.example.steam_tracker.wisiList.service.response;

import com.example.steam_tracker.wisiList.entity.TargetType;
import com.example.steam_tracker.wisiList.entity.WishList;
import lombok.Getter;

@Getter
public class WishListInfoResponse {
	private final Long wishListId;
	private final Long appId;
	private final String gameName;
	private final TargetType targetType;
	private final int targetValue;
	private final boolean inTargetRange;

	public WishListInfoResponse(WishList wishList, String gameName) {
		this.wishListId = wishList.getWishListId();
		this.appId = wishList.getAppId();
		this.gameName = gameName;
		this.targetType = wishList.getTargetType();
		this.targetValue = wishList.getTargetValue();
		this.inTargetRange = wishList.isInTargetRange();
	}
}
