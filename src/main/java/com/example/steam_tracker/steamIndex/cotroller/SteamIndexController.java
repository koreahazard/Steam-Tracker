package com.example.steam_tracker.steamIndex.cotroller;

import com.example.steam_tracker.common.ResponseForm;
import com.example.steam_tracker.steamIndex.service.SteamIndexService;
import com.example.steam_tracker.steamIndex.service.response.SteamIndexResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/steam-index")
public class SteamIndexController {
	private final SteamIndexService steamIndexService;

	@GetMapping("/history")
	public ResponseEntity<ResponseForm<List<SteamIndexResponse>>> getIndexHistory(
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "30") int size
	) {
		log.info("스팀 지수 기록 조회 요청 - page: {}, size: {}", page, size);
		List<SteamIndexResponse> response = steamIndexService.getIndexHistory(page, size);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ResponseForm.success(
						"SUCCESS",
						"스팀 지수 기록 조회 성공",
						response
				));
	}

}
