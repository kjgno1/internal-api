package com.ptn.internal.webservice;

import com.ptn.internal.constant.Constants;
import com.ptn.internal.model.dto.GeneralResponse;
import com.ptn.internal.model.dto.Status;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class BaseWS {
    public <T> ResponseEntity<GeneralResponse<T>> success(T data) {
        Status status = Status.builder()
                .code(Constants.STATUS_CODE.SUCCESS)
                .message(Constants.STATUS_MESSAGE.SUCCESS)
                .timestamp(LocalDateTime.now())
                .build();
        GeneralResponse<T> build = new GeneralResponse<>();
        build.setStatus(status);
        build.setData(data);
        return ResponseEntity.ok(build);
    }

    public <T> ResponseEntity<GeneralResponse<T>> success() {
        Status status = Status.builder()
                .code(Constants.STATUS_CODE.SUCCESS)
                .message(Constants.STATUS_MESSAGE.SUCCESS)
                .timestamp(LocalDateTime.now())
                .build();
        GeneralResponse<T> build = new GeneralResponse<>();
        build.setStatus(status);
        return ResponseEntity.ok(build);
    }

    public <T> ResponseEntity<GeneralResponse<T>> failed() {
        Status status = Status.builder()
                .code(Constants.STATUS_CODE.FAILED)
                .message(Constants.STATUS_MESSAGE.FAILED)
                .timestamp(LocalDateTime.now())
                .build();
        GeneralResponse<T> build = new GeneralResponse<>();
        build.setStatus(status);
        return ResponseEntity.ok(build);
    }
}
