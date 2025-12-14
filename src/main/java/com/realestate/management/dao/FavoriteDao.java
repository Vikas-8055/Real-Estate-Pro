package com.realestate.management.dao;

import com.realestate.management.model.Favorite;
import com.realestate.management.model.Property;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class FavoriteDao {

    @PersistenceContext
    private EntityManager entityManager;

    // Save favorite
    public void save(Favorite favorite) {
        entityManager.persist(favorite);
    }

    // Delete favorite
    public void delete(Favorite favorite) {
        entityManager.remove(entityManager.contains(favorite) ? favorite : entityManager.merge(favorite));
    }

    // Find favorite by user and property
    public Favorite findByUserAndProperty(Long userId, Long propertyId) {
        TypedQuery<Favorite> query = entityManager.createQuery(
            "SELECT f FROM Favorite f WHERE f.user.id = :userId AND f.property.id = :propertyId",
            Favorite.class);
        query.setParameter("userId", userId);
        query.setParameter("propertyId", propertyId);
        List<Favorite> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    // Get all favorites for a user
    public List<Property> findFavoritePropertiesByUser(Long userId) {
        TypedQuery<Property> query = entityManager.createQuery(
            "SELECT f.property FROM Favorite f WHERE f.user.id = :userId ORDER BY f.createdAt DESC",
            Property.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    // Check if property is favorited by user
    public boolean isFavorited(Long userId, Long propertyId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(f) FROM Favorite f WHERE f.user.id = :userId AND f.property.id = :propertyId",
            Long.class);
        query.setParameter("userId", userId);
        query.setParameter("propertyId", propertyId);
        return query.getSingleResult() > 0;
    }

    // Count favorites for a user
    public Long countByUser(Long userId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(f) FROM Favorite f WHERE f.user.id = :userId",
            Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }
}