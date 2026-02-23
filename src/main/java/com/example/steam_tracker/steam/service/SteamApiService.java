package com.example.steam_tracker.steam.service;


import java.util.List;

public interface SteamApiService {
	String getGameDetail(Long appId);

	String getPriceOverview(Long appId);
}
