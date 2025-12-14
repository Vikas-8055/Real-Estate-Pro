package com.realestate.management.service;

import com.realestate.management.dao.PropertyDao;
import com.realestate.management.model.Property;
import com.realestate.management.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PropertyService {

    private final PropertyDao propertyDao;

    @Autowired
    public PropertyService(PropertyDao propertyDao) {
        this.propertyDao = propertyDao;
    }

    // Create a new property
    public Property createProperty(Property property, User owner) {
        property.setOwner(owner);
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());
        property.setStatus(Property.PropertyStatus.PENDING);
        propertyDao.save(property);
        return property;
    }

    // Update a property
    public Property updateProperty(Property property) {
        property.setUpdatedAt(LocalDateTime.now());
        propertyDao.update(property);
        return property;
    }

    // Delete a property
    public void deleteProperty(Long id) {
        Property property = propertyDao.findById(id);
        if (property != null) {
            propertyDao.delete(property);
        }
    }

    // Get property by ID
    public Property getPropertyById(Long id) {
        return propertyDao.findById(id);
    }

    // Get all properties
    public List<Property> getAllProperties() {
        return propertyDao.findAll();
    }

    // Get all approved properties (for public listing)
    public List<Property> getAllApprovedProperties() {
        return propertyDao.findAllApproved();
    }

    // Get properties by owner
    public List<Property> getPropertiesByOwner(User owner) {
        return propertyDao.findByOwner(owner);
    }

    // Get properties by status
    public List<Property> getPropertiesByStatus(Property.PropertyStatus status) {
        return propertyDao.findByStatus(status);
    }

    // Get pending properties (for admin)
    public List<Property> getPendingProperties() {
        return propertyDao.findByStatus(Property.PropertyStatus.PENDING);
    }

    // Approve a property
    public Property approveProperty(Long id) {
        Property property = propertyDao.findById(id);
        if (property != null) {
            property.setStatus(Property.PropertyStatus.APPROVED);
            property.setUpdatedAt(LocalDateTime.now());
            propertyDao.update(property);
        }
        return property;
    }

    // Reject a property
    public Property rejectProperty(Long id) {
        Property property = propertyDao.findById(id);
        if (property != null) {
            property.setStatus(Property.PropertyStatus.REJECTED);
            property.setUpdatedAt(LocalDateTime.now());
            propertyDao.update(property);
        }
        return property;
    }

    // Mark property as sold
    public Property markAsSold(Long id) {
        Property property = propertyDao.findById(id);
        if (property != null) {
            property.setStatus(Property.PropertyStatus.SOLD);
            property.setUpdatedAt(LocalDateTime.now());
            propertyDao.update(property);
        }
        return property;
    }

    // Mark property as rented
    public Property markAsRented(Long id) {
        Property property = propertyDao.findById(id);
        if (property != null) {
            property.setStatus(Property.PropertyStatus.RENTED);
            property.setUpdatedAt(LocalDateTime.now());
            propertyDao.update(property);
        }
        return property;
    }

    // Get properties for sale
    public List<Property> getPropertiesForSale() {
        return propertyDao.findByListingType(Property.ListingType.SALE);
    }

    // Get properties for rent
    public List<Property> getPropertiesForRent() {
        return propertyDao.findByListingType(Property.ListingType.RENT);
    }

    // Get properties by type
    public List<Property> getPropertiesByType(Property.PropertyType type) {
        return propertyDao.findByPropertyType(type);
    }

    // Get properties by city
    public List<Property> getPropertiesByCity(String city) {
        return propertyDao.findByCity(city);
    }

    // Get properties by price range
    public List<Property> getPropertiesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return propertyDao.findByPriceRange(minPrice, maxPrice);
    }

    // Search properties with filters
    public List<Property> searchProperties(String city, Property.PropertyType propertyType,
                                           Property.ListingType listingType, BigDecimal minPrice,
                                           BigDecimal maxPrice, Integer bedrooms) {
        return propertyDao.searchProperties(city, propertyType, listingType, minPrice, maxPrice, bedrooms);
    }

    // Count properties by owner
    public Long countPropertiesByOwner(User owner) {
        return propertyDao.countByOwner(owner);
    }

    // Count pending properties
    public Long countPendingProperties() {
        return propertyDao.countPending();
    }

    // Check if user is the owner of the property
    public boolean isOwner(Long propertyId, Long userId) {
        Property property = propertyDao.findById(propertyId);
        return property != null && property.getOwner().getId().equals(userId);
    }
    
}
