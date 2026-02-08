package com.example.steam_tracker.steam_scraping.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Slf4j
@Getter
@RequiredArgsConstructor
@Service
public class SteamScrapingServiceImpl implements SteamScrapingService {

    @Override
    public Set<Integer> scrapingSteamAppId(int startRankIndex, int totalCount) {
        int batchSize = 100;
        // 5,000개를 목표로 할 때 중복/번들을 대비해 20% 더 긁음 (약 6,000개까지 시도)
        double overfetchRatio = 1.2;
        Set<Integer> set = new HashSet<>();
        Random random = new Random();

        log.info(">>> 스팀 AppID 수집 시작 (목표: 약 {}개)", totalCount);

        for (int start = startRankIndex; start < (int) (totalCount * overfetchRatio); start += batchSize) {
            String url = String.format(
                    "https://store.steampowered.com/search/results/?query=&start=%d&count=%d&dynamic_data=&sort_by=ConcurrentUsers_DESC&snr=1_7_7_7000_7",
                    start, batchSize);

            try {
                log.info("요청 중: {}", url);

                Document document = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36")
                        .timeout(10000)
                        .get();

                document.select("a[data-ds-appid]").forEach(element -> {
                    String rawAppIds = element.attr("data-ds-appid");

                    // 핵심 수정: 콤마(,)가 포함된 번들 ID 처리
                    if (rawAppIds.contains(",")) {
                        for (String id : rawAppIds.split(",")) {
                            addAppIdToSet(set, id.trim());
                        }
                    } else {
                        addAppIdToSet(set, rawAppIds.trim());
                    }
                });

                log.info("현재까지 수집된 고유 AppID 개수: {}", set.size());

                // 5,000개 수집 시 너무 오래 걸리지 않게 슬립 시간을 살짝 조정 (2~5초 랜덤)
                Thread.sleep(2000 + random.nextInt(3000));

            } catch (IOException e) {
                log.error("URL 요청 실패 (건너뜀): {}", url, e);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("수집 중단됨", ie);
                break;
            }
        }

        log.info(">>> 수집 완료! 최종 수집 개수: {}", set.size());
        return set;
    }

    // 숫자로 변환 시 발생할 수 있는 에러 대비 숫자형 문자 "777" 이 있어야하는데 "77ㄱ" 이런식의 문자가 석여있는 경우 대비
    private void addAppIdToSet(Set<Integer> set, String idStr) {
        try {
            if (!idStr.isEmpty()) {
                set.add(Integer.parseInt(idStr));
            }
        } catch (NumberFormatException e) {
            log.warn("잘못된 AppID 형식 무시: {}", idStr);
        }
    }
}
