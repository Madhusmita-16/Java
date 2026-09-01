package com.riddle.airiddlegame.repository;

import com.riddle.airiddlegame.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByUserIdOrderByStartedAtDesc(Long userId);
    Optional<Game> findByIdAndUserId(Long id, Long userId);
    List<Game> findByUserIdAndStatus(Long userId, Game.GameStatus status);
}
