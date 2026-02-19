package com.example.steam_tracker.steam.facade;

import com.example.steam_tracker.steam.facade.request.CollectGameDataRequest;
import com.example.steam_tracker.steam.facade.request.CollectPriceDataRequest;
import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;
import com.example.steam_tracker.steam.facade.response.CollectPriceDataResponse;
import com.example.steam_tracker.steam.service.SteamApiService;
import com.example.steam_tracker.steam.service.SteamParsingService;
import com.example.steam_tracker.steam.service.SteamScrapingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class SteamCollectorFacade {
    private final SteamScrapingService steamScrapingService;
    private final SteamApiService steamApiService;
    private final SteamParsingService steamParsingService;


    public List<CollectGameDataResponse> collectGameData(CollectGameDataRequest request) {

        int batchSize = 100;
        int start = request.getStartRankIndex();
        int count = request.getTotalCount();

        log.info("SteamAppId 스크래핑 시작 start={}, totalCount={}", start, count);

        Set<Long> appIds = new HashSet<>();

        int iterations = (int) Math.ceil((double) count / batchSize);
        for (int i = 0; i < iterations; i++) {

            int currentStart = start + i * batchSize;
            int currentBatchSize = Math.min(batchSize, count - i * batchSize);

            Set<Long> batchAppIds = steamScrapingService.scrapingSteamAppId(currentStart, currentBatchSize);
            appIds.addAll(batchAppIds);
            if (i < iterations - 1) {
                try {
                    // 2000ms(2초) ~ 5000ms(5초) 사이의 랜덤한 값 추출
                    long randomDelay = ThreadLocalRandom.current().nextLong(2000, 5001);

                    log.info("Rate Limit 회피를 위해 {}ms 동안 대기합니다...", randomDelay);
                    Thread.sleep(randomDelay);

                } catch (InterruptedException e) {
                    log.error("수집 중 인터럽트 발생: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        log.info("스크래핑 완료, 수집한 아이디 개수 ={}", appIds.size());

        List<Long> appIdList = new ArrayList<>(appIds);
        List<String> rawDataList = new ArrayList<>();

        log.info("rawData {}개 수집 시작",appIdList.size());
        for (int i = 0; i < appIdList.size(); i++) {

            Long currentAppId = appIdList.get(i);
            String rawData = steamApiService.getGameDetail(currentAppId);

            if (rawData != null) {
                rawDataList.add(rawData);
            }

            if (i < appIdList.size() - 1) {
                try {
                    long randomDelay = ThreadLocalRandom.current().nextLong(2000, 5001);

                    log.info("[{}/{}] AppID {} 처리 완료. {}ms 대기...",
                            (i + 1), appIdList.size(), currentAppId, randomDelay);

                    Thread.sleep(randomDelay);

                } catch (InterruptedException e) {
                    log.error("수집 중 중단 요청 발생: {}", e.getMessage());
                    // 인터럽트 상태 복구 (기록 남기기)
                    Thread.currentThread().interrupt();
                    // 루프 즉시 종료 (비상 정지)
                    break;
                }
            }
        }

        log.info("rawData수집 완료, 수집한 rawData 개수 ={}", rawDataList.size());

        log.info("rawData 파싱 시작 - 대상 개수: {}개", rawDataList.size());

        List<CollectGameDataResponse> response = steamParsingService.parseGameDetail(rawDataList);

        log.info("rawData 파싱 완료 - 결과 개수: {}개", response.size());

        return response;


    }
    public List<CollectPriceDataResponse> collectPriceData(CollectPriceDataRequest request) {
        List<Long> appIdList = request.getAppIdList();
        List<String> rawDataList = new ArrayList<>();

        log.info("가격 업데이트를 위한 rawData {}개 수집 시작", appIdList.size());

        for (int i = 0; i < appIdList.size(); i++) {
            Long currentAppId = appIdList.get(i);
            String rawData = steamApiService.getPriceOverview(currentAppId);
            rawDataList.add(rawData);
            if (i < appIdList.size() - 1) {
                try {
                    long randomDelay = ThreadLocalRandom.current().nextLong(2000, 5001);
                    log.info("[{}/{}] AppID: {} 수집 완료, {}ms 대기...", (i + 1), appIdList.size(), currentAppId, randomDelay);
                    Thread.sleep(randomDelay);
                } catch (InterruptedException e) {
                    log.error("수집 중단 발생: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        log.info("가격 rawData 수집 완료 (총 {}건)", rawDataList.size());
        List<CollectPriceDataResponse> response = steamParsingService.parsePriceOverview(rawDataList);
        log.info("rawData 파싱 완료 - 결과 개수: {}개", response.size());
        return response;
    }
}
