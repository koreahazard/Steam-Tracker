package com.example.steam_tracker.game.repository;

import com.example.steam_tracker.game.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre,Long> {
}
