package com.example.ecommerce.service;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Page<Product> getProductsPaginated(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
    
    public Page<Product> getProductsByCategory(Category category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }
    
    public List<Product> getFeaturedProducts() {
        return productRepository.findByFeaturedTrue();
    }
    
    public List<Product> getNewArrivals() {
        return productRepository.findTop8ByOrderByCreatedAtDesc();
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    public boolean updateStock(Long productId, int quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            
            if (product.getStockQuantity() >= quantity) {
                product.setStockQuantity(product.getStockQuantity() - quantity);
                productRepository.save(product);
                return true;
            }
        }
        
        return false;
    }
    
    public void restoreStock(Long productId, int quantity) {
        productRepository.findById(productId).ifPresent(product -> {
            product.setStockQuantity(product.getStockQuantity() + quantity);
            productRepository.save(product);
        });
    }
}