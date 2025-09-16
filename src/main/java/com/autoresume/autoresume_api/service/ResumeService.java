package com.autoresume.autoresume_api.service;

import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.autoresume.autoresume_api.dto.response.APIResponse;
import com.autoresume.autoresume_api.dto.response.ResumeUploadResponse;

public interface ResumeService {
    ResponseEntity<APIResponse<ResumeUploadResponse>> uploadResume(MultipartFile file, String userEmail)
            throws Exception;

    ResponseEntity<Resource> downloadResume(UUID resumeId, String email);

    ResponseEntity<Resource> downloadFinalResume(UUID resumeId, String email);

    ResponseEntity<Resource> generatePdfFromHtml(UUID resumeId, String userEmail, String html);
}
