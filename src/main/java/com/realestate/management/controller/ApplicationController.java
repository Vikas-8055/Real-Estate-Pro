package com.realestate.management.controller;

import com.realestate.management.model.Application;
import com.realestate.management.model.Property;
import com.realestate.management.model.User;
import com.realestate.management.service.ApplicationService;
import com.realestate.management.service.PropertyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final PropertyService propertyService;

    @Autowired
    public ApplicationController(ApplicationService applicationService, PropertyService propertyService) {
        this.applicationService = applicationService;
        this.propertyService = propertyService;
    }

    // Show application form
    @GetMapping("/submit/{propertyId}")
    public String showApplicationForm(@PathVariable Long propertyId,
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
        model.addAttribute("application", new Application());
        return "applications/submit";
    }

    // Submit application
    @PostMapping("/submit/{propertyId}")
    public String submitApplication(@PathVariable Long propertyId,
                                    @ModelAttribute Application application,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        try {
            applicationService.submitApplication(propertyId, loggedInUser.getId(), application);
            redirectAttributes.addFlashAttribute("successMessage",
                "Application submitted successfully! The property owner will review your application.");
            return "redirect:/applications/my-applications";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/applications/submit/" + propertyId;
        }
    }

    // View user's applications (buyer's view)
    @GetMapping("/my-applications")
    public String myApplications(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        List<Application> applications = applicationService.getUserApplications(loggedInUser.getId());
        Long applicationCount = applicationService.countUserApplications(loggedInUser.getId());

        model.addAttribute("applications", applications);
        model.addAttribute("applicationCount", applicationCount);
        return "applications/my-applications";
    }

    // View applications for owner's properties
    @GetMapping("/received")
    public String receivedApplications(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        List<Application> applications = applicationService.getOwnerApplications(loggedInUser.getId());
        List<Application> pendingApplications = applicationService.getPendingApplicationsForOwner(loggedInUser.getId());
        Long pendingCount = applicationService.countPendingApplications(loggedInUser.getId());

        model.addAttribute("applications", applications);
        model.addAttribute("pendingApplications", pendingApplications);
        model.addAttribute("pendingCount", pendingCount);
        return "applications/received";
    }

    // Approve application
    @PostMapping("/{id}/approve")
    public String approveApplication(@PathVariable Long id,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        // Check if user is the property owner
        if (!applicationService.isPropertyOwner(id, loggedInUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You don't have permission to approve this application.");
            return "redirect:/applications/received";
        }

        try {
            applicationService.approveApplication(id);
            redirectAttributes.addFlashAttribute("successMessage", "Application approved!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving application.");
        }

        return "redirect:/applications/received";
    }

    // Reject application
    @PostMapping("/{id}/reject")
    public String rejectApplication(@PathVariable Long id,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        // Check if user is the property owner
        if (!applicationService.isPropertyOwner(id, loggedInUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You don't have permission to reject this application.");
            return "redirect:/applications/received";
        }

        try {
            applicationService.rejectApplication(id);
            redirectAttributes.addFlashAttribute("successMessage", "Application rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting application.");
        }

        return "redirect:/applications/received";
    }

    // Withdraw application (by applicant)
    @PostMapping("/{id}/withdraw")
    public String withdrawApplication(@PathVariable Long id,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        // Check if user is the applicant
        if (!applicationService.isApplicant(id, loggedInUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You don't have permission to withdraw this application.");
            return "redirect:/applications/my-applications";
        }

        try {
            applicationService.withdrawApplication(id);
            redirectAttributes.addFlashAttribute("successMessage", "Application withdrawn.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error withdrawing application.");
        }

        return "redirect:/applications/my-applications";
    }

    // Mark as under review
    @PostMapping("/{id}/review")
    public String markUnderReview(@PathVariable Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        // Check if user is the property owner
        if (!applicationService.isPropertyOwner(id, loggedInUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You don't have permission.");
            return "redirect:/applications/received";
        }

        try {
            applicationService.markUnderReview(id);
            redirectAttributes.addFlashAttribute("successMessage", "Application marked as under review.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating application.");
        }

        return "redirect:/applications/received";
    }
}