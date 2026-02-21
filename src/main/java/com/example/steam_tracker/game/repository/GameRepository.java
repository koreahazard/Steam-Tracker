package com.example.steam_tracker.game.repository;

import com.example.steam_tracker.game.entity.Game;
import com.example.steam_tracker.game.service.response.GameIndexCalculationDataResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game,Long> {
        List<Game> findAllByTrackingTrue();

        @Query("SELECT new com.example.steam_tracker.game.service.response.GameIndexCalculationDataResponse(" +
                "COUNT(g), SUM(g.originalPrice), SUM(g.currentPrice)) " +
                "FROM Game g WHERE g.appId IN :appIds AND g.tracking = true")
        GameIndexCalculationDataResponse getGameIndexCalculationData(@Param("appIds") List<Long> appIds);
}

