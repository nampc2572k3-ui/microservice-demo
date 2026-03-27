package com.example.demo.core.application.service.impl;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.example.demo.core.application.dto.request.ResourceRequest;
import com.example.demo.core.application.dto.request.common.PageRequest;
import com.example.demo.core.application.dto.response.ResourceResponse;
import com.example.demo.core.domain.model.entity.Resource;
import org.springframework.data.domain.Page;
import com.example.demo.core.persistence.ResourceRepository;
import com.example.demo.core.application.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<ResourceResponse> getAll(PageRequest request) {
        Pageable pageable = PageRequest.toPageable(
                request.getPage(),
                request.getSize(),
                request.getSortBy(),
                request.getDirection()
        );

        return resourceRepository.findAllResourceResponses(pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(ResourceRequest request) {

        if (resourceRepository.existsByHttpMethodAndPathPattern(
                request.getPathPattern(), request.getHttpMethod())) {
            throw new CustomBusinessException(
                    ErrorCode.RESOURCE_ALREADY_EXISTS.getCode(),
                    ErrorCode.RESOURCE_ALREADY_EXISTS.getMessage()
            );
        }

        Resource resource = Resource.builder()
                .pathPattern(request.getPathPattern())
                .httpMethod(request.getHttpMethod())
                .description(request.getDescription())
                .action(request.getAction())
                .active(true)
                .build();

        resourceRepository.save(resource);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Long rId, ResourceRequest request) {

        Resource resource = resourceRepository.findById(rId)
                .orElseThrow(() -> new CustomBusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                        ErrorCode.RESOURCE_NOT_FOUND.getMessage()
                ));

        resource.setPathPattern(request.getPathPattern());
        resource.setHttpMethod(request.getHttpMethod());
        resource.setDescription(request.getDescription());
        resource.setAction(request.getAction());

        resourceRepository.save(resource);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void toggleActive(Long rId) {
        Resource resource = resourceRepository.findById(rId)
                .orElseThrow(() -> new CustomBusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                        ErrorCode.RESOURCE_NOT_FOUND.getMessage()
                ));

        resource.setActive(!resource.isActive());
        resourceRepository.save(resource);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long rId) {
        Resource resource = resourceRepository.findById(rId)
                .orElseThrow(() -> new CustomBusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                        ErrorCode.RESOURCE_NOT_FOUND.getMessage()
                ));

        resource.setDeleted(true);
        resourceRepository.save(resource);
    }


}
