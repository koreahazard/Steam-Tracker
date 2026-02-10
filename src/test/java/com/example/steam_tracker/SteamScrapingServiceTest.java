package com.example.steam_tracker;

import com.example.steam_tracker.steam_scraping.service.SteamScrapingService;
import com.example.steam_tracker.steam_scraping.service.SteamScrapingServiceImpl; // 구현체 임포트
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SteamScrapingServiceTest {

    private final SteamScrapingService steamScrapingService = new SteamScrapingServiceImpl();

    @Test
    @DisplayName("스프링 없이 순수하게 수집 로직만 테스트")
    void testScraping() {
        // 1. 수집 시도 (200개)
        Set<Integer> appIds = steamScrapingService.scrapingSteamAppId(0, 200);

        // 2. 결과 출력
        System.out.println("=====================================");
        System.out.println("수집된 데이터 개수: " + appIds.size());
        System.out.println("=====================================");

        // 3. 검증
        assertThat(appIds).isNotEmpty();
    }
}