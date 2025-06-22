package com.pulseiq.config;

import com.pulseiq.entity.Admin;
import com.pulseiq.entity.User;
import com.pulseiq.entity.UserRole;
import com.pulseiq.entity.UserStatus;
import com.pulseiq.repository.AdminRepository;
import com.pulseiq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public AdminSeeder(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public void run(String... args) {
        String userId = "A202506001";

        if (userRepository.findByUserId(userId).isEmpty()) {
            User user = new User();
            user.setUserId(userId);
            user.setUsername("super_admin");
            user.setPassword("$2a$12$E4.9tTLvehv4NOghm5mqROfyvfCOdY962drt8sfZeNC47k0ztCNFS"); // bcrypt of "admin123"
            user.setEmail("admin@pulseiq.com");
            user.setPhone("01800000000");
            user.setRole(UserRole.ADMIN);
            user.setStatus(UserStatus.ACTIVE);

            userRepository.save(user);

            Admin admin = new Admin();
            admin.setAdminId(userId);
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            // admin.setUser(user);

            adminRepository.save(admin);

            log.info("Default admin user seeded.");
        } else {
            log.info("Default admin user already exists.");
        }
    }
}
