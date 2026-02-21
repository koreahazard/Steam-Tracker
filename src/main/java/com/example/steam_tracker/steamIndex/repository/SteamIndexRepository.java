package com.example.steam_tracker.steamIndex.repository;

import com.example.steam_tracker.steamIndex.entity.SteamIndex;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SteamIndexRepository extends JpaRepository<SteamIndex,Long> {
}
