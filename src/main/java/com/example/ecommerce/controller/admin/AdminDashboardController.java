package com.example.ecommerce.controller.admin;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.ProductService;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String dashboard(Model model) {
        // Recent orders
        List<Order> recentOrders = orderService.getAllOrders().stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .limit(5)
                .toList();
        
        // Orders by status
        List<Order> pendingOrders = orderService.getOrdersByStatus(Order.OrderStatus.PENDING);
        List<Order> processingOrders = orderService.getOrdersByStatus(Order.OrderStatus.PROCESSING);
        List<Order> shippedOrders = orderService.getOrdersByStatus(Order.OrderStatus.SHIPPED);
        
        // Orders in the last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Order> recentOrdersStats = orderService.getOrdersBetweenDates(thirtyDaysAgo, LocalDateTime.now());
        
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        model.addAttribute("totalOrders", orderService.getAllOrders().size());
        model.addAttribute("pendingOrders", pendingOrders.size());
        model.addAttribute("processingOrders", processingOrders.size());
        model.addAttribute("shippedOrders", shippedOrders.size());
        model.addAttribute("recentOrdersCount", recentOrdersStats.size());
        
        return "admin/dashboard";
    }
}