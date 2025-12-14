package com.realestate.management.service;

import com.realestate.management.dao.FavoriteDao;
import com.realestate.management.dao.PropertyDao;
import com.realestate.management.dao.UserDao;
import com.realestate.management.model.Favorite;
import com.realestate.management.model.Property;
import com.realestate.management.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteDao favoriteDao;
    private final UserDao userDao;
    private final PropertyDao propertyDao;

    @Autowired
    public FavoriteService(FavoriteDao favoriteDao, UserDao userDao, PropertyDao propertyDao) {
        this.favoriteDao = favoriteDao;
        this.userDao = userDao;
        this.propertyDao = propertyDao;
    }

    // Add to favorites
    @Transactional
    public void addFavorite(Long userId, Long propertyId) {
        // Check if already favorited
        if (favoriteDao.isFavorited(userId, propertyId)) {
            throw new RuntimeException("Property is already in favorites");
        }

        User user = userDao.findById(userId);
        Property property = propertyDao.findById(propertyId);

        if (user == null || property == null) {
            throw new RuntimeException("User or Property not found");
        }

        Favorite favorite = new Favorite(user, property);
        favoriteDao.save(favorite);
    }

    // Remove from favorites
    @Transactional
    public void removeFavorite(Long userId, Long propertyId) {
        Favorite favorite = favoriteDao.findByUserAndProperty(userId, propertyId);
        if (favorite != null) {
            favoriteDao.delete(favorite);
        }
    }

    // Get all favorite properties for a user
    public List<Property> getUserFavorites(Long userId) {
        return favoriteDao.findFavoritePropertiesByUser(userId);
    }

    // Check if property is favorited
    public boolean isFavorited(Long userId, Long propertyId) {
        return favoriteDao.isFavorited(userId, propertyId);
    }

    // Count favorites for a user
    public Long countUserFavorites(Long userId) {
        return favoriteDao.countByUser(userId);
    }
}