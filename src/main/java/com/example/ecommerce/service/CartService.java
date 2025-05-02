package com.example.ecommerce.service;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ProductService productService;
    
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }
    
    public Cart addItemToCart(User user, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(user);
        
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        
        Product product = productOpt.get();
        
        // Check if product is already in cart
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getCartItems().add(newItem);
        }
        
        return cartRepository.save(cart);
    }
    
    public Cart updateCartItem(User user, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        if (quantity <= 0) {
            return removeItemFromCart(user, productId);
        }
        
        cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
        
        return cartRepository.save(cart);
    }
    
    public Cart removeItemFromCart(User user, Long productId) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(productId));
        
        return cartRepository.save(cart);
    }
    
    public void clearCart(User user) {
        Optional<Cart> cartOpt = cartRepository.findByUser(user);
        
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cart.getCartItems().clear();
            cartRepository.save(cart);
        }
    }
}