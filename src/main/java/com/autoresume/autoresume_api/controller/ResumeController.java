package com.autoresume.autoresume_api.controller;

import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.autoresume.autoresume_api.dto.request.PdfExportRequest;
import com.autoresume.autoresume_api.dto.response.APIResponse;
import com.autoresume.autoresume_api.dto.response.ResumeUploadResponse;
import com.autoresume.autoresume_api.service.ResumeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@Tag(name = "Resume", description = "Endpoints related to resume upload and parsing")
public class ResumeController {

        private final ResumeService resumeService;

        @Operation(summary = "Upload resume file", description = "Uploads a resume (PDF, DOCX, or TXT), saves file & metadata, and returns upload info.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "✅ Resume uploaded successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResumeUploadResponse.class))),
                        @ApiResponse(responseCode = "400", description = "❌ Bad Request - Invalid file"),
                        @ApiResponse(responseCode = "500", description = "❌ Internal Server Error")
        })
        @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<APIResponse<ResumeUploadResponse>> uploadResume(
                        @RequestPart("file") MultipartFile file,
                        @AuthenticationPrincipal Jwt jwt) throws Exception {

                String email = jwt.getClaimAsString("email");
                return resumeService.uploadResume(file, email);
        }

        @Operation(summary = "Download resume by ID", description = "Downloads the resume file associated with the given ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "✅ Resume downloaded successfully", content = {
                                        @Content(mediaType = "application/octet-stream")
                        }),
                        @ApiResponse(responseCode = "404", description = "❌ Resume not found"),
                        @ApiResponse(responseCode = "500", description = "❌ Internal server error")
        })
        @GetMapping(value = "/download/{resumeId}")
        public ResponseEntity<Resource> downloadResume(
                        @PathVariable UUID resumeId,
                        @AuthenticationPrincipal Jwt jwt) {

                String email = jwt.getClaimAsString("email");
                return resumeService.downloadResume(resumeId, email);
        }

        @Operation(summary = "Download final edited resume", description = "Generates and downloads a PDF from the latest parsed and edited resume data.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "✅ Final resume downloaded successfully", content = {
                                        @Content(mediaType = "application/octet-stream")
                        }),
                        @ApiResponse(responseCode = "404", description = "❌ Resume or parsed data not found"),
                        @ApiResponse(responseCode = "500", description = "❌ Internal server error")
        })
        @GetMapping("/download/final/{resumeId}")
        public ResponseEntity<Resource> downloadFinalResume(
                        @PathVariable UUID resumeId,
                        @AuthenticationPrincipal Jwt jwt) {

                String email = jwt.getClaimAsString("email");
                return resumeService.downloadFinalResume(resumeId, email);
        }

        @Operation(summary = "Generate and download final PDF from HTML", description = "Generates and downloads a resume PDF based on frontend-rendered HTML.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "✅ Final resume PDF downloaded successfully", content = {
                                        @Content(mediaType = "application/pdf")
                        }),
                        @ApiResponse(responseCode = "400", description = "❌ Invalid HTML"),
                        @ApiResponse(responseCode = "403", description = "❌ Access denied"),
                        @ApiResponse(responseCode = "500", description = "❌ Internal Server Error")
        })
        @PostMapping("/export-pdf")
        public ResponseEntity<Resource> exportResumePdf(
                        @RequestBody PdfExportRequest request,
                        @AuthenticationPrincipal Jwt jwt) {

                String email = jwt.getClaimAsString("email");
                return resumeService.generatePdfFromHtml(request.getResumeId(), email, request.getHtml());
        }

}
