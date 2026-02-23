package com.example.steam_tracker.wisiList.controller.requestForm;

import com.example.steam_tracker.wisiList.entity.TargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
public class AddWishListRequestForm {
	private Long appId;
	private TargetType targetType;
	private int targetValue;
}
