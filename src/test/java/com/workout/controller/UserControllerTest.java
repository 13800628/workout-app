package com.workout.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import static java.util.Objects.requireNonNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workout.config.SecurityConfig;
import com.workout.dto.UserRequest;
import com.workout.model.User;
import com.workout.service.UserService;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest  {
  
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void registerUser_正常系_ユーザーを新規登録し201を返す() throws Exception {
    UserRequest request = new UserRequest("テスト", 25, "password");

    User savedUser = new User();
    savedUser.setId(1L);
    savedUser.setUsername("テスト");
    savedUser.setAge(25);

    when(userService.registerUser(request)).thenReturn(savedUser);

    mockMvc.perform(post("/api/users")
        .with(csrf())
        .contentType(requireNonNull(MediaType.APPLICATION_JSON))
        .content(requireNonNull(objectMapper.writeValueAsString(request))))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.username").value("テスト"))
      .andExpect(jsonPath("$.age").value(25));
  }

  @Test
  void registerUser_異常系_バリデーションエラー時は400を返す() throws Exception {
    UserRequest request = new UserRequest("", 25, "password");

    when(userService.registerUser(request))
        .thenThrow(new IllegalArgumentException("ユーザー名は必須です"));

    mockMvc.perform(post("/api/users")
        .contentType(requireNonNull(MediaType.APPLICATION_JSON))
        .content(requireNonNull(objectMapper.writeValueAsBytes(request))))
      .andDo(print())
      .andExpect(status().isBadRequest());
  }

  @Test
  void getUserById_正常系_指定したIDのユーザーが取得し200を返す() throws Exception {
    Long id = 1L;
    User user = new User();
    user.setId(id);
    user.setUsername("検索ユーザー");

    when(userService.getUserById(id)).thenReturn(user);

    mockMvc.perform(get("/api/users/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("検索ユーザー"));
  }

  @Test
  void getUserById_異常系_存在しないIDを指定した場合は404を返す() throws Exception {
    Long id = 999L;

    when(userService.getUserById(id)).thenThrow(new IllegalArgumentException("IDが見つかりません: " + id));

    mockMvc.perform(get("/api/users/{id}", id))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void deleteUser_正常系_ユーザーが削除し204を返す() throws Exception {
    mockMvc.perform(delete("/api/users/{id}", 1L))
        .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
  }
}
