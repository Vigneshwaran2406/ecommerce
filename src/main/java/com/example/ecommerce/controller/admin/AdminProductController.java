package com.example.ecommerce.controller.admin;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.CategoryService;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    private final Path rootLocation = Paths.get("src/main/resources/static/uploads");
    
    @GetMapping
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<Product> productPage = productService.getProductsPaginated(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        
        return "admin/products/list";
    }
    
    @GetMapping("/new")
    public String newProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        
        return "admin/products/form";
    }
    
    @GetMapping("/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        
        return "admin/products/form";
    }
    
    @PostMapping("/save")
    public String saveProduct(
            @Valid Product product,
            BindingResult result,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(required = false) MultipartFile image,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/products/form";
        }
        
        // Set category
        Category category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);
        
        // Handle image upload
        if (image != null && !image.isEmpty()) {
            try {
                // Create upload directory if it doesn't exist
                if (!Files.exists(rootLocation)) {
                    Files.createDirectories(rootLocation);
                }
                
                // Generate unique filename
                String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Files.copy(image.getInputStream(), rootLocation.resolve(filename));
                
                product.setImageUrl("/uploads/" + filename);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
            }
        }
        
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("success", "Product saved successfully");
        
        return "redirect:/admin/products";
    }
    
    @GetMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("success", "Product deleted successfully");
        
        return "redirect:/admin/products";
    }
}