package com.example.steam_tracker.game.service.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GenreResponse {
    private final Long genreId;
    private final String genreName;
}
