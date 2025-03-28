package dev.park.dailynews.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
}
