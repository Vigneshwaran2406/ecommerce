package com.example.ecommerce.controller;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String viewOrders(Model model, Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders);
        
        return "orders";
    }
    
    @GetMapping("/{id}")
    public String viewOrderDetails(@PathVariable Long id, Model model, Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Security check - ensure the order belongs to the current user or user is admin
        if (!order.getUser().getId().equals(user.getId()) && !user.hasRole("ADMIN")) {
            return "redirect:/orders";
        }
        
        model.addAttribute("order", order);
        
        return "order-details";
    }
}