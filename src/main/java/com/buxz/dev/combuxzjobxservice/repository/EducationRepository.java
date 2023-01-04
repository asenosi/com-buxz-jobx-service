package com.buxz.dev.combuxzjobxservice.repository;

import com.buxz.dev.combuxzjobxservice.entity.EducationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<EducationEntity, Integer> {
    List<EducationEntity> findAllByVisible(boolean visible);
}
