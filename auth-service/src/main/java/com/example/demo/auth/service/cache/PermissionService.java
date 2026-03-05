package com.example.demo.auth.service.cache;

import com.example.demo.auth.model.entity.Resource;
import com.example.demo.common.constant.ActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    private final ResourceCacheService resourceCacheService;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public Resource findMatch(String method, String uri) {

        List<Resource> resources = resourceCacheService.loadAllResources();

        Resource matched = resources.stream()
                .filter(r -> r.getHttpMethod().equalsIgnoreCase(method))
                .filter(r -> matcher.match(r.getPattern(), uri))
                .findFirst()
                .orElse(null);

        if (matched != null) {
            log.debug("Matched resource: {} {} -> {}",
                    method, uri, matched.getPattern());
        } else {
            log.debug("No resource matched for {} {}", method, uri);
        }

        return matched;
    }

    public List<String> extractPermissions(int permissionValue) {
        List<String> permissions = new ArrayList<>();

        for (ActionType action : ActionType.values()) {
            if ((permissionValue & action.getValue()) != 0) {
                permissions.add(action.name().toLowerCase());
            }
        }

        return permissions;
    }

}
