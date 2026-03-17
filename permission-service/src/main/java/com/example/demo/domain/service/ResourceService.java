package com.example.demo.domain.service;

import com.example.demo.domain.model.dto.request.ResourceRequest;
import com.example.demo.domain.model.dto.request.common.PageRequest;
import com.example.demo.domain.model.dto.response.ResourceResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface ResourceService {
    Page<ResourceResponse> getAll(PageRequest request);

    void create(ResourceRequest request);

    void update(Long rId, @Valid ResourceRequest request);

    void toggleActive(Long rId);

    void delete(Long rId);
}
