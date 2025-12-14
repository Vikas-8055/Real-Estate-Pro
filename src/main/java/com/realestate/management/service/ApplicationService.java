package com.realestate.management.service;

import com.realestate.management.dao.ApplicationDao;
import com.realestate.management.dao.PropertyDao;
import com.realestate.management.dao.UserDao;
import com.realestate.management.model.Application;
import com.realestate.management.model.Property;
import com.realestate.management.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationDao applicationDao;
    private final PropertyDao propertyDao;
    private final UserDao userDao;

    @Autowired
    public ApplicationService(ApplicationDao applicationDao, PropertyDao propertyDao, UserDao userDao) {
        this.applicationDao = applicationDao;
        this.propertyDao = propertyDao;
        this.userDao = userDao;
    }

    // Submit an application
    @Transactional
    public Application submitApplication(Long propertyId, Long userId, Application application) {
        // Check if user already has an application for this property
        if (applicationDao.hasApplication(userId, propertyId)) {
            throw new RuntimeException("You already have an application for this property");
        }

        Property property = propertyDao.findById(propertyId);
        User user = userDao.findById(userId);

        if (property == null || user == null) {
            throw new RuntimeException("Property or User not found");
        }

        // Set the relationships
        application.setProperty(property);
        application.setUser(user);
        
        // Set application type based on property listing type
        if (property.getListingType() == Property.ListingType.SALE) {
            application.setApplicationType(Application.ApplicationType.PURCHASE);
        } else {
            application.setApplicationType(Application.ApplicationType.RENTAL);
        }

        applicationDao.save(application);
        return application;
    }

    // Get application by ID
    public Application getApplicationById(Long id) {
        return applicationDao.findById(id);
    }

    // Get all applications for a user
    public List<Application> getUserApplications(Long userId) {
        return applicationDao.findByUser(userId);
    }

    // Get all applications for a property
    public List<Application> getPropertyApplications(Long propertyId) {
        return applicationDao.findByProperty(propertyId);
    }

    // Get all applications for property owner
    public List<Application> getOwnerApplications(Long ownerId) {
        return applicationDao.findByPropertyOwner(ownerId);
    }

    // Get pending applications for owner
    public List<Application> getPendingApplicationsForOwner(Long ownerId) {
        return applicationDao.findPendingByOwner(ownerId);
    }

    // Approve application
    @Transactional
    public void approveApplication(Long applicationId) {
        Application application = applicationDao.findById(applicationId);
        if (application != null) {
            application.setStatus(Application.ApplicationStatus.APPROVED);
            applicationDao.update(application);
        }
    }

    // Reject application
    @Transactional
    public void rejectApplication(Long applicationId) {
        Application application = applicationDao.findById(applicationId);
        if (application != null) {
            application.setStatus(Application.ApplicationStatus.REJECTED);
            applicationDao.update(application);
        }
    }

    // Mark as under review
    @Transactional
    public void markUnderReview(Long applicationId) {
        Application application = applicationDao.findById(applicationId);
        if (application != null) {
            application.setStatus(Application.ApplicationStatus.UNDER_REVIEW);
            applicationDao.update(application);
        }
    }

    // Withdraw application
    @Transactional
    public void withdrawApplication(Long applicationId) {
        Application application = applicationDao.findById(applicationId);
        if (application != null) {
            application.setStatus(Application.ApplicationStatus.WITHDRAWN);
            applicationDao.update(application);
        }
    }

    // Count pending applications for owner
    public Long countPendingApplications(Long ownerId) {
        return applicationDao.countPendingByOwner(ownerId);
    }

    // Check if user owns the property
    public boolean isPropertyOwner(Long applicationId, Long userId) {
        Application application = applicationDao.findById(applicationId);
        return application != null && application.getProperty().getOwner().getId().equals(userId);
    }

    // Check if user is the applicant
    public boolean isApplicant(Long applicationId, Long userId) {
        Application application = applicationDao.findById(applicationId);
        return application != null && application.getUser().getId().equals(userId);
    }

    // Count applications by user
    public Long countUserApplications(Long userId) {
        return applicationDao.countByUser(userId);
    }
}