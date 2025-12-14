package com.realestate.management.controller;

import com.realestate.management.model.User;
import com.realestate.management.service.PropertyService;
import com.realestate.management.service.FavoriteService;
import com.realestate.management.service.ViewingService;
import com.realestate.management.service.ApplicationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final PropertyService propertyService;
    private final FavoriteService favoriteService;
    private final ViewingService viewingService;
    private final ApplicationService applicationService;

    @Autowired
    public DashboardController(PropertyService propertyService, 
                               FavoriteService favoriteService,
                               ViewingService viewingService,
                               ApplicationService applicationService) {
        this.propertyService = propertyService;
        this.favoriteService = favoriteService;
        this.viewingService = viewingService;
        this.applicationService = applicationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String userRole = (String) session.getAttribute("userRole");

        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        // Get statistics based on user role
        if ("OWNER".equals(userRole) || "AGENT".equals(userRole)) {
            // Owner/Agent statistics
            Long propertyCount = propertyService.countPropertiesByOwner(loggedInUser);
            Long viewingRequestsCount = viewingService.countPendingViewings(loggedInUser.getId());
            Long applicationsCount = applicationService.countPendingApplications(loggedInUser.getId());

            model.addAttribute("propertyCount", propertyCount);
            model.addAttribute("viewingRequestsCount", viewingRequestsCount);
            model.addAttribute("applicationsCount", applicationsCount);
            model.addAttribute("favoritesCount", 0L); // Owners don't typically track favorites

        } else if ("BUYER".equals(userRole) || "RENTER".equals(userRole)) {
            // Buyer/Renter statistics
            Long favoritesCount = favoriteService.countUserFavorites(loggedInUser.getId());
            Long viewingRequestsCount = (long) viewingService.getUserViewings(loggedInUser.getId()).size();
            Long applicationsCount = applicationService.countUserApplications(loggedInUser.getId());

            model.addAttribute("propertyCount", 0L); // Buyers don't list properties
            model.addAttribute("favoritesCount", favoritesCount);
            model.addAttribute("viewingRequestsCount", viewingRequestsCount);
            model.addAttribute("applicationsCount", applicationsCount);

        } else {
            // Default for other roles
            model.addAttribute("propertyCount", 0L);
            model.addAttribute("favoritesCount", 0L);
            model.addAttribute("viewingRequestsCount", 0L);
            model.addAttribute("applicationsCount", 0L);
        }

        return "dashboard";
    }
}