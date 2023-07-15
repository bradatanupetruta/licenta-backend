package com.licenta.backend.service;

import com.licenta.backend.model.User;
import com.licenta.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public ResponseEntity getUserByPin(String pin) {
        List<User> userList = userRepository.findByPin(pin);
        if (userList.size() == 1) {
            return ResponseEntity.ok(userList.get(0));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).get();
    }

    public ResponseEntity createUser(User user) {
        List<User> verificationList = userRepository.findByPin(user.getPin());
        if (verificationList.isEmpty() || user.getId() != null) {
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.IM_USED).build();
    }

    public ResponseEntity deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }
}
