package com.riddle.airiddlegame.repository;

import com.riddle.airiddlegame.entity.GameQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameQuestionRepository extends JpaRepository<GameQuestion, Long> {
    List<GameQuestion> findByGameIdOrderByQuestionOrderAsc(Long gameId);
    Optional<GameQuestion> findByGameIdAndQuestionOrder(Long gameId, Integer questionOrder);
}
