package com.example.steam_tracker.wisiList.service.request;

import com.example.steam_tracker.wisiList.entity.TargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddWishListRequest {
	private final Long accountId;
	private final Long appId;
	private final TargetType targetType;
	private final int targetValue;

}
