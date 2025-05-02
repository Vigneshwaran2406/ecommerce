package com.example.ecommerce.config;

import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) {
        // Create roles
        createRoleIfNotFound("USER");
        createRoleIfNotFound("ADMIN");
        
        // Create admin user if no admin exists
        if (userService.getAllUsers().stream().noneMatch(user -> user.hasRole("ADMIN"))) {
            User adminUser = new User();
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword("admin123"); // Will be encoded by the service
            
            userService.registerUser(adminUser, true);
            
            System.out.println("Admin user created: admin@example.com / admin123");
        }
    }
    
    private void createRoleIfNotFound(String name) {
        Optional<Role> roleOpt = roleRepository.findByName(name);
        if (roleOpt.isEmpty()) {
            Role role = new Role(name);
            roleRepository.save(role);
        }
    }
}