package com.example.steam_tracker.game.repository;

import com.example.steam_tracker.game.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {
	List<Genre> findAll();
}
