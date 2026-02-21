package com.example.steam_tracker.game.service;

import com.example.steam_tracker.game.service.response.GameIndexCalculationDataResponse;
import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;
import com.example.steam_tracker.steam.facade.response.CollectPriceDataResponse;

import java.util.List;

public interface GameService {
    //최초 1회 게임 데이터 저장
    void saveInitData(List<CollectGameDataResponse> dataList);
    void updatePriceData(List<CollectPriceDataResponse> dataList);
    boolean isEmpty();
    List<Long> getTrackingAppIds();
    GameIndexCalculationDataResponse getGameIndexCalculationData(List<Long> targetAppIdList);

}
