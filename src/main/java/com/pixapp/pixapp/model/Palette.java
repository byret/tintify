package com.pixapp.pixapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Palette {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    @Column(nullable = false)
    private List<String> colors;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonProperty("isPublic")
    @Column(name = "is_public")
    private boolean isPublic;

    @Column(nullable = false)
    private int likes = 0;
}
