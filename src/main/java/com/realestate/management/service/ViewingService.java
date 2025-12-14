package com.realestate.management.service;

import com.realestate.management.dao.ViewingDao;
import com.realestate.management.dao.PropertyDao;
import com.realestate.management.dao.UserDao;
import com.realestate.management.model.PropertyViewing;
import com.realestate.management.model.Property;
import com.realestate.management.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ViewingService {

    private final ViewingDao viewingDao;
    private final PropertyDao propertyDao;
    private final UserDao userDao;

    @Autowired
    public ViewingService(ViewingDao viewingDao, PropertyDao propertyDao, UserDao userDao) {
        this.viewingDao = viewingDao;
        this.propertyDao = propertyDao;
        this.userDao = userDao;
    }

    // Request a viewing
    @Transactional
    public PropertyViewing requestViewing(Long propertyId, Long userId, LocalDateTime viewingDate, String message) {
        // Check if already has pending/approved request
        if (viewingDao.hasViewingRequest(userId, propertyId)) {
            throw new RuntimeException("You already have a viewing request for this property");
        }

        Property property = propertyDao.findById(propertyId);
        User user = userDao.findById(userId);

        if (property == null || user == null) {
            throw new RuntimeException("Property or User not found");
        }

        // Check if viewing date is in the future
        if (viewingDate.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Viewing date must be in the future");
        }

        PropertyViewing viewing = new PropertyViewing(property, user, viewingDate, message);
        viewingDao.save(viewing);
        return viewing;
    }

    // Get viewing by ID
    public PropertyViewing getViewingById(Long id) {
        return viewingDao.findById(id);
    }

    // Get all viewings for a user
    public List<PropertyViewing> getUserViewings(Long userId) {
        return viewingDao.findByUser(userId);
    }

    // Get all viewings for a property
    public List<PropertyViewing> getPropertyViewings(Long propertyId) {
        return viewingDao.findByProperty(propertyId);
    }

    // Get all viewing requests for property owner
    public List<PropertyViewing> getOwnerViewingRequests(Long ownerId) {
        return viewingDao.findByPropertyOwner(ownerId);
    }

    // Get pending viewing requests for owner
    public List<PropertyViewing> getPendingViewingsForOwner(Long ownerId) {
        return viewingDao.findPendingByOwner(ownerId);
    }

    // Get upcoming approved viewings for user
    public List<PropertyViewing> getUpcomingViewings(Long userId) {
        return viewingDao.findUpcomingByUser(userId);
    }

    // Approve viewing request
    @Transactional
    public void approveViewing(Long viewingId) {
        PropertyViewing viewing = viewingDao.findById(viewingId);
        if (viewing != null) {
            viewing.setStatus(PropertyViewing.ViewingStatus.APPROVED);
            viewingDao.update(viewing);
        }
    }

    // Reject viewing request
    @Transactional
    public void rejectViewing(Long viewingId) {
        PropertyViewing viewing = viewingDao.findById(viewingId);
        if (viewing != null) {
            viewing.setStatus(PropertyViewing.ViewingStatus.REJECTED);
            viewingDao.update(viewing);
        }
    }

    // Cancel viewing
    @Transactional
    public void cancelViewing(Long viewingId) {
        PropertyViewing viewing = viewingDao.findById(viewingId);
        if (viewing != null) {
            viewing.setStatus(PropertyViewing.ViewingStatus.CANCELLED);
            viewingDao.update(viewing);
        }
    }

    // Mark viewing as completed
    @Transactional
    public void completeViewing(Long viewingId) {
        PropertyViewing viewing = viewingDao.findById(viewingId);
        if (viewing != null) {
            viewing.setStatus(PropertyViewing.ViewingStatus.COMPLETED);
            viewingDao.update(viewing);
        }
    }

    // Count pending viewings for owner
    public Long countPendingViewings(Long ownerId) {
        return viewingDao.countPendingByOwner(ownerId);
    }

    // Check if user owns the property
    public boolean isPropertyOwner(Long viewingId, Long userId) {
        PropertyViewing viewing = viewingDao.findById(viewingId);
        return viewing != null && viewing.getProperty().getOwner().getId().equals(userId);
    }

    // Check if user is the requester
    public boolean isRequester(Long viewingId, Long userId) {
        PropertyViewing viewing = viewingDao.findById(viewingId);
        return viewing != null && viewing.getUser().getId().equals(userId);
    }
}