package com.example.demo.profile.model.projection;

import com.example.demo.profile.model.entity.enums.PrivacyLevel;

import java.time.LocalDate;

public interface WorkProjection {


    String getJobTitle();

    LocalDate getStartDate();

    LocalDate getEndDate();

    String getLocation();

    String getDescription();

    // privacy of work
    PrivacyLevel getPrivacyLevel();

    // page of work
    String getPageName();

    String getPageSlug();

    // img url todo

}
