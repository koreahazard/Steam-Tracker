package com.example.steam_tracker.game.service;

import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;
import com.example.steam_tracker.steam.facade.response.CollectPriceDataResponse;

import java.util.List;

public interface GameService {
    //최초 1회 게임 데이터 저장
    void saveInitData(List<CollectGameDataResponse> dataList);
    //매일 실행
    //updateGamePrices
    //savePriceHistories
    //updateData로 한방에 하면될듯 프라이스히스토리랑 게임엔티티 업데이트 할거 체크
    void updatePriceData(List<CollectPriceDataResponse> dataList);
    boolean isEmpty();
    List<Long> getTrackingAppIds();
}
