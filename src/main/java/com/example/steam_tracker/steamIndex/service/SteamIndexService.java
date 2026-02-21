package com.example.steam_tracker.steamIndex.service;

import java.util.List;

public interface SteamIndexService {
    void recordDailyIndex(List<Long> targetAppIdList);
}
