package com.example.steam_tracker.game.repository;

import com.example.steam_tracker.game.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory,Long> {
}