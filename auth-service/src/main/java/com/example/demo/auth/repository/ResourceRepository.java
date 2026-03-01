package com.example.demo.auth.repository;

import com.example.demo.auth.model.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Resource findByHttpMethodAndPattern(String method, String uri);
}
