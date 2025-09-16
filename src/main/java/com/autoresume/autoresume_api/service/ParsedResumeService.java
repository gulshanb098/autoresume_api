package com.autoresume.autoresume_api.service;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.autoresume.autoresume_api.dto.request.ParsedResumeUpdateRequest;
import com.autoresume.autoresume_api.dto.response.APIResponse;
import com.autoresume.autoresume_api.model.ParsedResumeData;

public interface ParsedResumeService {
    ResponseEntity<APIResponse<ParsedResumeData>> getParsedDataByResumeId(UUID resumeId, String userEmail);

    ResponseEntity<APIResponse<String>> updateParsedResume(UUID resumeId, String userEmail,
            ParsedResumeUpdateRequest request);
}