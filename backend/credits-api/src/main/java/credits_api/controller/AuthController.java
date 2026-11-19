package credits_api.controller;

import credits_api.dto.RegisterRequest;
import credits_api.dto.UserBalanceResponse;
import credits_api.entity.User;
import credits_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserBalanceResponse> register(@Valid @RequestBody RegisterRequest request) {
        User created = authService.registerUser(
                request.fullName(),
                request.email(),
                request.password(),
                request.role()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new UserBalanceResponse(created.getId(), created.getFullName(), created.getEmail(), created.getRole(), created.getCreditsBalance())
        );
    }
}
