package com.example.demo.core.adapter.http;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.example.demo.core.application.dto.response.LoginResponse;
import com.example.demo.core.application.dto.response.common.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;

@FeignClient(name = "permission-service")
public interface PermissionClient {

    @GetMapping("/permissions/roles/{accId}")
    ResponseEntity<BaseResponse<List<LoginResponse.RolePermissionResponse>>> getPermissions(@PathVariable String accId);

    default List<LoginResponse.RolePermissionResponse> getPermissionsSafe(String accId) {
        try {
            var response = getPermissions(accId);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getData();
            }
        } catch (Exception e) {
            throw new CustomBusinessException(
                    ErrorCode.FAILED_TO_ASSIGN_ROLE.getCode(),
                    ErrorCode.FAILED_TO_ASSIGN_ROLE.getMessage()
            );
        }
        return Collections.emptyList();
    }
}
