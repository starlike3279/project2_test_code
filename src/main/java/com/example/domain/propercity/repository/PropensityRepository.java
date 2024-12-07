package com.example.domain.propercity.repository;

import com.example.domain.propercity.entity.Propensity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropensityRepository extends JpaRepository<Propensity, Long> {
    Optional<Propensity> findById(Long id);
}

