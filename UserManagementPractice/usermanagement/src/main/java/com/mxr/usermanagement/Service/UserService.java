
/*
Statelesness in this service means that no data is stored or persisted during or after any of these operations. Right now all data required to perform any operation is passed as a parameter(request).  
For this to be scaled horizontally, the data should be stored in a database or a distributed cache.
If session data lived in a local field, it would become stateful i.e Each time the service a new instance of the class is called and the 
data that existed in the previous instance is lost. Not just that, other instances dont know what happens in other instances, leading to 
unpredictable behavior.
*/
package com.mxr.usermanagement.Service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import com.mxr.usermanagement.config.UserServiceProperties;
import com.mxr.usermanagement.data.dto.requests.CreateUserDTO;
import com.mxr.usermanagement.data.dto.requests.Response;
import com.mxr.usermanagement.model.User;
import com.mxr.usermanagement.data.repo.UserRepo;
import com.mxr.usermanagement.exceptions.UserNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




@Service
public class UserService {


    private final UserRepo userRepo;
    private final UserServiceProperties userServiceProperties;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepo userRepo, UserServiceProperties userServiceProperties) {
        this.userRepo = userRepo;
        this.userServiceProperties = userServiceProperties;
    }
    

    public Response createUser(CreateUserDTO validRequestDTO) {
        User user = new User();
        user.setName(validRequestDTO.getName());
        user.setEmail(validRequestDTO.getEmail());
        user.setUserName(validRequestDTO.getUsername());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepo.save(user);
        Response response = mapToResponseDTO(user);
        return response;
    }

    public Page<Response> getAllUsers(Pageable pageable){
        Pageable page = PageRequest.of(pageable.getPageNumber(), userServiceProperties.getMaxUsersPerPage(), pageable.getSort());
        Page<User> users = userRepo.findAll(page);
        logger.info("Fetching users. Max users per page is {}", userServiceProperties.getMaxUsersPerPage());
        return users.map(this::mapToResponseDTO);
    }

    private Response mapToResponseDTO(User user){
        Response response  = Response.builder()
        .name(user.getName())
        .email(user.getEmail())
        .username(user.getUserName())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
        return response;
    }

    public Response getUserById(long number) {
        User user = userRepo.getUserById(number);
        if(user == null){
            throw new UserNotFoundException(number);
        }   

        logger.info("User found with id: {}", number);
        return mapToResponseDTO(user);
    }

    public Response updateUserDetail(Long id, CreateUserDTO userRequestDTO) {
        User user = userRepo.getUserById(id);
        if(user == null){
            throw new UserNotFoundException(id);
        }
        user.setName(userRequestDTO.getName());
        user.setEmail(userRequestDTO.getEmail());
        user.setUserName(userRequestDTO.getUsername());
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepo.save(user);
        logger.info("User updated with id: {}", id);
        return mapToResponseDTO(user);
        
    }


    public void deleteUser(Long id) {
        User user = userRepo.getById(id);
        if(user == null){
            throw new UserNotFoundException(id);
        }
        userRepo.deleteUserById(id);
        logger.info("User deleted with id: {}", id);
    }
}



