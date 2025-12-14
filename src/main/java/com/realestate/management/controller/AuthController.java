package com.realestate.management.controller;
import com.realestate.management.model.User;
import com.realestate.management.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Show registration page
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", User.UserRole.values());
        return "auth/register";
    }

    // Handle registration
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        
        // Gmail-only validation
        if (!user.getEmail().toLowerCase().endsWith("@gmail.com")) {
            model.addAttribute("errorMessage", "Only Gmail addresses (@gmail.com) are allowed!");
            model.addAttribute("roles", User.UserRole.values());
            return "auth/register";
        }
        
        // Check for validation errors
        if (result.hasErrors()) {
            model.addAttribute("roles", User.UserRole.values());
            return "auth/register";
        }
        
        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", User.UserRole.values());
            return "auth/register";
        }
    }

    // Show login page
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "auth/login";
    }

    // Handle login
    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        
        // Gmail-only validation
        if (!email.toLowerCase().endsWith("@gmail.com")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only Gmail addresses (@gmail.com) are allowed!");
            return "redirect:/auth/login";
        }
        
        try {
            User user = userService.loginUser(email, password);
            
            // Store user in session
            session.setAttribute("loggedInUser", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole().toString());
            
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + user.getFirstName() + "!");
            
            // Redirect based on role
            if (user.getRole() == User.UserRole.ADMIN) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/dashboard";
            }
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    // Handle logout
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out successfully.");
        return "redirect:/auth/login";
    }
}