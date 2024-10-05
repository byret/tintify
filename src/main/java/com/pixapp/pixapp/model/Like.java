package com.pixapp.pixapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
@Getter
@Setter
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "palette_id", nullable = false)
    private Palette palette;

    @Column(name = "liked_at", nullable = false, updatable = false)
    private LocalDateTime likedAt;

    @PrePersist
    protected void onLike() {
        this.likedAt = LocalDateTime.now();
    }
}
