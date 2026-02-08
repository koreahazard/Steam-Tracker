package com.example.steam_tracker.steam_scraping.service;

import java.util.Set;

public interface SteamScrapingService {
    //무조건 동접자기반으로 추출, start rank 시작 등수 totalcount 총 추출할 게임ID 개수
    Set<Integer> scrapingSteamAppId(int startRankIndex, int totalCount);
}
