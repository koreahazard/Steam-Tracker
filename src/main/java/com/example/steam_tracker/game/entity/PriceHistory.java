package com.example.steam_tracker.game.entity;

import com.example.steam_tracker.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "price_history")
public class PriceHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id",referencedColumnName = "appId")
    private Game game;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int discountPercent;

    @Column(nullable = false)
    private LocalDate snapshotDate;

    public PriceHistory(Game game,int price,int discountPercent, LocalDate snapshotDate)
    {
        this.game = game;
        this.price = price;
        this.discountPercent = discountPercent;
        this.snapshotDate = snapshotDate;
    }

}
