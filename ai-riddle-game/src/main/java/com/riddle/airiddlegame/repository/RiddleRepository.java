package com.riddle.airiddlegame.repository;

import com.riddle.airiddlegame.entity.Riddle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiddleRepository extends JpaRepository<Riddle, Long> {
    List<Riddle> findByDifficulty(String difficulty);
    List<Riddle> findByCategoryId(Long categoryId);
    List<Riddle> findByDifficultyAndCategoryId(String difficulty, Long categoryId);

    @Query("SELECT r FROM Riddle r WHERE (:difficulty IS NULL OR r.difficulty = :difficulty) AND (:categoryId IS NULL OR r.category.id = :categoryId)")
    List<Riddle> findByFilter(@Param("difficulty") String difficulty, @Param("categoryId") Long categoryId);
}
