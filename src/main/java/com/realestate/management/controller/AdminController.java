package com.realestate.management.controller;

import com.realestate.management.model.Property;
import com.realestate.management.model.User;
import com.realestate.management.service.PropertyService;
import com.realestate.management.service.UserService;
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
@RequestMapping("/admin")
public class AdminController {

    private final PropertyService propertyService;
    private final UserService userService;

    @Autowired
    public AdminController(PropertyService propertyService, UserService userService) {
        this.propertyService = propertyService;
        this.userService = userService;
    }

    // Check if user is admin
    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        return role != null && role.equals("ADMIN");
    }

    // Admin Dashboard
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        List<Property> pendingProperties = propertyService.getPendingProperties();
        List<User> allUsers = userService.getAllUsers();
        Long pendingCount = propertyService.countPendingProperties();

        model.addAttribute("pendingProperties", pendingProperties);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("totalUsers", allUsers.size());

        return "admin/dashboard";
    }

    // View pending properties
    @GetMapping("/properties/pending")
    public String pendingProperties(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        List<Property> pendingProperties = propertyService.getPendingProperties();
        model.addAttribute("properties", pendingProperties);
        return "admin/pending-properties";
    }

    // Approve property
    @PostMapping("/properties/{id}/approve")
    public String approveProperty(@PathVariable Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        try {
            propertyService.approveProperty(id);
            redirectAttributes.addFlashAttribute("successMessage", "Property approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving property.");
        }

        return "redirect:/admin/dashboard";
    }

    // Reject property
    @PostMapping("/properties/{id}/reject")
    public String rejectProperty(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        try {
            propertyService.rejectProperty(id);
            redirectAttributes.addFlashAttribute("successMessage", "Property rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting property.");
        }

        return "redirect:/admin/dashboard";
    }

    // View all users
    @GetMapping("/users")
    public String allUsers(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    // Deactivate user
    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        try {
            userService.deactivateUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deactivated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deactivating user.");
        }

        return "redirect:/admin/users";
    }

    // Activate user
    @PostMapping("/users/{id}/activate")
    public String activateUser(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        try {
            userService.activateUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User activated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error activating user.");
        }

        return "redirect:/admin/users";
    }
}