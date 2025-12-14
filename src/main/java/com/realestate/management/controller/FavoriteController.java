package com.realestate.management.controller;

import com.realestate.management.model.Property;
import com.realestate.management.service.FavoriteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // View all favorites
    @GetMapping
    public String viewFavorites(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        List<Property> favorites = favoriteService.getUserFavorites(userId);
        Long favoriteCount = favoriteService.countUserFavorites(userId);

        model.addAttribute("favorites", favorites);
        model.addAttribute("favoriteCount", favoriteCount);
        return "favorites";
    }

    // Add to favorites
    @PostMapping("/add/{propertyId}")
    public String addFavorite(@PathVariable Long propertyId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        try {
            favoriteService.addFavorite(userId, propertyId);
            redirectAttributes.addFlashAttribute("successMessage", "Property added to favorites!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/properties/" + propertyId;
    }

    // Remove from favorites
    @PostMapping("/remove/{propertyId}")
    public String removeFavorite(@PathVariable Long propertyId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        try {
            favoriteService.removeFavorite(userId, propertyId);
            redirectAttributes.addFlashAttribute("successMessage", "Property removed from favorites!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/favorites";
    }
}