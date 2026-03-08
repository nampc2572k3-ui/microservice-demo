package com.example.demo.profile.model.entity;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BaseEntity extends Audit {

    private String name;

    private String slug;

}
