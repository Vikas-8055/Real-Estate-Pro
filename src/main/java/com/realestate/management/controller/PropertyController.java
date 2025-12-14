package com.realestate.management.controller;

import com.realestate.management.model.Property;
import com.realestate.management.model.User;
import com.realestate.management.service.PropertyService;
import com.realestate.management.service.UserService;
import com.realestate.management.service.FavoriteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/properties")
public class PropertyController {

    private final PropertyService propertyService;
    private final UserService userService;
    private final FavoriteService favoriteService;

    @Autowired
    public PropertyController(PropertyService propertyService, UserService userService, FavoriteService favoriteService) {
        this.propertyService = propertyService;
        this.userService = userService;
        this.favoriteService = favoriteService;
    }

    // List all approved properties (public)
    @GetMapping
    public String listProperties(Model model) {
        List<Property> properties = propertyService.getAllApprovedProperties();
        model.addAttribute("properties", properties);
        model.addAttribute("propertyTypes", Property.PropertyType.values());
        model.addAttribute("listingTypes", Property.ListingType.values());
        return "property/list";
    }

    // Search properties with filters
    @GetMapping("/search")
    public String searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) String listingType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer bedrooms,
            Model model) {

        Property.PropertyType propType = null;
        Property.ListingType listType = null;

        if (propertyType != null && !propertyType.isEmpty()) {
            propType = Property.PropertyType.valueOf(propertyType);
        }
        if (listingType != null && !listingType.isEmpty()) {
            listType = Property.ListingType.valueOf(listingType);
        }

        List<Property> properties = propertyService.searchProperties(
                city, propType, listType, minPrice, maxPrice, bedrooms);

        model.addAttribute("properties", properties);
        model.addAttribute("propertyTypes", Property.PropertyType.values());
        model.addAttribute("listingTypes", Property.ListingType.values());
        model.addAttribute("searchCity", city);
        model.addAttribute("searchPropertyType", propertyType);
        model.addAttribute("searchListingType", listingType);
        model.addAttribute("searchMinPrice", minPrice);
        model.addAttribute("searchMaxPrice", maxPrice);
        model.addAttribute("searchBedrooms", bedrooms);

        return "property/list";
    }

    // View single property details
    @GetMapping("/{id}")
    public String viewProperty(@PathVariable Long id, Model model, HttpSession session) {
        Property property = propertyService.getPropertyById(id);
        if (property == null) {
            return "redirect:/properties";
        }

        model.addAttribute("property", property);

        // Check if current user is the owner
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("isOwner", property.getOwner().getId().equals(loggedInUser.getId()));
            
            // Check if property is favorited by this user
            boolean isFavorited = favoriteService.isFavorited(loggedInUser.getId(), id);
            model.addAttribute("isFavorited", isFavorited);
        } else {
            model.addAttribute("isFavorited", false);
        }

        return "property/details";
    }

    // Show create property form
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session) {
        // Check if user is logged in
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("property", new Property());
        model.addAttribute("propertyTypes", Property.PropertyType.values());
        model.addAttribute("listingTypes", Property.ListingType.values());
        return "property/create";
    }

    // Handle create property
    @PostMapping("/new")
    public String createProperty(@Valid @ModelAttribute("property") Property property,
                                 BindingResult result,
                                 Model model,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        // Check if user is logged in
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("propertyTypes", Property.PropertyType.values());
            model.addAttribute("listingTypes", Property.ListingType.values());
            return "property/create";
        }

        try {
            // Get fresh user from database
            User owner = userService.getUserById(loggedInUser.getId());
            propertyService.createProperty(property, owner);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Property listed successfully! It will be visible after admin approval.");
            return "redirect:/properties/my-properties";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating property: " + e.getMessage());
            model.addAttribute("propertyTypes", Property.PropertyType.values());
            model.addAttribute("listingTypes", Property.ListingType.values());
            return "property/create";
        }
    }

    // Show user's properties
    @GetMapping("/my-properties")
    public String myProperties(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        User owner = userService.getUserById(loggedInUser.getId());
        List<Property> properties = propertyService.getPropertiesByOwner(owner);
        model.addAttribute("properties", properties);
        return "property/my-properties";
    }

    // Show edit property form
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        Property property = propertyService.getPropertyById(id);
        if (property == null) {
            return "redirect:/properties/my-properties";
        }

        // Check if user is the owner
        if (!property.getOwner().getId().equals(loggedInUser.getId())) {
            return "redirect:/properties/my-properties";
        }

        model.addAttribute("property", property);
        model.addAttribute("propertyTypes", Property.PropertyType.values());
        model.addAttribute("listingTypes", Property.ListingType.values());
        return "property/edit";
    }

    // Handle edit property
    @PostMapping("/{id}/edit")
    public String updateProperty(@PathVariable Long id,
                                 @Valid @ModelAttribute("property") Property property,
                                 BindingResult result,
                                 Model model,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        Property existingProperty = propertyService.getPropertyById(id);
        if (existingProperty == null || !existingProperty.getOwner().getId().equals(loggedInUser.getId())) {
            return "redirect:/properties/my-properties";
        }

        if (result.hasErrors()) {
            model.addAttribute("propertyTypes", Property.PropertyType.values());
            model.addAttribute("listingTypes", Property.ListingType.values());
            return "property/edit";
        }

        try {
            // Preserve original data
            property.setId(id);
            property.setOwner(existingProperty.getOwner());
            property.setCreatedAt(existingProperty.getCreatedAt());
            property.setStatus(Property.PropertyStatus.PENDING); // Reset to pending for re-approval

            propertyService.updateProperty(property);
            redirectAttributes.addFlashAttribute("successMessage", "Property updated successfully!");
            return "redirect:/properties/my-properties";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating property: " + e.getMessage());
            model.addAttribute("propertyTypes", Property.PropertyType.values());
            model.addAttribute("listingTypes", Property.ListingType.values());
            return "property/edit";
        }
    }

    // Delete property
    @PostMapping("/{id}/delete")
    public String deleteProperty(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        Property property = propertyService.getPropertyById(id);
        if (property == null || !property.getOwner().getId().equals(loggedInUser.getId())) {
            return "redirect:/properties/my-properties";
        }

        try {
            propertyService.deleteProperty(id);
            redirectAttributes.addFlashAttribute("successMessage", "Property deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting property.");
        }

        return "redirect:/properties/my-properties";
    }

    // Mark property as sold
    @PostMapping("/{id}/mark-sold")
    public String markAsSold(@PathVariable Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        Property property = propertyService.getPropertyById(id);
        if (property == null || !property.getOwner().getId().equals(loggedInUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You don't have permission to update this property.");
            return "redirect:/properties/my-properties";
        }

        try {
            propertyService.markAsSold(id);
            redirectAttributes.addFlashAttribute("successMessage", "Property marked as SOLD!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating property status.");
        }

        return "redirect:/properties/my-properties";
    }

    // Mark property as rented
    @PostMapping("/{id}/mark-rented")
    public String markAsRented(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        Property property = propertyService.getPropertyById(id);
        if (property == null || !property.getOwner().getId().equals(loggedInUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You don't have permission to update this property.");
            return "redirect:/properties/my-properties";
        }

        try {
            propertyService.markAsRented(id);
            redirectAttributes.addFlashAttribute("successMessage", "Property marked as RENTED!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating property status.");
        }

        return "redirect:/properties/my-properties";
    }
}