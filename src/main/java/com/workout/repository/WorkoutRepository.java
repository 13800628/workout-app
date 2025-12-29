package com.workout.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workout.model.Workout;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
 
  @EntityGraph(attributePaths = {"user"})
  List<Workout> findByUserId(Long id);
}
