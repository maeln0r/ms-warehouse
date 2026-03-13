package backend.security.service;

import backend.model.Role;
import backend.model.User;
import backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceUserSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${service-auth.seed-enabled:true}")
    private boolean seedEnabled;

    @Value("${service-auth.username:}")
    private String serviceUsername;

    @Value("${service-auth.password:}")
    private String servicePassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!seedEnabled) {
            log.info("Service auth seeding is disabled (SERVICE_AUTH_SEED_ENABLED=false).");
            return;
        }

        if (!StringUtils.hasText(serviceUsername) || !StringUtils.hasText(servicePassword)) {
            log.warn("Service auth seeding skipped: WAREHOUSE_USERNAME/WAREHOUSE_PASSWORD are empty.");
            return;
        }

        var existing = userRepository.findByUsername(serviceUsername);

        if (existing.isPresent()) {
            User user = existing.get();
            if (passwordEncoder.matches(servicePassword, user.getPassword())) {
                log.info("Service user '{}' already exists with the expected password.", serviceUsername);
                return;
            }

            user.setPassword(passwordEncoder.encode(servicePassword));
            userRepository.save(user);
            log.info("Service user '{}' password has been updated from secret.", serviceUsername);
            return;
        }

        User serviceUser = User.builder()
                .username(serviceUsername)
                .password(passwordEncoder.encode(servicePassword))
                .role(Role.USER)
                .build();

        userRepository.save(serviceUser);
        log.info("Service user '{}' has been created.", serviceUsername);
    }
}
