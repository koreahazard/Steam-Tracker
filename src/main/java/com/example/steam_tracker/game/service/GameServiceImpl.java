package com.example.steam_tracker.game.service;

import com.example.steam_tracker.game.entity.Game;
import com.example.steam_tracker.game.entity.GameGenreMap;
import com.example.steam_tracker.game.entity.Genre;
import com.example.steam_tracker.game.entity.PriceHistory;
import com.example.steam_tracker.game.repository.GameGenreMapRepository;
import com.example.steam_tracker.game.repository.GameRepository;
import com.example.steam_tracker.game.repository.GenreRepository;
import com.example.steam_tracker.game.repository.PriceHistoryRepository;
import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public boolean isEmpty() {
        return gameRepository.count() == 0;
    }

}
