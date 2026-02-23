package com.example.steam_tracker.game.service.response;

import com.example.steam_tracker.game.entity.PriceHistory;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PriceHistoryResponse {
	private final LocalDate snapshotDate;
	private final int price;
	private final int discountPercent;

	public PriceHistoryResponse(PriceHistory priceHistory) {
		this.snapshotDate = priceHistory.getSnapshotDate();
		this.price = priceHistory.getPrice();
		this.discountPercent = priceHistory.getDiscountPercent();
	}
}