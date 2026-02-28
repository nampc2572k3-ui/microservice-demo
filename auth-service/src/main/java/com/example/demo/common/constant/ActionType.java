package com.example.demo.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ActionType {
    READ(1),
    MODIFY(2),
    DELETE(4);

    private int value;
}
