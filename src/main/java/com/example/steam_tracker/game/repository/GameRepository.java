package com.example.steam_tracker.game.repository;

import com.example.steam_tracker.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game,Long> {
        List<Game> findAllByTrackingTrue();

}
