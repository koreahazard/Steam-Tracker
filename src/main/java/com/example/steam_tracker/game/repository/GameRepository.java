package com.example.steam_tracker.game.repository;

import com.example.steam_tracker.game.entity.Game;
import com.example.steam_tracker.game.service.response.GameIndexCalculationDataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
	List<Game> findAllByTrackingTrue();

	@Query("SELECT new com.example.steam_tracker.game.service.response.GameIndexCalculationDataResponse(" +
			"COUNT(g), SUM(g.originalPrice), SUM(g.currentPrice)) " +
			"FROM Game g WHERE g.appId IN :appIds AND g.tracking = true")
	GameIndexCalculationDataResponse getGameIndexCalculationData(@Param("appIds") List<Long> appIds);

	Page<Game> findAllByTrackingTrue(Pageable pageable);

	@Query("SELECT DISTINCT g FROM Game g JOIN GameGenreMap m ON m.game = g WHERE m.genre.genreId IN :genreIds AND g.tracking = true")
	Page<Game> findByGenreIds(@Param("genreIds") List<Long> genreIds, Pageable pageable);

	Optional<Game> findByAppId(Long appId);
}

