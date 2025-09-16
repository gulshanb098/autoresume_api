package com.autoresume.autoresume_api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoresume.autoresume_api.model.Resume;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {
}
