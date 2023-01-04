package com.buxz.dev.combuxzjobxservice.repository;

import com.buxz.dev.combuxzjobxservice.entity.WorkExperienceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkExperienceRepository extends JpaRepository<WorkExperienceEntity, Integer> {
}
