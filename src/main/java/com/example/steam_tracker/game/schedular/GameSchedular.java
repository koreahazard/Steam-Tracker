package com.example.steam_tracker.game.schedular;

import com.example.steam_tracker.game.service.GameService;
import com.example.steam_tracker.steam.facade.SteamCollectorFacade;
import com.example.steam_tracker.steam.facade.request.CollectGameDataRequest;
import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class GameSchedular {

    private final SteamCollectorFacade steamCollectorFacade;
    private final GameService gameService;

    @EventListener(ApplicationReadyEvent.class)
    public void initialCollect() {
        int startRankIndex = 0;
        int totalCount = 100;
        CollectGameDataRequest request = new CollectGameDataRequest(startRankIndex,totalCount);
        if (gameService.isEmpty()) {
            List<CollectGameDataResponse> data = steamCollectorFacade.collectGameData(request);
            gameService.saveInitData(data);
        }
    }

}
