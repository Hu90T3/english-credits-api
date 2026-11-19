package credits_api.service;

import credits_api.entity.User;
import credits_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(rollbackFor = Exception.class)
    public User registerUser(String fullName, String email, String rawPassword, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("El correo electrónico ya está registrado.");
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        // Encriptamos la contraseña antes de guardarla en Oracle
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setCreditsBalance(0);

        return userRepository.save(user);
    }
}
