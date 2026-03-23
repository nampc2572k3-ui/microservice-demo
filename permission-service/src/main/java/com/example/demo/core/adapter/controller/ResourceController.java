package com.example.demo.core.adapter.controller;

import com.example.demo.common.utils.ResponseUtils;
import com.example.demo.core.application.dto.request.ResourceRequest;
import com.example.demo.core.application.dto.response.ResourceResponse;
import com.example.demo.core.application.dto.response.common.BaseResponse;
import com.example.demo.core.application.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.core.application.dto.request.common.PageRequest;

import java.util.List;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<ResourceResponse>>> getAll(
            @ModelAttribute @Valid PageRequest request
    ) {
        var page = resourceService.getAll(request);
        return ResponseEntity.ok(ResponseUtils.success(
                        page.getContent(),
                        "Get data successfully",
                        page
                )
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> create(
            @RequestBody @Valid ResourceRequest request
            ) {
        resourceService.create(request);
        return ResponseEntity.ok(ResponseUtils.success("Create data successfully"));
    }

    @PutMapping("/{rId}")
    public ResponseEntity<BaseResponse<Void>> update(
            @PathVariable Long rId,
            @RequestBody @Valid ResourceRequest request
    ) {
        resourceService.update(rId ,request);
        return ResponseEntity.ok(ResponseUtils.success("Update data successfully"));
    }

    @PatchMapping("/{rId}/toggle")
    public ResponseEntity<BaseResponse<Void>> toggle(
            @PathVariable Long rId
    ) {
        resourceService.toggleActive(rId);
        return ResponseEntity.ok(ResponseUtils.success("Toggle resource successfully"));
    }

    @DeleteMapping("/{rId}")
    public ResponseEntity<BaseResponse<Void>> delete(
            @PathVariable Long rId
    ) {
        resourceService.delete(rId);
        return ResponseEntity.ok(ResponseUtils.success("Delete resource successfully"));
    }
}
