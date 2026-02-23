package com.example.steam_tracker.steamIndex.service;

import com.example.steam_tracker.game.service.GameService;
import com.example.steam_tracker.game.service.response.GameIndexCalculationDataResponse;
import com.example.steam_tracker.steamIndex.entity.SteamIndex;
import com.example.steam_tracker.steamIndex.repository.SteamIndexRepository;
import com.example.steam_tracker.steamIndex.service.response.SteamIndexResponse;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class SteamIndexServiceImpl implements SteamIndexService {
	private final GameService gameService;
	private final SteamIndexRepository steamIndexRepository;

	@Override
	@Transactional
	public void recordDailyIndex(List<Long> targetAppIdList) {
		if (targetAppIdList == null || targetAppIdList.isEmpty()) {
			log.warn("지수 기록 중단: 대상 appId 리스트가 비어있습니다.");
			return;
		}
		GameIndexCalculationDataResponse data = gameService.getGameIndexCalculationData(targetAppIdList);

		if (data.getTotalOriginalPrice() == 0) {
			log.warn("지수 기록 중단: 정가 합계가 0입니다.");
			return;
		}
		double indexValue = ((double) data.getTotalCurrentPrice() / data.getTotalOriginalPrice()) * 1000;
		indexValue = Math.round(indexValue * 100.0) / 100.0;

		LocalDate today = LocalDate.now();

		SteamIndex steamIndex = new SteamIndex(
				today,
				indexValue,
				data.getTotalOriginalPrice(),
				data.getTotalCurrentPrice(),
				data.getTotalGameCount()
		);
		steamIndexRepository.save(steamIndex);
		log.info("Steam 시장 지수 기록 완료 (날짜: {}, 지수: {}, 게임 수: {})", today, indexValue, data.getTotalGameCount());

	}

	@Override
	public List<SteamIndexResponse> getIndexHistory(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<SteamIndex> result = steamIndexRepository.findAllByOrderByRecordDateDesc(pageable);

		List<SteamIndexResponse> responseList = new ArrayList<>();
		for (SteamIndex steamIndex : result) {
			responseList.add(new SteamIndexResponse(steamIndex));
		}
		Collections.reverse(responseList);
		return responseList;

	}

}