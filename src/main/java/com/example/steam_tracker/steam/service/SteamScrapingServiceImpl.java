package com.example.steam_tracker.steam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


@Slf4j
@RequiredArgsConstructor
@Service
public class SteamScrapingServiceImpl implements SteamScrapingService {
    //totalcount를 2와같은 작은 수로 요청해도 최소 24개 나오는듯함
    @Override
    public Set<Long> scrapingSteamAppId(int startRankIndex, int batchsize) {
        Set<Long> set = new HashSet<>();
        String url = String.format(
                "https://store.steampowered.com/search/results/?query=&start=%d&count=%d&dynamic_data=&sort_by=ConcurrentUsers_DESC&snr=1_7_7_7000_7",
                startRankIndex, batchsize);

        try {
            log.info("스팀 AppID 페이지 요청 중: {}", url);

            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get();

            document.select("a[data-ds-appid]").forEach(element -> {
                String rawAppIds = element.attr("data-ds-appid");
                if (rawAppIds.contains(",")) {
                    for (String id : rawAppIds.split(",")) {
                        addAppIdToSet(set, id.trim());
                    }
                } else {
                    addAppIdToSet(set, rawAppIds.trim());
                }
            });

        } catch (IOException e) {
            log.error("URL 요청 실패: {}", url, e);
        }

        return set;
    }

    private void addAppIdToSet(Set<Long> set, String idStr) {
        try {
            if (!idStr.isEmpty()) {
                set.add(Long.parseLong(idStr));
            }
        } catch (NumberFormatException e) {
            log.warn("잘못된 AppID 형식 무시: {}", idStr);
        }
    }
}