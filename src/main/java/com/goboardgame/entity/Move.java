package com.goboardgame.entity;

import javax.persistence.*;

@Entity
@Table(name = "Move")
public class Move {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    // inne właściwości, np. pozycja, kolor kamienia

    // getter, setter
}
