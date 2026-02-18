package com.example.steam_tracker.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name ="game_genre_map")
public class GameGenreMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameGenreMapId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id",referencedColumnName = "appId")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    public GameGenreMap(Game game, Genre genre) {
        this.game = game;
        this.genre = genre;
    }

}
