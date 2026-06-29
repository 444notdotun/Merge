package com.devarena.user.service.impl;

import com.devarena.exception.DuplicateResourceException;
import com.devarena.exception.ResourceNotFoundException;
import com.devarena.user.dto.request.CreateUserRequest;
import com.devarena.user.dto.request.UpdateUserRequest;
import com.devarena.user.dto.response.UserResponse;
import com.devarena.user.entity.User;
import com.devarena.user.repository.UserRepository;
import com.devarena.user.service.UserService;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = toEntity(request, encodedPassword);
        User savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getUserByUsername(String username){
        User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User with username" + username + " not found!"));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(this::toResponse);
    }

    @Transactional
    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        return toResponse(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .bio(user.getBio())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private User toEntity(CreateUserRequest request, String encodedPassword) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .role(request.getRole())
                .bio(request.getBio())
                .build();
    }
}
