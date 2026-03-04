package com.example.demo.auth.controller;

import com.example.demo.auth.model.dto.response.BaseResponse;
import com.example.demo.common.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test1")
@RequiredArgsConstructor
public class TestController1 {


    @PostMapping("/post")
    public ResponseEntity<BaseResponse<?>> post() {
        return ResponseEntity.ok(
                ResponseUtils.success("test")
        );
    }

    @GetMapping("/get")
    public ResponseEntity<BaseResponse<?>> get() {
        return ResponseEntity.ok(
                ResponseUtils.success("test")
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<BaseResponse<?>> get(@PathVariable String id) {
        return ResponseEntity.ok(
                ResponseUtils.success("test")
        );
    }


    @PutMapping("/put")
    public ResponseEntity<BaseResponse<?>> put() {
        return ResponseEntity.ok(
                ResponseUtils.success("test")
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse<?>> delete() {
        return ResponseEntity.ok(
                ResponseUtils.success("test")
        );
    }

}
