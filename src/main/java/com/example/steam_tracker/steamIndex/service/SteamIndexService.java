package com.example.steam_tracker.steamIndex.service;

import com.example.steam_tracker.steamIndex.service.response.SteamIndexResponse;

import java.util.List;

public interface SteamIndexService {
	void recordDailyIndex(List<Long> targetAppIdList);

	List<SteamIndexResponse> getIndexHistory(int page, int size);
}
