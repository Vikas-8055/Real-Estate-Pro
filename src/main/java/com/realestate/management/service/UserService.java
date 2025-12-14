package com.realestate.management.service;

import com.realestate.management.dao.UserDao;
import com.realestate.management.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    // Register a new user
    public User registerUser(User user) {
        // Check if email already exists
        if (userDao.emailExists(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Set timestamps
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setActive(true);
        
        // Save user
        userDao.save(user);
        return user;
    }

    // Login user
    public User loginUser(String email, String password) {
        User user = userDao.findByEmail(email);
        
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }
        
        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }
        
        return user;
    }

    // Get user by ID
    public User getUserById(Long id) {
        return userDao.findById(id);
    }

    // Get user by email
    public User getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    // Get users by role
    public List<User> getUsersByRole(User.UserRole role) {
        return userDao.findByRole(role);
    }

    // Update user
    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        userDao.update(user);
        return user;
    }

    // Deactivate user
    public void deactivateUser(Long id) {
        User user = userDao.findById(id);
        if (user != null) {
            user.setActive(false);
            user.setUpdatedAt(LocalDateTime.now());
            userDao.update(user);
        }
    }

    // Activate user
    public void activateUser(Long id) {
        User user = userDao.findById(id);
        if (user != null) {
            user.setActive(true);
            user.setUpdatedAt(LocalDateTime.now());
            userDao.update(user);
        }
    }

    // Check if email exists
    public boolean emailExists(String email) {
        return userDao.emailExists(email);
    }
}