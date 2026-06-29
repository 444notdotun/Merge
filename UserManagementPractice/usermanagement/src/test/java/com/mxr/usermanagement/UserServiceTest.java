package com.mxr.usermanagement;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.mxr.usermanagement.Service.UserService;
import com.mxr.usermanagement.data.dto.requests.CreateUserDTO;
import com.mxr.usermanagement.data.repo.UserRepo;
import com.mxr.usermanagement.exceptions.UserNotFoundException;
import com.mxr.usermanagement.model.User;
import com.mxr.usermanagement.data.dto.requests.Response;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    private CreateUserDTO validRequestDTO;
    
    @BeforeEach
    void setUp() {
        validRequestDTO = new CreateUserDTO();
        validRequestDTO.setName("Martins");
        validRequestDTO.setEmail("mxr@gmail.com");
        validRequestDTO.setUsername("mxr");

        User validUser = new User();
        validUser.setId(1L);
        validUser.setName("Martins");
        validUser.setEmail("mxr@gmail.com");
        validUser.setUserName("mxr");
        validUser.setCreatedAt(LocalDateTime.now());
        validUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldReturnResponseDTO_whenValidRequestisProvided() {
        Response user = userService.createUser(validRequestDTO);

        assertTrue(user.getName() == validRequestDTO.getName());
        assertTrue(user != null);
    }

    @Test
    void shouldReturnUserNotFoundException_whenUserDoesNotExist() {

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

}

