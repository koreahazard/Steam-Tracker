package com.example.steam_tracker.game.entity;

import com.example.steam_tracker.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name="game")
public class Game extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(nullable = false, unique = true)
    private Long appId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int currentPrice;

    @Column(nullable = false)
    private int originalPrice;

    @Column(nullable = false)
    private int discountPercent;

    @Column(nullable = false)
    boolean isTracking = true;

    public Game(Long appId, String name, int currentPrice, int originalPrice, int discountPercent) {
        this.appId = appId;
        this.name = name;
        this.currentPrice = currentPrice;
        this.originalPrice = originalPrice;
        this.discountPercent = discountPercent;

    }

}

