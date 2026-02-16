package com.example.steam_tracker.steam.service;

import com.example.steam_tracker.game.entity.Game;
import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;

import java.util.List;

public interface SteamParsingService {

    List<CollectGameDataResponse> parseGamedetail(List<String> rawDataList);
}
