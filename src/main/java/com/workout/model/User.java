package com.workout.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name = "users")
public class User extends BaseEntity {

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private Integer age;

  @Column(nullable = false)
  private String password;

  public User() {}

  public User(String username, Integer age, String password) {
    this.username = username;
    this.age = age;
    this.password = password;
  }


  // 基本的なgetterとsetter
  
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }


  // プロフィール専用での更新(重要度が高くなるなら切り出しも検討か)
  public void updateProfile(String username, Integer age) {
    if (username == null) throw new IllegalArgumentException("ユーザー名を入力してください");
    if (age < 0) throw new IllegalArgumentException("年齢は0以上で入力してください");
    this.username = username;
    this.age = age;
  }
} 

