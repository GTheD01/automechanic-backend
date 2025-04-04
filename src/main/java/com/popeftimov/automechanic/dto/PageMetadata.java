package com.popeftimov.automechanic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Pagination metadata for a paginated response")
public class PageMetadata {
    @Schema(description = "Size of the page (number of items per page)")
    private int size;

    @Schema(description = "Page number")
    private int number;

    @Schema(description = "Total number of elements in the dataset")
    private long totalElements;

    @Schema(description = "Total number of pages in the dataset")
    private int totalPages;
}
