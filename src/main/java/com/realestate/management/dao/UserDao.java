package com.realestate.management.dao;

import com.realestate.management.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    // Save a new user
    public void save(User user) {
        entityManager.persist(user);
    }

    // Update an existing user
    public void update(User user) {
        entityManager.merge(user);
    }

    // Delete a user
    public void delete(User user) {
        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
    }

    // Find user by ID
    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }

    // Find user by email
    public User findByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        List<User> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    // Get all users
    public List<User> findAll() {
        TypedQuery<User> query = entityManager.createQuery(
            "SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    // Find users by role
    public List<User> findByRole(User.UserRole role) {
        TypedQuery<User> query = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.role = :role", User.class);
        query.setParameter("role", role);
        return query.getResultList();
    }

    // Check if email exists
    public boolean emailExists(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }

    // Find active users
    public List<User> findActiveUsers() {
        TypedQuery<User> query = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.isActive = true", User.class);
        return query.getResultList();
    }
}