package com.example.steam_tracker.game.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class GameServiceImpl implements GameService{
    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final GameGenreMapRepository gameGenreMapRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    @Override
    @Transactional
    public void saveInitData(List<CollectGameDataResponse> dataList) {
        List<Game> gameList = new ArrayList<>();
        List<PriceHistory> priceHistoryList = new ArrayList<>();
        List<GameGenreMap> gameGenreMapList = new ArrayList<>();
        Map<String, Genre> genreMap = new HashMap<>();
        LocalDate snapshotDate = LocalDate.now();
        for (CollectGameDataResponse data : dataList) {


            Game game = new Game(
                    data.getAppId(),
                    data.getName(),
                    data.getCurrentPrice(),
                    data.getOriginalPrice(),
                    data.getDiscountPercent()
            );
            gameList.add(game);

            PriceHistory history = new PriceHistory(
                    game,
                    data.getCurrentPrice(),
                    data.getDiscountPercent(),
                    snapshotDate
            );

            priceHistoryList.add(history);


            for (String genreName : data.getGenreNames()) {
                //Action 장르가 처음 나오면 새로 만들고, 이미 있으면 기존 것 가져옴
                Genre genre = genreMap.computeIfAbsent(genreName, name -> new Genre(name));

                // 배그(game)와 장르(genre)를 연결하는 줄을 하나 긋는 과정 (매핑 엔티티 생성)
                GameGenreMap mapping = new GameGenreMap(game, genre);
                gameGenreMapList.add(mapping);
            }


        }
        gameRepository.saveAll(gameList);
        genreRepository.saveAll(genreMap.values());
        priceHistoryRepository.saveAll(priceHistoryList);
        gameGenreMapRepository.saveAll(gameGenreMapList);

        log.info("{}개의 게임, {}개의 장르, {}개의 매핑 데이터가 저장되었습니다.",
                gameList.size(), genreMap.size(), gameGenreMapList.size());
    }
    @Override
    @Transactional
    public void updatePriceData(List<CollectPriceDataResponse> dataList) {

        List<Game> trackingGames = gameRepository.findAllByTrackingTrue();

        // 조회를 빠르게 하기 위해 appId를 키로 하는 Map 생성
        Map<Long, Game> gameMap = new HashMap<>();
        for (Game g : trackingGames) {
            gameMap.put(g.getAppId(), g);
        }

        List<PriceHistory> newPriceHistories = new ArrayList<>();
        LocalDate snapshotDate = LocalDate.now();

        //외부에서 가져온 최신 데이터(dataList)를 순회
        for (CollectPriceDataResponse data : dataList) {
            Game game = gameMap.get(data.getAppId());

            // DB에 있고, 추적 중인 게임인 경우에만 처리
            if (game != null) {

                // 새로운 가격 이력 생성
                if(data.getCurrentPrice()==0 && data.getOriginalPrice() ==0 ) {

                    game.thisIsFreeGame(
                            data.getCurrentPrice(),
                            data.getOriginalPrice(),
                            data.getDiscountPercent(),
                            false
                    );
                }
                else {
                    //dirty checking
                    game.updatePrice(
                            data.getCurrentPrice(),
                            data.getOriginalPrice(),
                            data.getDiscountPercent()
                    );
                    PriceHistory history = new PriceHistory(
                            game,
                            data.getCurrentPrice(),
                            data.getDiscountPercent(),
                            snapshotDate
                    );
                    newPriceHistories.add(history);
                }

            }
        }

        priceHistoryRepository.saveAll(newPriceHistories);

        log.info("가격 업데이트 완료: {}개의 게임 상태 변경 및 이력 추가", newPriceHistories.size());
    }
    @Override
    public boolean isEmpty() {
        return gameRepository.count() == 0;
    }
    @Override
    public List<Long> getTrackingAppIds() {
        List<Game> trackingGames = gameRepository.findAllByTrackingTrue();
        List<Long> appIdList = new ArrayList<>();
        for (Game game : trackingGames) {
            appIdList.add(game.getAppId());
        }
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
        for (Genre genre : genres) {
            responseList.add(new GenreResponse(genre.getGenreId(), genre.getGenreName()));
        }
        return responseList;
    }
    @Override
    public List<GameListResponse> getGameList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Game> games = gameRepository.findAllByTrackingTrue(pageable);
        List<GameListResponse> responseList = new ArrayList<>();
        for (Game game : games) {
            responseList.add(new GameListResponse(game));
        }
        return responseList;
    }

    @Override
    public List<GameListResponse> getGameListByGenres(List<Long> genreIds, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Game> games = gameRepository.findByGenreIds(genreIds, pageable);
        List<GameListResponse> responseList = new ArrayList<>();
        for (Game game : games) {
            responseList.add(new GameListResponse(game));
        }
        return responseList;
    }
    @Override
    public  List<PriceHistoryResponse> getPriceHistory(Long appId, int page, int size) {
        Game game = gameRepository.findByAppId(appId)
                .orElseThrow(() -> new RuntimeException("게임을 찾을 수 없습니다."));
        Pageable pageable = PageRequest.of(page, size);
        Page<PriceHistory> priceHistories = priceHistoryRepository.findByGameOrderBySnapshotDateDesc(game, pageable);
        List<PriceHistoryResponse> responseList = new ArrayList<>();
        for (PriceHistory priceHistory : priceHistories) {
            responseList.add(new PriceHistoryResponse(priceHistory));
        }
        Collections.reverse(responseList);
        return responseList;
    }

}
