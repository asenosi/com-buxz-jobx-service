package com.buxz.dev.combuxzjobxservice.repository;

import com.buxz.dev.combuxzjobxservice.entity.BusinessProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessProfileRepository extends JpaRepository<BusinessProfileEntity, Integer> {
}
