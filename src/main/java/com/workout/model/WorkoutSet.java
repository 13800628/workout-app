package com.workout.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "workout_sets")
public class WorkoutSet extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "workout_id", nullable = false)
  private Workout workout;

  @Column(name = "set_order", nullable = false)
  private Integer setOrder;

  @Column(nullable = false)
  private Integer reps;

  @Column(nullable = false)
  private Integer weights;

  protected WorkoutSet() {
  }

  public WorkoutSet(Integer reps, Integer weights) {
    if (reps == null || weights == null) {
      throw new IllegalArgumentException("回数と重量は必須です");
    }
    if (reps < 0)
      throw new IllegalArgumentException("回数は0回以上にしてください");
    if (weights < 0)
      throw new IllegalArgumentException("重量にマイナスは使えません");
    this.reps = reps;
    this.weights = weights;
  }

  void attachTo(Workout workout, int setOrder) {
    this.workout = workout;
    this.setOrder = setOrder;
  }

  public Workout getWorkout() {
    return workout;
  }

  public Integer getSetOrder() {
    return setOrder;
  }

  public Integer getReps() {
    return reps;
  }

  public Integer getWeights() {
    return weights;
  }
}
