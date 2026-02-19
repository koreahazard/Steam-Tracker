package com.example.steam_tracker;

import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;
import com.example.steam_tracker.steam.service.SteamApiServiceImpl;
import com.example.steam_tracker.steam.service.SteamParsingServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class SteamParsingServiceTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final SteamApiServiceImpl steamApiService = new SteamApiServiceImpl(restTemplate);
    private final SteamParsingServiceImpl steamParsingService = new SteamParsingServiceImpl();

    @Test
    @DisplayName("실제 API 데이터 수집 및 파싱 결과 전체 출력")
    void printRealDataParsingResult() {
        // 1. 진짜 데이터를 가져올 AppID 리스트 (테스트용으로 730:글옵, 1091500:사펑 준비)
        List<Long> targetAppIds = List.of(578080L, 1245620L);

        for (Long appId : targetAppIds) {
            // 2. 진짜 API 호출
            String realJson = steamApiService.getGameDetail(appId);

            if (realJson != null) {
                // 3. 파싱 서비스 호출
                List<CollectGameDataResponse> responses = steamParsingService.parseGameDetail(List.of(realJson));

                // 4. 반환된 response 결과값 전체 출력
                for (CollectGameDataResponse res : responses) {
                    System.out.println("==========================================");
                    System.out.println("AppID: " + res.getAppId());
                    System.out.println("게임명: " + res.getName());
                    System.out.println("현재가: " + res.getCurrentPrice() + "원");
                    System.out.println("원래가격: " + res.getOriginalPrice() + "원");
                    System.out.println("할인율: " + res.getDiscountPercent() + "%");
                    System.out.println("장르: " + String.join(", ", res.getGenreNames()));
                    System.out.println("==========================================");
                }
            } else {
                System.out.println("AppID " + appId + "의 데이터를 가져오지 못했습니다.");
            }
        }
    }
}