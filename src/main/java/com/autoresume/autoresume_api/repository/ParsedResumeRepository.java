package com.autoresume.autoresume_api.repository;

import com.autoresume.autoresume_api.model.ParsedResumeData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParsedResumeRepository extends JpaRepository<ParsedResumeData, UUID> {
    Optional<ParsedResumeData> findByResumeId(UUID resumeId);
}