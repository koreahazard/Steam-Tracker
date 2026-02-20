package com.example.steam_tracker.game.schedular;

import com.example.steam_tracker.game.service.GameService;
import com.example.steam_tracker.steam.facade.SteamCollectorFacade;
import com.example.steam_tracker.steam.facade.request.CollectGameDataRequest;
import com.example.steam_tracker.steam.facade.request.CollectPriceDataRequest;
import com.example.steam_tracker.steam.facade.request.ExpandGameDataRequest;
import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;
import com.example.steam_tracker.steam.facade.response.CollectPriceDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
@RequiredArgsConstructor
public class GameSchedular {

    private final SteamCollectorFacade steamCollectorFacade;
    private final GameService gameService;

    @EventListener(ApplicationReadyEvent.class)
    public void initialCollect() {
        int startRankIndex = 0;
        int totalCount = 100;
        CollectGameDataRequest request = new CollectGameDataRequest(startRankIndex,totalCount);
        if (gameService.isEmpty()) {
            List<CollectGameDataResponse> data = steamCollectorFacade.collectGameData(request);
            gameService.saveInitData(data);
        }
        else {
            log.info("초기 데이터가 이미 존재하여 수집을 건너뜁니다.");
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정(00:00)에 실행
    public void periodicCollect() {
        log.info("주기적 업데이트 스케줄러 시작");
        int minimumConstituentCount = 2000;
        int startRankIndex = 2000;
        int totalCount = 1000;

        try {

            List<Long> targetAppIdList = gameService.getTrackingAppIds();
            if( targetAppIdList.size() < minimumConstituentCount )
            {
               ExpandGameDataRequest request = new ExpandGameDataRequest(targetAppIdList,startRankIndex,totalCount);
                List<CollectGameDataResponse> data = steamCollectorFacade.expandGameData(request);
                long randomDelay = ThreadLocalRandom.current().nextLong(2000, 5001);

                log.info("Rate Limit 회피를 위해 {}ms 동안 대기합니다...", randomDelay);
                Thread.sleep(randomDelay);
                gameService.saveInitData(data);
            }

            if (targetAppIdList.isEmpty()) {
                log.info("추적 중인 게임이 없어 스케줄러를 종료합니다.");
                return;
            }


            CollectPriceDataRequest request = new CollectPriceDataRequest(targetAppIdList);
            List<CollectPriceDataResponse> priceDataList = steamCollectorFacade.collectPriceData(request);

            gameService.updatePriceData(priceDataList);

            log.info("가격 정보 업데이트 완료");
        } catch (Exception e) {
            log.error("가격 정보 업데이트 중 오류 발생: {}", e.getMessage());
        }
    }



}
