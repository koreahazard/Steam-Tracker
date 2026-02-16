package com.example.steam_tracker.steam.service;

import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;
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
    public List<CollectGameDataResponse> parseGamedetail(List<String> rawDataList) {
        List<CollectGameDataResponse> parsedGameList = new ArrayList<>();

        for (String rawData : rawDataList) {

            try {
                JsonNode root = objectMapper.readTree(rawData);

                //appId key 추출
                Iterator<String> fieldNames = root.fieldNames();
                if (!fieldNames.hasNext()) continue;

                String appIdKey = fieldNames.next();
                JsonNode appNode = root.get(appIdKey);

                //success 체크
                if (appNode == null || !appNode.path("success").asBoolean(false)) {
                    continue;
                }

                JsonNode data = appNode.path("data");

                //type 체크 (game만 허용)
                if (!"game".equals(data.path("type").asText())) {
                    continue;
                }

                Long appId = Long.parseLong(appIdKey);
                String name = data.path("name").asText("");

                boolean isFree = data.path("is_free").asBoolean(false);

                int originalPrice = 0;
                int currentPrice = 0;
                int discountPercent = 0;

                //가격 처리
                if (!isFree && data.has("price_overview")) {
                    JsonNode price = data.path("price_overview");

                    originalPrice = price.path("initial").asInt(0) / 100;
                    currentPrice = price.path("final").asInt(0) / 100;
                    discountPercent = price.path("discount_percent").asInt(0);
                }

                //장르 처리
                List<String> genreNames = new ArrayList<>();
                if (data.has("genres")) {
                    for (JsonNode genre : data.path("genres")) {
                        genreNames.add(genre.path("description").asText());
                    }
                }

                CollectGameDataResponse parsedGameData = new CollectGameDataResponse(
                        appId,
                        name,
                        currentPrice,
                        originalPrice,
                        discountPercent,
                        isFree,
                        genreNames
                );

                parsedGameList.add(parsedGameData);

            } catch (Exception e) {
                log.warn("파싱 실패 - 해당 JSON 스킵", e);
            }
        }
        return parsedGameList;
    }
}
