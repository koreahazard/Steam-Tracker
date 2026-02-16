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
    private int concurrentPlayer;

    @Column(nullable = false)
    private int currentPrice;

    @Column(nullable = false)
    private int originalPrice;

    @Column(nullable = false)
    private int discountPercent;

    @Column(nullable = false)
    private double trendScore7d;

    @Column(nullable = false)
    boolean isActive = true;

}
//TODO: 생성자 만들어야함
