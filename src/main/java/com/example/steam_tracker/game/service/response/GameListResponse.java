package com.example.steam_tracker.game.service.response;

import com.example.steam_tracker.game.entity.Game;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class GameListResponse {
	private final Long appId;
	private final String name;
	private final int currentPrice;
	private final int originalPrice;
	private final int discountPercent;

	public GameListResponse(Game game) {
		this.appId = game.getAppId();
		this.name = game.getName();
		this.currentPrice = game.getCurrentPrice();
		this.originalPrice = game.getOriginalPrice();
		this.discountPercent = game.getDiscountPercent();
	}
}
