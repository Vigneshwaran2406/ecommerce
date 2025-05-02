package com.example.ecommerce.controller;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.CategoryService;
import com.example.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("/")
    public String home(Model model) {
        List<Product> featuredProducts = productService.getFeaturedProducts();
        List<Product> newArrivals = productService.getNewArrivals();
        List<Category> categories = categoryService.getAllCategories();
        
        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("newArrivals", newArrivals);
        model.addAttribute("categories", categories);
        
        return "home";
    }
    
    @GetMapping("/products")
    public String getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<Product> productPage;
        
        if (keyword != null && !keyword.isEmpty()) {
            productPage = productService.searchProducts(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else if (categoryId != null) {
            categoryService.getCategoryById(categoryId).ifPresent(category -> {
                model.addAttribute("selectedCategory", category);
            });
            productPage = productService.getProductsByCategory(categoryService.getCategoryById(categoryId).orElse(null), pageable);
        } else {
            productPage = productService.getProductsPaginated(pageable);
        }
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        
        return "products";
    }
}