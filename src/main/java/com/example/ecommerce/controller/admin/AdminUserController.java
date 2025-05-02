package com.example.ecommerce.controller.admin;

import com.example.ecommerce.model.User;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;
    
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        
        return "admin/users/list";
    }
    
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", user);
        
        return "admin/users/details";
    }
    
    @PostMapping("/{id}/make-admin")
    public String makeAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.makeAdmin(id);
        redirectAttributes.addFlashAttribute("success", "User promoted to admin successfully");
        
        return "redirect:/admin/users/" + id;
    }
    
    @PostMapping("/{id}/remove-admin")
    public String removeAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.removeAdmin(id);
        redirectAttributes.addFlashAttribute("success", "Admin privileges removed successfully");
        
        return "redirect:/admin/users/" + id;
    }
    
    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        
        return "redirect:/admin/users";
    }
}