package com.example.ecommerce.controller;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String viewCart(Model model, Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartService.getOrCreateCart(user);
        model.addAttribute("cart", cart);
        
        return "cart";
    }
    
    @PostMapping("/add")
    public String addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            cartService.addItemToCart(user, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Product added to cart");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/products/" + productId;
    }
    
    @PostMapping("/update")
    public String updateCartItem(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        cartService.updateCartItem(user, productId, quantity);
        redirectAttributes.addFlashAttribute("success", "Cart updated");
        
        return "redirect:/cart";
    }
    
    @PostMapping("/remove")
    public String removeFromCart(
            @RequestParam Long productId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        cartService.removeItemFromCart(user, productId);
        redirectAttributes.addFlashAttribute("success", "Item removed from cart");
        
        return "redirect:/cart";
    }
    
    @PostMapping("/clear")
    public String clearCart(
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        cartService.clearCart(user);
        redirectAttributes.addFlashAttribute("success", "Cart cleared");
        
        return "redirect:/cart";
    }
}