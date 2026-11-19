package credits_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank @Pattern(regexp = "ROLE_ADMIN|ROLE_STUDENT", message = "El rol debe ser ROLE_ADMIN o ROLE_STUDENT") String role
) {}