package com.ptn.internal.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseRequest {
    private int startIndex;
    private int maxPerPage = 150;
}
