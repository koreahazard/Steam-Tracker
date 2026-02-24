package com.example.steam_tracker.steamIndex.entity;

import com.example.steam_tracker.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "steam_index")
public class SteamIndex extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long steamIndexId;

	@Column(nullable = false, unique = true)
	private LocalDate recordDate; // 지수 기록 날짜

	@Column(nullable = false)
	private Double indexValue; // 최종 지수 값

	@Column(nullable = false)
	private Long totalOriginalPrice; // 계산에 사용된 정가 총합

	@Column(nullable = false)
	private Long totalCurrentPrice; // 계산에 사용된 현재가 총합

	@Column(nullable = false)
	private Long totalGameCount; // 계산에 참여한 게임 수

	public SteamIndex(LocalDate recordDate, Double indexValue, Long totalOriginalPrice, Long totalCurrentPrice, Long totalGameCount) {
		this.recordDate = recordDate;
		this.indexValue = indexValue;
		this.totalOriginalPrice = totalOriginalPrice;
		this.totalCurrentPrice = totalCurrentPrice;
		this.totalGameCount = totalGameCount;
	}
	public void update(Double indexValue,
							Long totalOriginalPrice,
							Long totalCurrentPrice,
							Long totalGameCount) {
		this.indexValue = indexValue;
		this.totalOriginalPrice = totalOriginalPrice;
		this.totalCurrentPrice = totalCurrentPrice;
		this.totalGameCount = totalGameCount;
	}
}
