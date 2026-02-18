package com.example.steam_tracker.game.service;

import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;

import java.util.List;

public interface GameService {
    //최초 1회 게임 데이터 저장
    void saveInitData(List<CollectGameDataResponse> dataList);
    //매일 실행
    //updateGamePrices
    //savePriceHistories
    boolean isEmpty();
}
