package com.example.steam_tracker.steam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
@RequiredArgsConstructor
public class SteamApiServiceImpl implements SteamApiService {

    private final RestTemplate restTemplate;

    @Override
    public String getGameDetail(Integer appId) {

        String rawData = null;

            try {
                String url = String.format("https://store.steampowered.com/api/appdetails?appids=%d&cc=kr&l=korean", appId);
                String response = restTemplate.getForObject(url, String.class);
                if (response != null) {
                    log.info("AppID {} 수집 성공", appId);
                    rawData = response;
                }

            } catch (Exception e) {
                log.error("AppID {} 수집 중 에러 발생: {}", appId, e.getMessage());
                // 에러가 나도 다음 게임은 가져와야 하므로 계속 진행
            }

        return rawData;

    }

}
