package com.licenta.backend.repository;

import com.licenta.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByPin(String pin);
}
