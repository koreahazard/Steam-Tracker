package com.example.steam_tracker.steam.facade.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ExpandGameDataRequest {
    private final List<Long> targetAppIdList;
    private final int startRankIndex;
    private final int totalCount;

}
