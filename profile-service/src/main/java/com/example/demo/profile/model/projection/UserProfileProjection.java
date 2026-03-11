package com.example.demo.profile.model.projection;

import java.time.LocalDate;
import java.util.List;

public interface UserProfileProjection {

    Long  getId();

    String getName();

    LocalDate getDob();

    String getLocation();

    String getHometown();

    String getEmail();

    String getPhone();

    List<PrivacyProjection> getPrivacySettings();

    List<WorkProjection> getWorks();


}
