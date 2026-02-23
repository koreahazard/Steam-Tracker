package com.example.steam_tracker.wisiList.entity;

import com.example.steam_tracker.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "wish_list")
public class WishList extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long wishListId;

	@Column(nullable = false)
	private Long accountId;

	@Column(nullable = false)
	private Long appId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TargetType targetType;

	@Column(nullable = false)
	private int targetValue;

	@Column
	private Integer lastTriggeredValue;

	@Column(nullable = false)
	private boolean inTargetRange = false;

	public WishList(Long accountId, Long appId, TargetType targetType, int targetValue) {
		this.accountId = accountId;
		this.appId = appId;
		this.targetType = targetType;
		this.targetValue = targetValue;
	}

	public void updateInTargetRange(boolean inTargetRange) {
		this.inTargetRange = inTargetRange;
	}

	public void updateTriggered(int currentValue) {
		this.inTargetRange = true;
		this.lastTriggeredValue = currentValue;
	}

}
