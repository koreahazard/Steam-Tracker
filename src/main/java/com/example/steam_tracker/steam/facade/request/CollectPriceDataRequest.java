package com.example.steam_tracker.steam.facade.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CollectPriceDataRequest {
    private final List<Long> appIdList;
}
