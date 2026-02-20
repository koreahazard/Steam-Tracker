package com.example.steam_tracker;

import com.example.steam_tracker.steam.service.SteamApiService;
import com.example.steam_tracker.steam.service.SteamApiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SteamApiServiceTest {

    private SteamApiService steamApiService;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        this.steamApiService = new SteamApiServiceImpl(restTemplate);
    }

    @Test
    @DisplayName("스팀 Api 호출 테스트")
    void testGetGameDetails() {
        //테스트 데이터 (정상 ID + 없는 ID)
        List<Integer> appIds = List.of(9999999, 730, 578080);
        List<String> results = new ArrayList<>();

        //루프 돌며 단건 호출
        for (int i = 0; i < appIds.size(); i++) {
            Long appId = Long.valueOf(appIds.get(i));

            String response = steamApiService.getGameDetail(appId);

            if (response != null && response.contains("\"success\":true")) {
                results.add(response);
                System.out.println("유효한 AppID 수집 성공: " + appId);
            } else {
                System.out.println("유효하지 않은 AppID 또는 수집 실패: " + appId);
            }

            System.out.println(">>> [AppID " + appId + "] 수집된 데이터: " + response);
            //마지막 요소가 아닐 때만 랜덤 딜레이 (여기도 필수!)
            if (i < appIds.size() - 1) {
                try {
                    long delay = java.util.concurrent.ThreadLocalRandom.current().nextLong(2000, 4001);
                    System.out.println("테스트 중... Rate Limit 방지를 위해 " + delay + "ms 대기");
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // 4. 결과 검증
        assertThat(results).hasSize(2); // 9999999는 빠져야 함
        assertThat(results.get(0)).contains("\"success\":true");
    }

}

