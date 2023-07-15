package com.licenta.backend.controller;

import com.licenta.backend.model.User;
import com.licenta.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/login/{pin}")
    public ResponseEntity getUserByPin(@PathVariable("pin") String pin) {
        return userService.getUserByPin(pin);
    }

    @PostMapping("/save")
    public ResponseEntity createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteProduct(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
