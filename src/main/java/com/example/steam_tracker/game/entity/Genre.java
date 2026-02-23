package com.example.steam_tracker.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "genre")
@lombok.EqualsAndHashCode(of = "genreName")
public class Genre {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long genreId;

	@Column(nullable = false, unique = true)
	private String genreName;

	public Genre(String genreName) {
		this.genreName = genreName;
	}

}
