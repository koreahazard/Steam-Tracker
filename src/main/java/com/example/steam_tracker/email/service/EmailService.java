package com.example.steam_tracker.email.service;

import com.example.steam_tracker.wisiList.entity.TargetType;

public interface EmailService {
	void sendWishListAlert(String email, String gameName, TargetType targetType, int currentValue);

}
