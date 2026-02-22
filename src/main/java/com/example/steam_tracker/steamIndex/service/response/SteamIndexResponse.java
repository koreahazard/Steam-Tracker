package com.example.steam_tracker.steamIndex.service.response;

import com.example.steam_tracker.steamIndex.entity.SteamIndex;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
public class SteamIndexResponse {
    private final LocalDate recordDate;
    private final Double indexValue;
    private final Long totalGameCount;

    public SteamIndexResponse(SteamIndex steamIndex) {
        this.recordDate = steamIndex.getRecordDate();
        this.indexValue = steamIndex.getIndexValue();
        this.totalGameCount = steamIndex.getTotalGameCount();
    }
}
