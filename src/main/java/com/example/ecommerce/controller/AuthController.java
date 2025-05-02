package com.example.ecommerce.controller;

import com.example.ecommerce.model.User;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid User user, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }
        
        if (userService.isEmailTaken(user.getEmail())) {
            result.rejectValue("email", "error.user", "An account already exists with this email");
            return "register";
        }
        
        userService.registerUser(user, false);
        redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
        
        return "redirect:/login";
    }
    
    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", user);
        return "profile";
    }
    
    @GetMapping("/profile/edit")
    public String editProfile(Model model, Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", user);
        return "edit-profile";
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") User userRequest, 
                                Authentication authentication, 
                                RedirectAttributes redirectAttributes) {
        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update only allowed fields
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setAddress(userRequest.getAddress());
        user.setCity(userRequest.getCity());
        user.setState(userRequest.getState());
        user.setZipCode(userRequest.getZipCode());
        user.setCountry(userRequest.getCountry());
        
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        
        return "redirect:/profile";
    }
}