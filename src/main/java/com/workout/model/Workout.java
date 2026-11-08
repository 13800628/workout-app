package com.workout.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "workouts")
public class Workout extends BaseEntity {

  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("setOrder ASC")
  private List<WorkoutSet> sets = new ArrayList<>();

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public Workout() {
  }

  public Workout(String name, User user) {
    this.name = name;
    this.user = user;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<WorkoutSet> getSets() {
    return Collections.unmodifiableList(sets);
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void replaceSets(List<WorkoutSet> newSets) {
    if (newSets == null) {
      throw new IllegalArgumentException("セットを指定してください");
    }
    this.sets.clear();
    int order = 1;
    for (WorkoutSet s : newSets) {
      s.attachTo(this, order++);
      this.sets.add(s);
    }
  }

  public void updateAllWorkoutDetails(String name, List<WorkoutSet> newSets) {

    if (name == null || newSets == null) {
      throw new IllegalArgumentException("全ての項目を入力してください");
    }
    this.name = name;
    replaceSets(newSets);
  }
}
