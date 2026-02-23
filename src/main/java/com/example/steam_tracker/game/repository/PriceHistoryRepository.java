package com.example.steam_tracker.game.repository;

import com.example.steam_tracker.game.entity.Game;
import com.example.steam_tracker.game.entity.PriceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
	Page<PriceHistory> findByGameOrderBySnapshotDateDesc(Game game, Pageable pageable);
}