package credits_api.dto;

public record UserBalanceResponse(
        Long id,
        String fullName,
        String email,
        String role,
        Integer creditsBalance
) {}
