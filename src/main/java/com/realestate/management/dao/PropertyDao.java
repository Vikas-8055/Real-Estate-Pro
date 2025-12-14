package com.realestate.management.dao;

import com.realestate.management.model.Property;
import com.realestate.management.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
@Transactional
public class PropertyDao {

    @PersistenceContext
    private EntityManager entityManager;

    // Save a new property
    public void save(Property property) {
        entityManager.persist(property);
    }

    // Update an existing property
    public void update(Property property) {
        entityManager.merge(property);
    }

    // Delete a property
    public void delete(Property property) {
        entityManager.remove(entityManager.contains(property) ? property : entityManager.merge(property));
    }

    // Find property by ID
    public Property findById(Long id) {
        return entityManager.find(Property.class, id);
    }

    // Get all properties
    public List<Property> findAll() {
        TypedQuery<Property> query = entityManager.createQuery(
            "SELECT p FROM Property p ORDER BY p.createdAt DESC", Property.class);
        return query.getResultList();
    }

    // Get all approved properties
    public List<Property> findAllApproved() {
        TypedQuery<Property> query = entityManager.createQuery(
            "SELECT p FROM Property p WHERE p.status = :status ORDER BY p.createdAt DESC", Property.class);
        query.setParameter("status", Property.PropertyStatus.APPROVED);
        return query.getResultList();
    }

    // Get properties by owner
    public List<Property> findByOwner(User owner) {
        TypedQuery<Property> query = entityManager.createQuery(
            "SELECT p FROM Property p WHERE p.owner = :owner ORDER BY p.createdAt DESC", Property.class);
        query.setParameter("owner", owner);
        return query.getResultList();
    }

    // Get properties by status
    public List<Property> findByStatus(Property.PropertyStatus status) {
        TypedQuery<Property> query = entityManager.createQuery(
            "SELECT p FROM Property p WHERE p.status = :status ORDER BY p.createdAt DESC", Property.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    // Get properties by listing type (SALE or RENT)
    public List<Property> findByListingType(Property.ListingType listingType) {
        TypedQuery<Property> query = entityManager.createQuery(
            "SELECT p FROM Property p WHERE p.listingType = :listingType AND p.status = :status ORDER BY p.createdAt DESC", Property.class);
        query.setParameter("listingType", listingType);
        query.setParameter("status", Property.PropertyStatus.APPROVED);
        return query.getResultList();
    }

    // Get properties by property type
    public List<Property> findByPropertyType(Property.PropertyType propertyType) {
        TypedQuery<Property> query = entityManager.createQuery(
            "SELECT p FROM Property p WHERE p.propertyType = :propertyType AND p.status = :status ORDER BY p.createdAt DESC", Property.class);
        query.setParameter("propertyType", propertyType);
        query.setParameter("status", Property.PropertyStatus.APPROVED);
        return query.getResultList();
    }

    // Get properties by city
    public List<Property> findByCity(String city) {
        TypedQuery<Property> query = entityManager.createQuery(
            "SELECT p FROM Property p WHERE LOWER(p.city) LIKE LOWER(:city) AND p.status = :status ORDER BY p.createdAt DESC", Property.class);
        query.setParameter("city", "%" + city + "%");
        query.setParameter("status", Property.PropertyStatus.APPROVED);
        return query.getResultList();
    }

    // Get properties by price range
    public List<Property> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        TypedQuery<Property> query = entityManager.createQuery(
            "SELECT p FROM Property p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.status = :status ORDER BY p.price ASC", Property.class);
        query.setParameter("minPrice", minPrice);
        query.setParameter("maxPrice", maxPrice);
        query.setParameter("status", Property.PropertyStatus.APPROVED);
        return query.getResultList();
    }

    // Get properties by bedrooms
    public List<Property> findByBedrooms(Integer bedrooms) {
        TypedQuery<Property> query = entityManager.createQuery(
            "SELECT p FROM Property p WHERE p.bedrooms >= :bedrooms AND p.status = :status ORDER BY p.createdAt DESC", Property.class);
        query.setParameter("bedrooms", bedrooms);
        query.setParameter("status", Property.PropertyStatus.APPROVED);
        return query.getResultList();
    }

    // Search properties with filters
    public List<Property> searchProperties(String city, Property.PropertyType propertyType, 
                                           Property.ListingType listingType, BigDecimal minPrice, 
                                           BigDecimal maxPrice, Integer bedrooms) {
        StringBuilder queryStr = new StringBuilder("SELECT p FROM Property p WHERE p.status = :status");
        
        if (city != null && !city.isEmpty()) {
            queryStr.append(" AND LOWER(p.city) LIKE LOWER(:city)");
        }
        if (propertyType != null) {
            queryStr.append(" AND p.propertyType = :propertyType");
        }
        if (listingType != null) {
            queryStr.append(" AND p.listingType = :listingType");
        }
        if (minPrice != null) {
            queryStr.append(" AND p.price >= :minPrice");
        }
        if (maxPrice != null) {
            queryStr.append(" AND p.price <= :maxPrice");
        }
        if (bedrooms != null) {
            queryStr.append(" AND p.bedrooms >= :bedrooms");
        }
        
        queryStr.append(" ORDER BY p.createdAt DESC");
        
        TypedQuery<Property> query = entityManager.createQuery(queryStr.toString(), Property.class);
        query.setParameter("status", Property.PropertyStatus.APPROVED);
        
        if (city != null && !city.isEmpty()) {
            query.setParameter("city", "%" + city + "%");
        }
        if (propertyType != null) {
            query.setParameter("propertyType", propertyType);
        }
        if (listingType != null) {
            query.setParameter("listingType", listingType);
        }
        if (minPrice != null) {
            query.setParameter("minPrice", minPrice);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        if (bedrooms != null) {
            query.setParameter("bedrooms", bedrooms);
        }
        
        return query.getResultList();
    }

    // Count properties by owner
    public Long countByOwner(User owner) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM Property p WHERE p.owner = :owner", Long.class);
        query.setParameter("owner", owner);
        return query.getSingleResult();
    }

    // Count pending properties
    public Long countPending() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM Property p WHERE p.status = :status", Long.class);
        query.setParameter("status", Property.PropertyStatus.PENDING);
        return query.getSingleResult();
    }
}