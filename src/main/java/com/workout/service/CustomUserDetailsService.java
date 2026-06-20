package com.workout.service;

import com.workout.config.CustomUserDetails;
import com.workout.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // DBからユーザー名をキーに検索
        com.workout.model.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザー名が見つかりません: " + username));

        return new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword());
    }
}