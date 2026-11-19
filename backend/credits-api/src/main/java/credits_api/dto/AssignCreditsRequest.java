package credits_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AssignCreditsRequest(
        @NotNull Long studentId,
        @NotNull @Min(1) Integer amount,
        String description
) {}