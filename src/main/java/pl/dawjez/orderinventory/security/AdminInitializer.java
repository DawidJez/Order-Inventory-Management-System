package pl.dawjez.orderinventory.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.dawjez.orderinventory.user.Role;
import pl.dawjez.orderinventory.user.User;
import pl.dawjez.orderinventory.user.UserRepository;

import java.util.Locale;

// Creates the first ADMIN account at startup, so the system has someone who can manage products.
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    @Override
    public void run(ApplicationArguments args) {
        String email = adminProperties.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            log.info("Admin account already exists, skipping creation");
            return;
        }
        userRepository.save(new User(
                email,
                passwordEncoder.encode(adminProperties.password()),
                "Admin",
                "Admin",
                Role.ADMIN));

        log.info("Admin account created");
    }
}
