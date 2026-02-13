package com.example.steam_tracker;

import com.example.steam_tracker.steam.service.SteamScrapingService;
import com.example.steam_tracker.steam.service.SteamScrapingServiceImpl; // 구현체 임포트
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SteamScrapingServiceTest {

    private final SteamScrapingService steamScrapingService = new SteamScrapingServiceImpl();

    @Test
    @DisplayName("스팀스크래핑테스트")
    void ScrapingTest() {

        int startRankIndex = 0;
        int totalCount = 29;

        // when
        Set<Integer> appIds = steamScrapingService.scrapingSteamAppId(startRankIndex, totalCount);

        // then
        System.out.println("수집된 ID 개수: " + appIds.size());
        System.out.println("ID 목록: " + appIds);

        assertThat(appIds).isNotEmpty();
        assertThat(appIds.size()).isGreaterThanOrEqualTo(10);
    }
}