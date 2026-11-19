package credits_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCourseRequest(
        @NotBlank String title,
        @NotBlank String levelCode,
        @NotNull @Min(1) Integer creditCost,
        @NotNull @Min(1) Integer maxCapacity
) {}