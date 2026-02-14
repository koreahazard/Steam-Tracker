package com.example.steam_tracker.steam.facade.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CollectGameDataRequest {
    private int startRankIndex;
    private int totalCount;
}
