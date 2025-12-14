package com.realestate.management.controller;

import com.realestate.management.model.Property;
import com.realestate.management.model.PropertyViewing;
import com.realestate.management.model.User;
import com.realestate.management.service.PropertyService;
import com.realestate.management.service.ViewingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/viewings")
public class ViewingController {

    private final ViewingService viewingService;
    private final PropertyService propertyService;

    @Autowired
    public ViewingController(ViewingService viewingService, PropertyService propertyService) {
        this.viewingService = viewingService;
        this.propertyService = propertyService;
    }

    // Show request viewing form
    @GetMapping("/request/{propertyId}")
    public String showRequestForm(@PathVariable Long propertyId, 
                                   Model model, 
                                   HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        Property property = propertyService.getPropertyById(propertyId);
        if (property == null) {
            return "redirect:/properties";
        }

        // Check if user is the owner
        if (property.getOwner().getId().equals(loggedInUser.getId())) {
            return "redirect:/properties/" + propertyId;
        }

        model.addAttribute("property", property);
        return "viewings/request";
    }

    // Submit viewing request
    @PostMapping("/request/{propertyId}")
    public String submitViewingRequest(@PathVariable Long propertyId,
                                       @RequestParam("viewingDate") String viewingDateStr,
                                       @RequestParam("message") String message,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        try {
            // Parse the datetime string
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime viewingDate = LocalDateTime.parse(viewingDateStr, formatter);

            viewingService.requestViewing(propertyId, loggedInUser.getId(), viewingDate, message);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Viewing request submitted successfully! The owner will review your request.");
            return "redirect:/viewings/my-viewings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/viewings/request/" + propertyId;
        }
    }

    // View user's viewing requests (buyer's view)
    @GetMapping("/my-viewings")
    public String myViewings(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        List<PropertyViewing> viewings = viewingService.getUserViewings(loggedInUser.getId());
        List<PropertyViewing> upcomingViewings = viewingService.getUpcomingViewings(loggedInUser.getId());

        model.addAttribute("viewings", viewings);
        model.addAttribute("upcomingViewings", upcomingViewings);
        return "viewings/my-viewings";
    }

    // View viewing requests for owner's properties
    @GetMapping("/requests")
    public String viewingRequests(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        List<PropertyViewing> viewings = viewingService.getOwnerViewingRequests(loggedInUser.getId());
        List<PropertyViewing> pendingViewings = viewingService.getPendingViewingsForOwner(loggedInUser.getId());
        Long pendingCount = viewingService.countPendingViewings(loggedInUser.getId());

        model.addAttribute("viewings", viewings);
        model.addAttribute("pendingViewings", pendingViewings);
        model.addAttribute("pendingCount", pendingCount);
        return "viewings/requests";
    }

    // Approve viewing request
    @PostMapping("/{id}/approve")
    public String approveViewing(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        // Check if user is the property owner
        if (!viewingService.isPropertyOwner(id, loggedInUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You don't have permission to approve this request.");
            return "redirect:/viewings/requests";
        }

        try {
            viewingService.approveViewing(id);
            redirectAttributes.addFlashAttribute("successMessage", "Viewing request approved!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving viewing request.");
        }

        return "redirect:/viewings/requests";
    }

    // Reject viewing request
    @PostMapping("/{id}/reject")
    public String rejectViewing(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        // Check if user is the property owner
        if (!viewingService.isPropertyOwner(id, loggedInUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You don't have permission to reject this request.");
            return "redirect:/viewings/requests";
        }

        try {
            viewingService.rejectViewing(id);
            redirectAttributes.addFlashAttribute("successMessage", "Viewing request rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting viewing request.");
        }

        return "redirect:/viewings/requests";
    }

    // Cancel viewing request (by requester)
    @PostMapping("/{id}/cancel")
    public String cancelViewing(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        // Check if user is the requester
        if (!viewingService.isRequester(id, loggedInUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You don't have permission to cancel this request.");
            return "redirect:/viewings/my-viewings";
        }

        try {
            viewingService.cancelViewing(id);
            redirectAttributes.addFlashAttribute("successMessage", "Viewing request cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error cancelling viewing request.");
        }

        return "redirect:/viewings/my-viewings";
    }

    // Mark viewing as completed
    @PostMapping("/{id}/complete")
    public String completeViewing(@PathVariable Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        // Check if user is the property owner
        if (!viewingService.isPropertyOwner(id, loggedInUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You don't have permission to complete this viewing.");
            return "redirect:/viewings/requests";
        }

        try {
            viewingService.completeViewing(id);
            redirectAttributes.addFlashAttribute("successMessage", "Viewing marked as completed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error completing viewing.");
        }

        return "redirect:/viewings/requests";
    }
}