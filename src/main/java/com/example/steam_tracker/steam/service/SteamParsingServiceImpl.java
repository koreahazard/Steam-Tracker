package com.example.steam_tracker.steam.service;

import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;
import com.example.steam_tracker.steam.facade.response.CollectPriceDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SteamParsingServiceImpl implements SteamParsingService{

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<CollectGameDataResponse> parseGameDetail(List<String> rawDataList) {
        List<CollectGameDataResponse> parsedGameList = new ArrayList<>();

        for (String rawData : rawDataList) {
            try {
                JsonNode root = objectMapper.readTree(rawData);

                // 1. appId key 추출
                Iterator<String> fieldNames = root.fieldNames();
                if (!fieldNames.hasNext()) continue;

                String appIdKey = fieldNames.next();
                JsonNode appNode = root.get(appIdKey);

                // 2. success 체크 및 데이터 확보
                if (appNode == null || !appNode.path("success").asBoolean(false)) continue;
                JsonNode data = appNode.path("data");

                // 3. 타입 체크 (game만 수집)
                if (!"game".equals(data.path("type").asText())) continue;

                // 4. 가격 데이터 처리 (is_free 필드는 무시)
                int originalPrice = 0;
                int currentPrice = 0;
                int discountPercent = 0;

                if (data.has("price_overview")) {
                    JsonNode price = data.path("price_overview");

                    originalPrice = price.path("initial").asInt(0) / 100;
                    discountPercent = price.path("discount_percent").asInt(0);

                    if (discountPercent == 100) {
                        currentPrice = 0;
                    } else {
                        currentPrice = price.path("final").asInt(0) / 100;
                    }
                } else {
                    continue;
                }

                Long appId = Long.parseLong(appIdKey);
                String name = data.path("name").asText("Unknown");

                //장르 처리
                List<String> genreNames = new ArrayList<>();
                if (data.has("genres")) {
                    for (JsonNode genre : data.path("genres")) {
                        genreNames.add(genre.path("description").asText());
                    }
                }

                parsedGameList.add(new CollectGameDataResponse(
                        appId,
                        name,
                        currentPrice,
                        originalPrice,
                        discountPercent,
                        genreNames
                ));

            } catch (Exception e) {
                log.warn("AppID {} 파싱 중 에러 발생: {}", rawData, e.getMessage());
            }
        }
        return parsedGameList;
    }
    @Override
    public List<CollectPriceDataResponse> parsePriceOverview(List<String> rawDataList) {
        List<CollectPriceDataResponse> parsedPriceList = new ArrayList<>();

        for (String rawData : rawDataList) {
            try {
                JsonNode root = objectMapper.readTree(rawData);

                // 1. appId key 추출
                Iterator<String> fieldNames = root.fieldNames();
                if (!fieldNames.hasNext()) continue;

                String appIdKey = fieldNames.next();
                Long appId = Long.parseLong(appIdKey);
                JsonNode appNode = root.get(appIdKey);

                // 2. success 체크
                if (appNode == null || !appNode.path("success").asBoolean(false)) continue;

                JsonNode data = appNode.path("data");

                int originalPrice = 0;
                int currentPrice = 0;
                int discountPercent = 0;

                // 3. 데이터가 비어있는 경우 (data: []) 또는 price_overview가 없는 경우
                if ((data.isArray() && data.isEmpty()) || !data.has("price_overview")) {
                    log.info("AppID {} : 가격 정보 없음 (무료 혹은 판매중단). 0원으로 설정합니다.", appId);
                    // 모든 가격을 0으로 둔 채로 리스트에 추가
                    originalPrice = 0;
                    currentPrice = 0;
                    discountPercent = 0;
                } else {
                    // 4. 정상적인 가격 데이터가 있는 경우
                    JsonNode price = data.path("price_overview");

                    originalPrice = price.path("initial").asInt(0) / 100;
                    discountPercent = price.path("discount_percent").asInt(0);

                    // 할인율 100%이면 현재 가격 0으로
                    if (discountPercent == 100) {
                        currentPrice = 0;
                    } else {
                        currentPrice = price.path("final").asInt(0) / 100;
                    }
                }




                // 5. 결과 객체 생성 및 리스트 추가
                parsedPriceList.add(new CollectPriceDataResponse(
                        appId,
                        currentPrice,
                        originalPrice,
                        discountPercent
                ));

            } catch (Exception e) {
                log.warn("가격 파싱 중 에러 발생: {}", e.getMessage());
            }
        }
        return parsedPriceList;
    }
}
