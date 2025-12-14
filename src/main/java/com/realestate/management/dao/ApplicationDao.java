package com.realestate.management.dao;

import com.realestate.management.model.Application;
import com.realestate.management.model.Property;
import com.realestate.management.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class ApplicationDao {

    @PersistenceContext
    private EntityManager entityManager;

    // Save application
    public void save(Application application) {
        entityManager.persist(application);
    }

    // Update application
    public void update(Application application) {
        entityManager.merge(application);
    }

    // Delete application
    public void delete(Application application) {
        entityManager.remove(entityManager.contains(application) ? application : entityManager.merge(application));
    }

    // Find application by ID
    public Application findById(Long id) {
        return entityManager.find(Application.class, id);
    }

    // Get all applications
    public List<Application> findAll() {
        TypedQuery<Application> query = entityManager.createQuery(
            "SELECT a FROM Application a ORDER BY a.createdAt DESC", 
            Application.class);
        return query.getResultList();
    }

    // Get applications by user (buyer's applications)
    public List<Application> findByUser(Long userId) {
        TypedQuery<Application> query = entityManager.createQuery(
            "SELECT a FROM Application a WHERE a.user.id = :userId ORDER BY a.createdAt DESC", 
            Application.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    // Get applications by property
    public List<Application> findByProperty(Long propertyId) {
        TypedQuery<Application> query = entityManager.createQuery(
            "SELECT a FROM Application a WHERE a.property.id = :propertyId ORDER BY a.createdAt DESC", 
            Application.class);
        query.setParameter("propertyId", propertyId);
        return query.getResultList();
    }

    // Get applications by property owner (for owner to review)
    public List<Application> findByPropertyOwner(Long ownerId) {
        TypedQuery<Application> query = entityManager.createQuery(
            "SELECT a FROM Application a WHERE a.property.owner.id = :ownerId ORDER BY a.createdAt DESC", 
            Application.class);
        query.setParameter("ownerId", ownerId);
        return query.getResultList();
    }

    // Get applications by status
    public List<Application> findByStatus(Application.ApplicationStatus status) {
        TypedQuery<Application> query = entityManager.createQuery(
            "SELECT a FROM Application a WHERE a.status = :status ORDER BY a.createdAt DESC", 
            Application.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    // Get pending applications for owner
    public List<Application> findPendingByOwner(Long ownerId) {
        TypedQuery<Application> query = entityManager.createQuery(
            "SELECT a FROM Application a WHERE a.property.owner.id = :ownerId AND a.status = :status ORDER BY a.createdAt DESC", 
            Application.class);
        query.setParameter("ownerId", ownerId);
        query.setParameter("status", Application.ApplicationStatus.PENDING);
        return query.getResultList();
    }

    // Count pending applications for owner
    public Long countPendingByOwner(Long ownerId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(a) FROM Application a WHERE a.property.owner.id = :ownerId AND a.status = :status", 
            Long.class);
        query.setParameter("ownerId", ownerId);
        query.setParameter("status", Application.ApplicationStatus.PENDING);
        return query.getSingleResult();
    }

    // Check if user already has an application for property
    public boolean hasApplication(Long userId, Long propertyId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(a) FROM Application a WHERE a.user.id = :userId AND a.property.id = :propertyId AND a.status IN (:statuses)", 
            Long.class);
        query.setParameter("userId", userId);
        query.setParameter("propertyId", propertyId);
        query.setParameter("statuses", List.of(
            Application.ApplicationStatus.PENDING, 
            Application.ApplicationStatus.UNDER_REVIEW,
            Application.ApplicationStatus.APPROVED
        ));
        return query.getSingleResult() > 0;
    }

    // Count applications by user
    public Long countByUser(Long userId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(a) FROM Application a WHERE a.user.id = :userId", 
            Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }
}