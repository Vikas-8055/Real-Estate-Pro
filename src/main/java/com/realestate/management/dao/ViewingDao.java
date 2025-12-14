package com.realestate.management.dao;

import com.realestate.management.model.PropertyViewing;
import com.realestate.management.model.User;
import com.realestate.management.model.Property;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class ViewingDao {

    @PersistenceContext
    private EntityManager entityManager;

    // Save viewing request
    public void save(PropertyViewing viewing) {
        entityManager.persist(viewing);
    }

    // Update viewing
    public void update(PropertyViewing viewing) {
        entityManager.merge(viewing);
    }

    // Delete viewing
    public void delete(PropertyViewing viewing) {
        entityManager.remove(entityManager.contains(viewing) ? viewing : entityManager.merge(viewing));
    }

    // Find viewing by ID
    public PropertyViewing findById(Long id) {
        return entityManager.find(PropertyViewing.class, id);
    }

    // Get all viewings
    public List<PropertyViewing> findAll() {
        TypedQuery<PropertyViewing> query = entityManager.createQuery(
            "SELECT v FROM PropertyViewing v ORDER BY v.viewingDate DESC", 
            PropertyViewing.class);
        return query.getResultList();
    }

    // Get viewings by user (buyer's viewings)
    public List<PropertyViewing> findByUser(Long userId) {
        TypedQuery<PropertyViewing> query = entityManager.createQuery(
            "SELECT v FROM PropertyViewing v WHERE v.user.id = :userId ORDER BY v.viewingDate DESC", 
            PropertyViewing.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    // Get viewings by property
    public List<PropertyViewing> findByProperty(Long propertyId) {
        TypedQuery<PropertyViewing> query = entityManager.createQuery(
            "SELECT v FROM PropertyViewing v WHERE v.property.id = :propertyId ORDER BY v.viewingDate DESC", 
            PropertyViewing.class);
        query.setParameter("propertyId", propertyId);
        return query.getResultList();
    }

    // Get viewings by property owner (for owner to see requests)
    public List<PropertyViewing> findByPropertyOwner(Long ownerId) {
        TypedQuery<PropertyViewing> query = entityManager.createQuery(
            "SELECT v FROM PropertyViewing v WHERE v.property.owner.id = :ownerId ORDER BY v.viewingDate DESC", 
            PropertyViewing.class);
        query.setParameter("ownerId", ownerId);
        return query.getResultList();
    }

    // Get viewings by status
    public List<PropertyViewing> findByStatus(PropertyViewing.ViewingStatus status) {
        TypedQuery<PropertyViewing> query = entityManager.createQuery(
            "SELECT v FROM PropertyViewing v WHERE v.status = :status ORDER BY v.viewingDate DESC", 
            PropertyViewing.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    // Get pending viewings for owner
    public List<PropertyViewing> findPendingByOwner(Long ownerId) {
        TypedQuery<PropertyViewing> query = entityManager.createQuery(
            "SELECT v FROM PropertyViewing v WHERE v.property.owner.id = :ownerId AND v.status = :status ORDER BY v.viewingDate ASC", 
            PropertyViewing.class);
        query.setParameter("ownerId", ownerId);
        query.setParameter("status", PropertyViewing.ViewingStatus.PENDING);
        return query.getResultList();
    }

    // Get upcoming approved viewings for user
    public List<PropertyViewing> findUpcomingByUser(Long userId) {
        TypedQuery<PropertyViewing> query = entityManager.createQuery(
            "SELECT v FROM PropertyViewing v WHERE v.user.id = :userId AND v.status = :status AND v.viewingDate > :now ORDER BY v.viewingDate ASC", 
            PropertyViewing.class);
        query.setParameter("userId", userId);
        query.setParameter("status", PropertyViewing.ViewingStatus.APPROVED);
        query.setParameter("now", LocalDateTime.now());
        return query.getResultList();
    }

    // Count pending viewings for owner
    public Long countPendingByOwner(Long ownerId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(v) FROM PropertyViewing v WHERE v.property.owner.id = :ownerId AND v.status = :status", 
            Long.class);
        query.setParameter("ownerId", ownerId);
        query.setParameter("status", PropertyViewing.ViewingStatus.PENDING);
        return query.getSingleResult();
    }

    // Check if user already has a viewing request for property
    public boolean hasViewingRequest(Long userId, Long propertyId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(v) FROM PropertyViewing v WHERE v.user.id = :userId AND v.property.id = :propertyId AND v.status IN (:statuses)", 
            Long.class);
        query.setParameter("userId", userId);
        query.setParameter("propertyId", propertyId);
        query.setParameter("statuses", List.of(
            PropertyViewing.ViewingStatus.PENDING, 
            PropertyViewing.ViewingStatus.APPROVED
        ));
        return query.getSingleResult() > 0;
    }
}