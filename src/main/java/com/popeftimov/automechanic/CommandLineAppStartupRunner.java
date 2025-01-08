package com.popeftimov.automechanic;

import com.popeftimov.automechanic.model.UserRole;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${ADMIN_FIRSTNAME}")
    private String firstName;

    @Value("${ADMIN_LASTNAME}")
    private String lastName;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Override
    public void run(String...args) throws Exception{
        User admin = new User(
                firstName,
                lastName,
                adminEmail,
                passwordEncoder.encode(adminPassword),
                UserRole.ADMIN
        );
        admin.setEnabled(true);
        userRepository.save(admin);
    }
}
