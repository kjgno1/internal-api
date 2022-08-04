package com.ptn.internal.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BestHqRequest {
    private int pageNumber;
    private int total;
    private String baseUrl;
}
