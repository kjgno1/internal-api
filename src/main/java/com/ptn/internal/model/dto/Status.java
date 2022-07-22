package com.ptn.internal.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Status {
    private String code;
    private String message;
    private LocalDateTime timestamp;
}
