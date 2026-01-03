package com.workout.controller;

// Controllerクラスで使うWorkoutRequestクラスの定義
// コードはシンプル
/**
* request.getName(), request.getReps(), request.getSets() のGetter/Setter
 */
public class WorkoutRequest {
  private String name;
  private Integer reps;
  private Integer sets;
  private Integer weights;
  private Long userId;
  
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

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public void setUser(Long userId) {
    this.userId = userId;
  }
}
