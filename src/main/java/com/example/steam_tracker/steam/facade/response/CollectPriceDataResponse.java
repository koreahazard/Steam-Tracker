package com.example.steam_tracker.steam.facade.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CollectPriceDataResponse {
	private final Long appId;
	private final int currentPrice;
	private final int originalPrice;
	private final int discountPercent;
}
