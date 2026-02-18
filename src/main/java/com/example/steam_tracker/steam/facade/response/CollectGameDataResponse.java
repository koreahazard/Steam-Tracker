package com.example.steam_tracker.steam.facade.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CollectGameDataResponse {
    private final Long appId;
    private final String name;
    private final int currentPrice;
    private final int originalPrice;
    private final int discountPercent;
    private final List<String> genreNames;
}

