package com.workout.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.workout.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
  
  @Modifying
  @Query("DELETE FROM User u WHERE u.id = :id")
  int deleteDirectlyById(@Param("id") Long id);

  Optional<User> findByUsername(String username);
}


