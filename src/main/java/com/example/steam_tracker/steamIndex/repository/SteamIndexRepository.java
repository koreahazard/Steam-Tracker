package com.example.steam_tracker.steamIndex.repository;

import com.example.steam_tracker.steamIndex.entity.SteamIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SteamIndexRepository extends JpaRepository<SteamIndex, Long> {
	Page<SteamIndex> findAllByOrderByRecordDateDesc(Pageable pageable);
	SteamIndex findByRecordDate(LocalDate recordDate);
}
