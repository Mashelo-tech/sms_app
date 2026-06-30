package com.schoolsystem.sms.service;

import com.schoolsystem.sms.model.User;
import com.schoolsystem.sms.model.Role;
import java.util.Optional;
import java.util.List;

public interface UserService {
    User registerUser(String username, String password, String fullName, String email, Role role);
    Optional<User> findByUsername(String username);
    List<User> findAllUsers();
}
