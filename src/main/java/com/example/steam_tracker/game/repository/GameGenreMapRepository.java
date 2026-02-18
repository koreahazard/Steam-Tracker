package com.example.steam_tracker.game.repository;

import com.example.steam_tracker.game.entity.GameGenreMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameGenreMapRepository extends JpaRepository<GameGenreMap,Long> {
}
