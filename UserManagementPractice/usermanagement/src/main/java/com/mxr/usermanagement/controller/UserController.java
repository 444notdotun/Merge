package com.mxr.usermanagement.controller;

import com.mxr.usermanagement.data.dto.requests.CreateUserDTO;
import com.mxr.usermanagement.data.dto.requests.Response;
import com.mxr.usermanagement.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.aspectj.internal.lang.annotation.ajcDeclareSoft;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Response> createUser(
            @Valid @RequestBody CreateUserDTO requestDTO) {
        Response response = userService.createUser(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getUserById(@PathVariable Long id) {
        Response response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<Response>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Response> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);

    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody CreateUserDTO requestDTO) {
        Response response = userService.updateUserDetail(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}