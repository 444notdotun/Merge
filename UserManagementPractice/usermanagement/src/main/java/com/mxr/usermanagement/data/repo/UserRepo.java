package com.mxr.usermanagement.data.repo;

import com.mxr.usermanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;


public interface UserRepo extends JpaRepository<User, Long> {
    boolean existsByuserName(String username);

    User save(User user);

    User getUserById(Long id);

    User getUserByuserName(String username);

    List<User> getUsersByName(String name);

    void deleteUserById(Long id);
}
