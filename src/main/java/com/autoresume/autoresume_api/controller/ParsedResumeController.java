package com.autoresume.autoresume_api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoresume.autoresume_api.dto.request.ParsedResumeUpdateRequest;
import com.autoresume.autoresume_api.dto.response.APIResponse;
import com.autoresume.autoresume_api.model.ParsedResumeData;
import com.autoresume.autoresume_api.service.ParsedResumeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/parsed")
@RequiredArgsConstructor
public class ParsedResumeController {

    private final ParsedResumeService parsedResumeService;

    @Operation(summary = "Get parsed resume data by ID", description = "Returns parsed resume data for a specific resume ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "✅ Parsed resume found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParsedResumeData.class))),
            @ApiResponse(responseCode = "404", description = "❌ Parsed resume not found"),
            @ApiResponse(responseCode = "500", description = "❌ Internal Server Error")
    })
    @GetMapping("/{resumeId}")
    public ResponseEntity<APIResponse<ParsedResumeData>> getParsedData(
            @PathVariable UUID resumeId,
            @AuthenticationPrincipal Jwt jwt) {

        String userEmail = jwt.getClaimAsString("email");
        return parsedResumeService.getParsedDataByResumeId(resumeId, userEmail);
    }

    @Operation(summary = "Update parsed resume", description = "Updates the parsed resume data by resume ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "✅ Resume updated successfully"),
            @ApiResponse(responseCode = "404", description = "❌ Resume or parsed data not found"),
            @ApiResponse(responseCode = "403", description = "❌ Access denied"),
            @ApiResponse(responseCode = "500", description = "❌ Internal server error")
    })
    @PutMapping("/{resumeId}")
    public ResponseEntity<APIResponse<String>> updateParsedResume(
            @PathVariable UUID resumeId,
            @RequestBody ParsedResumeUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        return parsedResumeService.updateParsedResume(resumeId, email, request);
    }
}
