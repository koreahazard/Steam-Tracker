package com.example.steam_tracker.game.controller;

import com.example.steam_tracker.common.ResponseForm;
import com.example.steam_tracker.game.service.GameService;
import com.example.steam_tracker.game.service.response.GameListResponse;
import com.example.steam_tracker.game.service.response.GenreResponse;
import com.example.steam_tracker.game.service.response.PriceHistoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game")
public class GameController {

	private final GameService gameService;

	@GetMapping("/genre")
	public ResponseEntity<ResponseForm<List<GenreResponse>>> getAllGenres() {
		log.info("장르 목록 조회 요청");
		List<GenreResponse> response = gameService.getAllGenres();
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ResponseForm.success(
						"SUCCESS",
						"장르 목록 조회 성공",
						response
				));
	}

	@GetMapping
	public ResponseEntity<ResponseForm<List<GameListResponse>>> getGameList(
			@RequestParam(required = false) List<Long> genreIds,
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "20") int size,
			@RequestParam(required = false, defaultValue = "default") String sortBy
	) {
		log.info("게임 목록 조회 요청 - genreIds: {}, page: {}, size: {}, sortBy: {}", genreIds, page, size, sortBy);
		List<GameListResponse> response;
		if (genreIds == null || genreIds.isEmpty()) {
			response = gameService.getGameList(page, size, sortBy);
		} else {
			response = gameService.getGameListByGenres(genreIds, page, size, sortBy);
		}
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ResponseForm.success(
						"SUCCESS",
						"게임 목록 조회 성공",
						response
				));
	}

	@GetMapping("/{appId}/price-history")
	public ResponseEntity<ResponseForm<List<PriceHistoryResponse>>> getPriceHistory(
			@PathVariable Long appId,
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "30") int size
	) {
		log.info("가격 기록 조회 요청 - appId: {}, page: {}, size: {}", appId, page, size);
		List<PriceHistoryResponse> response = gameService.getPriceHistory(appId, page, size);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ResponseForm.success(
						"SUCCESS",
						"가격 기록 조회 성공",
						response
				));
	}
}