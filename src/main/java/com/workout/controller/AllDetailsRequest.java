package com.workout.controller;

public class AllDetailsRequest {
  private String name;
  private Integer reps;
  private Integer sets;
  private Integer weights;

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

  public void setWeights() {
    this.weights = weights;
  }

}
