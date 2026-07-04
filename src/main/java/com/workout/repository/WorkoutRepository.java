package com.workout.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.workout.model.Workout;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
 
  @EntityGraph(attributePaths = {"user"})
  List<Workout> findByUserId(Long id);

  @Modifying
  @Transactional
  @Query("DELETE FROM Workout w WHERE w.id = :id")
  int deleteDirectlyById(@Param("id") Long id);

  @Modifying
  @Transactional
  @Query("DELETE FROM Workout w WHERE w.id = :id AND w.user.id = :userId")
  int deleteDirectlyByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
