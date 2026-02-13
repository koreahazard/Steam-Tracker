package com.example.steam_tracker.steam.service;

import java.util.Set;

public interface SteamScrapingService {
    //start rank index 시작 등수 totalcount 총 추출할 게임ID 개수
    Set<Integer> scrapingSteamAppId(int startRankIndex, int totalCount);
}
