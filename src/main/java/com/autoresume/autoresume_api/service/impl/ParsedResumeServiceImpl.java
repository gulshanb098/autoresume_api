package com.autoresume.autoresume_api.service.impl;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.autoresume.autoresume_api.dto.request.ParsedResumeUpdateRequest;
import com.autoresume.autoresume_api.dto.response.APIResponse;
import com.autoresume.autoresume_api.exception.AccessDeniedException;
import com.autoresume.autoresume_api.exception.ResourceNotFoundException;
import com.autoresume.autoresume_api.model.ParsedResumeData;
import com.autoresume.autoresume_api.model.Resume;
import com.autoresume.autoresume_api.repository.ParsedResumeRepository;
import com.autoresume.autoresume_api.repository.ResumeRepository;
import com.autoresume.autoresume_api.service.ParsedResumeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParsedResumeServiceImpl implements ParsedResumeService {

    private final ParsedResumeRepository parsedResumeRepository;
    private final ResumeRepository resumeRepository;

    @Override
    public ResponseEntity<APIResponse<ParsedResumeData>> getParsedDataByResumeId(UUID resumeId, String userEmail) {
        try {
            ParsedResumeData parsedData = parsedResumeRepository.findByResumeId(resumeId)
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Parsed resume not found for resume ID: " + resumeId));

            if (!parsedData.getResume().getUser().getEmail().equals(userEmail)) {
                throw new AccessDeniedException("You do not have access to this parsed resume.");
            }

            return ResponseEntity.ok(APIResponse.success(parsedData));
        } catch (ResourceNotFoundException | AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.error("❌ " + ex.getMessage()));
        } catch (Exception e) {
            log.error("Error retrieving parsed resume data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.error("❌ Internal Server Error"));
        }
    }

    @Override
    public ResponseEntity<APIResponse<String>> updateParsedResume(UUID resumeId, String userEmail,
            ParsedResumeUpdateRequest request) {
        try {
            Resume resume = resumeRepository.findById(resumeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

            if (!resume.getUser().getEmail().equals(userEmail)) {
                throw new AccessDeniedException("You are not authorized to update this resume.");
            }

            ParsedResumeData parsedData = parsedResumeRepository.findByResumeId(resumeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parsed resume data not found."));

            parsedData.setName(request.getName());
            parsedData.setEmail(request.getEmail());
            parsedData.setPhone(request.getPhone());
            parsedData.setAddress(request.getAddress());
            parsedData.setEducation(request.getEducation());
            parsedData.setExperience(request.getExperience());
            parsedData.setSkills(request.getSkills());
            parsedData.setSummary(request.getSummary());

            parsedResumeRepository.save(parsedData);

            return ResponseEntity.ok(
                    APIResponse.success("✅ Parsed resume updated successfully."));

        } catch (ResourceNotFoundException | AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.error("❌ " + ex.getMessage()));
        } catch (Exception e) {
            log.error("Error updating parsed resume", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.error("❌ Internal server error"));
        }
    }

}