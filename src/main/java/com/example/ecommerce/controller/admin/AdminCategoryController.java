package com.example.ecommerce.controller.admin;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;
    
    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        
        return "admin/categories/list";
    }
    
    @GetMapping("/new")
    public String newCategory(Model model) {
        model.addAttribute("category", new Category());
        
        return "admin/categories/form";
    }
    
    @GetMapping("/{id}/edit")
    public String editCategory(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        model.addAttribute("category", category);
        
        return "admin/categories/form";
    }
    
    @PostMapping("/save")
    public String saveCategory(
            @Valid Category category,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "admin/categories/form";
        }
        
        // Check if name is already taken (for new categories)
        if (category.getId() == null && categoryService.isCategoryNameTaken(category.getName())) {
            result.rejectValue("name", "error.category", "Category name already exists");
            return "admin/categories/form";
        }
        
        categoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("success", "Category saved successfully");
        
        return "redirect:/admin/categories";
    }
    
    @GetMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteCategory(id);
        redirectAttributes.addFlashAttribute("success", "Category deleted successfully");
        
        return "redirect:/admin/categories";
    }
}