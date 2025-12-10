package com.workout.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.workout.model.User;
import com.workout.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", 25);
        testUser.setId(1L);
    }

    @Test
    void registerUser_正常系_ユーザーが登録される() {
        // Arrange
        String username = "newuser";
        Integer age = 30;
        User savedUser = new User(username, age);
        savedUser.setId(1L);
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.registerUser(username, age);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(age, result.getAge());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getAllUsers_正常系_全ユーザーが取得される() {
        // Arrange
        User user2 = new User("user2", 28);
        user2.setId(2L);
        List<User> users = Arrays.asList(testUser, user2);
        
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_正常系_指定したIDのユーザーが取得される() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals(25, result.get().getAge());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_異常系_存在しないIDの場合空が返される() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(userId);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateUser_正常系_ユーザー情報が更新される() {
        // Arrange
        Long userId = 1L;
        String newUsername = "updateduser";
        Integer newAge = 35;
        User updatedUser = new User(newUsername, newAge);
        updatedUser.setId(userId);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userService.updateUser(userId, newUsername, newAge);

        // Assert
        assertNotNull(result);
        assertEquals(newUsername, result.getUsername());
        assertEquals(newAge, result.getAge());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_異常系_存在しないIDの場合nullが返される() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        User result = userService.updateUser(userId, "newname", 30);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_正常系_ユーザーが削除される() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }
}