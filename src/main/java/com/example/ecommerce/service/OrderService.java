package com.example.ecommerce.service;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private ProductService productService;
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }
    
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    public List<Order> getOrdersBetweenDates(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByOrderDateBetween(start, end);
    }
    
    @Transactional
    public Order createOrder(User user, String shippingAddress, String paymentMethod) {
        Cart cart = cartService.getOrCreateCart(user);
        
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cannot create order with empty cart");
        }
        
        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        
        // Create payment
        Payment payment = new Payment();
        payment.setAmount(cart.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        order.setPayment(payment);
        payment.setOrder(order);
        
        // Add items to order
        BigDecimal total = BigDecimal.ZERO;
        
        for (CartItem cartItem : cart.getCartItems()) {
            // Check stock
            if (cartItem.getProduct().getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + cartItem.getProduct().getName());
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            order.getOrderItems().add(orderItem);
            
            total = total.add(orderItem.getSubtotal());
            
            // Update product stock
            productService.updateStock(cartItem.getProduct().getId(), cartItem.getQuantity());
        }
        
        order.setTotalAmount(total);
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        // Clear cart
        cartService.clearCart(user);
        
        return savedOrder;
    }
    
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        
        // If order is cancelled, restore stock
        if (status == Order.OrderStatus.CANCELLED) {
            order.getOrderItems().forEach(item -> 
                productService.restoreStock(item.getProduct().getId(), item.getQuantity())
            );
        }
        
        return orderRepository.save(order);
    }
}