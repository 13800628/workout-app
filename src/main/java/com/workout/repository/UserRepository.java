package com.workout.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workout.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
  
}


