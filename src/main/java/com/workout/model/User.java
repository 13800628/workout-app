package com.workout.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "users")
public class User {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private Integer age;

  public User() {}

  public User(String username, Integer age) {
    this.username = username;
    this.age = age;
  }


  // 基本的なgetterとsetter
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
  
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  // 各メソッドの実装
  public void updateProfile(String username, Integer age) {
    if (username == null) throw new IllegalArgumentException("ユーザー名を入力してください");
    if (age < 0) throw new IllegalArgumentException("年齢は0以上で入力してください");
    this.username = username;
    this.age = age;

  }
} 

