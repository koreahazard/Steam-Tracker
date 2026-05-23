package com.example.steam_tracker.game.service;

import com.example.steam_tracker.game.service.response.GameIndexCalculationDataResponse;
import com.example.steam_tracker.game.service.response.GameListResponse;
import com.example.steam_tracker.game.service.response.GenreResponse;
import com.example.steam_tracker.game.service.response.PriceHistoryResponse;
import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;
import com.example.steam_tracker.steam.facade.response.CollectPriceDataResponse;

import java.util.List;

public interface GameService {
	void saveInitData(List<CollectGameDataResponse> dataList);

	void updatePriceData(List<CollectPriceDataResponse> dataList);

	boolean isEmpty();

	List<Long> getTrackingAppIds();

	GameIndexCalculationDataResponse getGameIndexCalculationData(List<Long> targetAppIdList);

	List<GenreResponse> getAllGenres();

	List<GameListResponse> getGameList(int page, int size, String sortBy);

	List<GameListResponse> getGameListByGenres(List<Long> genreIds, int page, int size, String sortBy);

	List<PriceHistoryResponse> getPriceHistory(Long appId, int page, int size);
}