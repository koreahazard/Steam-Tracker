package com.example.steam_tracker.steam.service;

import com.example.steam_tracker.game.entity.Game;
import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;
import com.example.steam_tracker.steam.facade.response.CollectPriceDataResponse;

import java.util.List;

public interface SteamParsingService {

	List<CollectGameDataResponse> parseGameDetail(List<String> rawDataList);

	List<CollectPriceDataResponse> parsePriceOverview(List<String> rawDataList);
}
