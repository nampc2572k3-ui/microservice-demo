package com.example.demo.core.domain.event.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PermissionChangedEvent {

    private String accountId;

}
