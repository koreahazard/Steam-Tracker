package com.example.steam_tracker.game.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class GameIndexCalculationDataResponse {
    private long totalGameCount;
    private long totalOriginalPrice;
    private long totalCurrentPrice;
}
