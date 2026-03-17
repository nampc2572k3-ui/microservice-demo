package com.example.demo.domain.model.dto.request.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@Getter
public class PageRequest {

    @Min(value = 1, message = "Page number must be greater than or equal to 0")
    private int page;

    @Min(value = 1, message = "Size must be greater than or equal to 1")
    @Max(value = 100, message = "Size must be less than or equal to 100")
    private int size;

    @NotBlank
    private String sortBy;

    @Pattern(
            regexp = "ASC|DESC",
            message = "Sort direction must be either 'ASC' or 'DESC'"
    )
    private String direction;



    public static Pageable toPageable(int index, int size, String sortBy, String sortDirection) {

        int page = Math.max(index - 1, 0);

        Sort.Direction direction = Optional.ofNullable(sortDirection)
                .map(d -> {
                    try {
                        return Sort.Direction.fromString(d);
                    } catch (Exception e) {
                        return Sort.Direction.ASC;
                    }
                })
                .orElse(Sort.Direction.ASC);

        if (sortBy == null || sortBy.isBlank()) {
            return org.springframework.data.domain.PageRequest.of(page, size);
        }

        return org.springframework.data.domain.PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

}
