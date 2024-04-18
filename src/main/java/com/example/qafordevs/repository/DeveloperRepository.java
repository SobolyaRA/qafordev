package com.example.qafordevs.repository;

import com.example.qafordevs.entity.DeveloperEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeveloperRepository extends JpaRepository<DeveloperEntity, Integer> {
    DeveloperEntity findByEmail(String email);

    @Query("SELECT d FROM DeveloperEntity d WHERE d.status = 'ACTIVE' and d.specialty = ?1")
    List<DeveloperEntity> findAllActiveBySpeciality(String specialty);
}
