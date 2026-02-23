package com.example.steam_tracker.game.repository;

import com.example.steam_tracker.game.entity.GameGenreMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameGenreMapRepository extends JpaRepository<GameGenreMap,Long> {

}
