package com.example.ecommerce.controller;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private CartService cartService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String checkout(Model model, Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartService.getOrCreateCart(user);
        
        if (cart.getCartItems().isEmpty()) {
            return "redirect:/cart";
        }
        
        model.addAttribute("cart", cart);
        model.addAttribute("user", user);
        
        return "checkout";
    }
    
    @PostMapping("/place-order")
    public String placeOrder(
            @RequestParam String shippingAddress,
            @RequestParam String paymentMethod,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            Order order = orderService.createOrder(user, shippingAddress, paymentMethod);
            redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout";
        }
    }
}