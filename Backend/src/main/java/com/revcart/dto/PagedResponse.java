package com.revcart.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagedResponse<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
}

