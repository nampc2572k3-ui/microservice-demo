package com.example.demo.auth.controller;

import com.example.demo.auth.model.dto.response.BaseResponse;
import com.example.demo.common.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test3")
@RequiredArgsConstructor
public class TestController3 {


    @PostMapping
    public ResponseEntity<BaseResponse<?>> post() {
        return ResponseEntity.ok(
                ResponseUtils.success("test")
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<?>> get() {
        return ResponseEntity.ok(
                ResponseUtils.success("test")
        );
    }

    @PutMapping
    public ResponseEntity<BaseResponse<?>> put() {
        return ResponseEntity.ok(
                ResponseUtils.success("test")
        );
    }

    @DeleteMapping
    public ResponseEntity<BaseResponse<?>> delete() {
        return ResponseEntity.ok(
                ResponseUtils.success("test")
        );
    }

}

