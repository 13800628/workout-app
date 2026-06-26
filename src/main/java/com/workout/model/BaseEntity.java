package com.workout.model;

import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.GenerationType;

@MappedSuperclass
public abstract class BaseEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public Long getId() { return id;}
  public void setId(Long id) { this.id = id; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public LocalDateTime getUpdateAt() { return updatedAt; }
}
