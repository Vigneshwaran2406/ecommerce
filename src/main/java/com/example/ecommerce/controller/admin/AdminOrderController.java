package com.example.ecommerce.controller.admin;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public String listOrders(
            @RequestParam(required = false) Order.OrderStatus status,
            Model model) {
        
        List<Order> orders;
        
        if (status != null) {
            orders = orderService.getOrdersByStatus(status);
        } else {
            orders = orderService.getAllOrders();
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("statuses", Order.OrderStatus.values());
        model.addAttribute("selectedStatus", status);
        
        return "admin/orders/list";
    }
    
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        model.addAttribute("order", order);
        model.addAttribute("statuses", Order.OrderStatus.values());
        
        return "admin/orders/details";
    }
    
    @PostMapping("/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status,
            RedirectAttributes redirectAttributes) {
        
        orderService.updateOrderStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Order status updated successfully");
        
        return "redirect:/admin/orders/" + id;
    }
}