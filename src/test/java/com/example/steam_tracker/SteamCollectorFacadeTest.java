package com.example.steam_tracker;

import com.example.steam_tracker.steam.facade.SteamCollectorFacade;
import com.example.steam_tracker.steam.facade.request.CollectGameDataRequest;
import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;
import com.example.steam_tracker.steam.service.SteamApiServiceImpl;
import com.example.steam_tracker.steam.service.SteamParsingServiceImpl;
import com.example.steam_tracker.steam.service.SteamScrapingServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SteamCollectorFacadeTest {

    private final SteamScrapingServiceImpl steamScrapingService = new SteamScrapingServiceImpl();
    private final SteamApiServiceImpl steamApiService = new SteamApiServiceImpl(new RestTemplate());
    private final SteamParsingServiceImpl steamParsingService = new SteamParsingServiceImpl();

    private final SteamCollectorFacade steamCollectorFacade = new SteamCollectorFacade(
            steamScrapingService,
            steamApiService,
            steamParsingService
    );

    @Test
    @DisplayName("진짜 스팀 데이터 30개 수집 및 전체 결과 출력")
    void realDataCollectionTest() {
        // given: 30개 설정
        int targetCount = 30;
        CollectGameDataRequest request = new CollectGameDataRequest(0, targetCount);

        // when: 실제 전체 프로세스 실행
        // ⚠️ 주의: 각 아이디마다 2~5초 대기하므로 30개면 약 1~2분 정도 소요됩니다.
        // 켜두고 커피 한 잔 마시고 오세요!
        List<CollectGameDataResponse> responses = steamCollectorFacade.collectGameData(request);

        // then: 출력
        System.out.println("\n" + "=".repeat(20) + " 최종 수집 및 파싱 결과 " + "=".repeat(20));
        System.out.println("총 수집 성공 개수: " + responses.size());

        for (CollectGameDataResponse res : responses) {
            System.out.println("------------------------------------------");
            System.out.println("AppID       : " + res.getAppId());
            System.out.println("게임명      : " + res.getName());
            System.out.println("무료 여부   : " + (res.isFree() ? "YES" : "NO"));
            System.out.println("현재 판매가 : " + res.getCurrentPrice() + "원");
            System.out.println("원래 가격   : " + res.getOriginalPrice() + "원");
            System.out.println("현재 할인율 : " + res.getDiscountPercent() + "%");
            System.out.println("장르 목록   : " + String.join(", ", res.getGenreNames()));
        }
        System.out.println("=".repeat(60) + "\n");

        // 검증: 30개 이하로 성공적으로 가져왔는지 확인
        assertThat(responses).isNotEmpty();
        assertThat(responses.size())
                .as("수집된 게임 데이터가 최소 10개 이상이어야 합니다.")
                .isGreaterThanOrEqualTo(10);
    }
}