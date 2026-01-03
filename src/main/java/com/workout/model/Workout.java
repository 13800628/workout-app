package com.workout.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "workouts")
public class Workout {
  
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Integer reps;

  @Column(nullable = false)
  private Integer sets;
  
  @Column(nullable = true)
  private Integer weights;


  @ManyToOne(optional= true)
  @JoinColumn(name = "user_id", nullable = true)
  private User user;


  
  public Workout() {}

  public Workout(String name, Integer reps, Integer sets, Integer weights, User user) {
    this.name = name;
    this.reps = reps;
    this.sets = sets;
    this.weights = weights;
    this.user = user;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getReps() {
    return reps;
  }

  public void setReps(Integer reps) {
    this.reps = reps;
  }

  public Integer getSets() {
    return sets;
  }

  public void setSets(Integer sets) {
    this.sets = sets;
  }

  public Integer getWeights() {
    return weights;
  }

  public void setWeights(Integer weights) {
    this.weights = weights;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void updateAllWorkoutDetails(String name, Integer reps, Integer sets, Integer weights) {
    if (name == null) throw new IllegalArgumentException("種目名を入力してください");
    if (reps < 0) throw new IllegalArgumentException("回数は0回以上にしてください");
    if (sets < 0) throw new IllegalArgumentException("セット数は0回以上にしてください");
    if (weights < -1) throw new IllegalArgumentException("重量にマイナスは入力できません");
    this.name = name;
    this.reps = reps;
    this.sets = sets;
    this.weights = weights;
  }
}
