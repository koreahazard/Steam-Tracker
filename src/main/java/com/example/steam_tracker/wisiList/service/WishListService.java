package com.example.steam_tracker.wisiList.service;

import com.example.steam_tracker.wisiList.service.request.AddWishListRequest;
import com.example.steam_tracker.wisiList.service.response.WishListInfoResponse;

import java.util.List;

public interface WishListService {
	void addWishList(AddWishListRequest request);

	List<WishListInfoResponse> getWishListInfo(Long accountId);

	void deleteWishList(Long accountId, Long wishListId);

	void checkAndNotify(Long appId, int currentPrice, int discountPercent);

}
