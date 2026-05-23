package com.example.steam_tracker.game.service;

import com.example.steam_tracker.common.CustomException;
import com.example.steam_tracker.common.ErrorCode;
import com.example.steam_tracker.game.entity.Game;
import com.example.steam_tracker.game.entity.GameGenreMap;
import com.example.steam_tracker.game.entity.Genre;
import com.example.steam_tracker.game.entity.PriceHistory;
import com.example.steam_tracker.game.repository.GameGenreMapRepository;
import com.example.steam_tracker.game.repository.GameRepository;
import com.example.steam_tracker.game.repository.GenreRepository;
import com.example.steam_tracker.game.repository.PriceHistoryRepository;
import com.example.steam_tracker.game.service.response.GameIndexCalculationDataResponse;
import com.example.steam_tracker.game.service.response.GameListResponse;
import com.example.steam_tracker.game.service.response.GenreResponse;
import com.example.steam_tracker.game.service.response.PriceHistoryResponse;
import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;
import com.example.steam_tracker.steam.facade.response.CollectPriceDataResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class GameServiceImpl implements GameService {
	private final GameRepository gameRepository;
	private final GenreRepository genreRepository;
	private final GameGenreMapRepository gameGenreMapRepository;
	private final PriceHistoryRepository priceHistoryRepository;

	private Pageable buildPageable(int page, int size, String sortBy) {
		Sort sort = switch (sortBy) {
			case "discount" -> Sort.by(Sort.Direction.DESC, "discountPercent");
			case "price_asc" -> Sort.by(Sort.Direction.ASC, "currentPrice");
			case "price_desc" -> Sort.by(Sort.Direction.DESC, "currentPrice");
			default -> Sort.unsorted();
		};
		return sort.isUnsorted() ? PageRequest.of(page, size) : PageRequest.of(page, size, sort);
	}

	@Override
	@Transactional
	public void saveInitData(List<CollectGameDataResponse> dataList) {
		List<Game> gameList = new ArrayList<>();
		List<PriceHistory> priceHistoryList = new ArrayList<>();
		List<GameGenreMap> gameGenreMapList = new ArrayList<>();
		Map<String, Genre> genreMap = new HashMap<>();
		LocalDate snapshotDate = LocalDate.now();

		List<Genre> existingGenres = genreRepository.findAll();
		for (Genre genre : existingGenres) {
			genreMap.put(genre.getGenreName(), genre);
		}

		for (CollectGameDataResponse data : dataList) {
			Game game = new Game(data.getAppId(), data.getName(), data.getCurrentPrice(), data.getOriginalPrice(), data.getDiscountPercent());
			gameList.add(game);

			PriceHistory history = new PriceHistory(game, data.getCurrentPrice(), data.getDiscountPercent(), snapshotDate);
			priceHistoryList.add(history);

			for (String genreName : data.getGenreNames()) {
				Genre genre = genreMap.computeIfAbsent(genreName, name -> new Genre(name));
				GameGenreMap mapping = new GameGenreMap(game, genre);
				gameGenreMapList.add(mapping);
			}
		}

		gameRepository.saveAll(gameList);

		List<Genre> newGenres = new ArrayList<>();
		for (Genre genre : genreMap.values()) {
			if (genre.getGenreId() == null) newGenres.add(genre);
		}
		genreRepository.saveAll(newGenres);
		priceHistoryRepository.saveAll(priceHistoryList);
		gameGenreMapRepository.saveAll(gameGenreMapList);

		log.info("{}개의 게임, {}개의 장르, {}개의 매핑 데이터가 저장되었습니다.", gameList.size(), genreMap.size(), gameGenreMapList.size());
	}

	@Override
	@Transactional
	public void updatePriceData(List<CollectPriceDataResponse> dataList) {
		List<Game> trackingGames = gameRepository.findAllByTrackingTrue();
		Map<Long, Game> gameMap = new HashMap<>();
		for (Game g : trackingGames) gameMap.put(g.getAppId(), g);

		List<PriceHistory> newPriceHistories = new ArrayList<>();
		LocalDate snapshotDate = LocalDate.now();

		for (CollectPriceDataResponse data : dataList) {
			Game game = gameMap.get(data.getAppId());
			if (game != null) {
				if (data.getCurrentPrice() == 0 && data.getOriginalPrice() == 0) {
					game.thisIsFreeGame(data.getCurrentPrice(), data.getOriginalPrice(), data.getDiscountPercent(), false);
				} else {
					game.updatePrice(data.getCurrentPrice(), data.getOriginalPrice(), data.getDiscountPercent());
					PriceHistory history = new PriceHistory(game, data.getCurrentPrice(), data.getDiscountPercent(), snapshotDate);
					newPriceHistories.add(history);
				}
			}
		}

		priceHistoryRepository.saveAll(newPriceHistories);
		log.info("가격 업데이트 완료: {}개의 게임 상태 변경 및 이력 추가", newPriceHistories.size());
	}

	@Override
	public boolean isEmpty() { return gameRepository.count() == 0; }

	@Override
	public List<Long> getTrackingAppIds() {
		List<Game> trackingGames = gameRepository.findAllByTrackingTrue();
		List<Long> appIdList = new ArrayList<>();
		for (Game game : trackingGames) appIdList.add(game.getAppId());
		return appIdList;
	}

	@Override
	public GameIndexCalculationDataResponse getGameIndexCalculationData(List<Long> targetAppIdList) {
		return gameRepository.getGameIndexCalculationData(targetAppIdList);
	}

	@Override
	public List<GenreResponse> getAllGenres() {
		List<Genre> genres = genreRepository.findAll();
		List<GenreResponse> responseList = new ArrayList<>();
		for (Genre genre : genres) responseList.add(new GenreResponse(genre.getGenreId(), genre.getGenreName()));
		return responseList;
	}

	@Override
	public List<GameListResponse> getGameList(int page, int size, String sortBy) {
		Pageable pageable = buildPageable(page, size, sortBy);
		Page<Game> games = gameRepository.findAllByTrackingTrue(pageable);
		List<GameListResponse> responseList = new ArrayList<>();
		for (Game game : games) responseList.add(new GameListResponse(game));
		return responseList;
	}

	@Override
	public List<GameListResponse> getGameListByGenres(List<Long> genreIds, int page, int size, String sortBy) {
		Pageable pageable = buildPageable(page, size, sortBy);
		Page<Game> games = gameRepository.findByGenreIds(genreIds, (long) genreIds.size(), pageable);
		List<GameListResponse> responseList = new ArrayList<>();
		for (Game game : games) responseList.add(new GameListResponse(game));
		return responseList;
	}

	@Override
	public List<PriceHistoryResponse> getPriceHistory(Long appId, int page, int size) {
		Game game = gameRepository.findByAppId(appId).orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));
		Pageable pageable = PageRequest.of(page, size);
		Page<PriceHistory> priceHistories = priceHistoryRepository.findByGameOrderBySnapshotDateDesc(game, pageable);
		List<PriceHistoryResponse> responseList = new ArrayList<>();
		for (PriceHistory priceHistory : priceHistories) responseList.add(new PriceHistoryResponse(priceHistory));
		Collections.reverse(responseList);
		return responseList;
	}
}