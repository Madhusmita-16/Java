package com.riddle.airiddlegame.repository;

import com.riddle.airiddlegame.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    Optional<Score> findByUserId(Long userId);

    @Query("SELECT s FROM Score s ORDER BY s.highestScore DESC, s.averageAccuracy DESC")
    List<Score> findTopLeaderboard();
}
