package com.autoresume.autoresume_api.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.autoresume.autoresume_api.dto.response.APIResponse;
import com.autoresume.autoresume_api.dto.response.ResumeUploadResponse;
import com.autoresume.autoresume_api.exception.AccessDeniedException;
import com.autoresume.autoresume_api.exception.FileTypeNotAllowedException;
import com.autoresume.autoresume_api.exception.ResourceNotFoundException;
import com.autoresume.autoresume_api.model.ParsedResumeData;
import com.autoresume.autoresume_api.model.Resume;
import com.autoresume.autoresume_api.model.User;
import com.autoresume.autoresume_api.repository.ParsedResumeRepository;
import com.autoresume.autoresume_api.repository.ResumeRepository;
import com.autoresume.autoresume_api.repository.UserRepository;
import com.autoresume.autoresume_api.service.ResumeService;
import com.autoresume.autoresume_api.util.AllowedFileType;
import com.autoresume.autoresume_api.util.PdfResumeGeneratorService;
import com.autoresume.autoresume_api.util.ResumeParserUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResumeServiceImpl implements ResumeService {

    // Store uploaded files in a top-level `uploads/` folder in project root
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads";

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ParsedResumeRepository parsedResumeDataRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<APIResponse<ResumeUploadResponse>> uploadResume(MultipartFile file, String userEmail)
            throws Exception {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (!AllowedFileType.isAllowed(file.getContentType(), originalFileName)) {
            throw new FileTypeNotAllowedException("❌ File type not allowed. Only PDF, DOCX, and TXT are supported.");
        }

        UUID resumeId = UUID.randomUUID(); // Unique ID for both DB and file

        String uniqueFileName = resumeId + "_" + originalFileName;

        try {
            // Ensure upload directory exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath.toAbsolutePath());
            }

            // Save file to disk
            Path filePath = uploadPath.resolve(uniqueFileName);
            file.transferTo(filePath.toFile());
            log.info("File saved to: {}", filePath.toAbsolutePath());

            // Fetch user by email (should already exist)
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

            // Save metadata to DB
            Resume resume = Resume.builder()
                    .id(resumeId)
                    .fileName(uniqueFileName)
                    .uploadedAt(LocalDateTime.now())
                    .user(user)
                    .build();

            resumeRepository.save(resume);
            log.info("Resume metadata saved to DB for user: {}", userEmail);

            ParsedResumeData parsedData = ResumeParserUtil.parse(file);
            parsedData.setResume(resume);
            parsedResumeDataRepository.save(parsedData);

            return ResponseEntity.ok(
                    APIResponse.success(
                            new ResumeUploadResponse(
                                    "✅ Resume uploaded successfully!",
                                    uniqueFileName,
                                    userEmail,
                                    resume.getUploadedAt())));

        } catch (IOException e) {
            log.error("Failed to upload resume", e);
            throw new Exception("File upload failed. Please try again.");
        }
    }

    @Override
    public ResponseEntity<Resource> downloadResume(UUID resumeId, String email) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + resumeId));

        // Validate that the resume belongs to the user
        if (!resume.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("You are not authorized to download this resume.");
        }

        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(resume.getFileName());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new ResourceNotFoundException("Resume file not found on disk");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resume.getFileName() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            log.error("Error downloading resume file", e);
            throw new RuntimeException("Failed to download resume");
        }
    }

    @Override
    public ResponseEntity<Resource> downloadFinalResume(UUID resumeId, String userEmail) {
        try {
            Resume resume = resumeRepository.findById(resumeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + resumeId));

            if (!resume.getUser().getEmail().equals(userEmail)) {
                throw new AccessDeniedException("You do not have access to this resume.");
            }

            if (resume.getParsedData() == null) {
                throw new ResourceNotFoundException("Parsed resume data not found.");
            }

            byte[] pdfBytes = PdfResumeGeneratorService.generateResumePdf(resume.getParsedData());
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"final_resume.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (ResourceNotFoundException | AccessDeniedException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("Error generating final resume PDF", e);
            throw new RuntimeException("Internal server error");
        }
    }

    @Override
    public ResponseEntity<Resource> generatePdfFromHtml(UUID resumeId, String userEmail, String html) {
        try {
            Resume resume = resumeRepository.findById(resumeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + resumeId));

            if (!resume.getUser().getEmail().equals(userEmail)) {
                throw new AccessDeniedException("You do not have access to this resume.");
            }

            byte[] pdfBytes = PdfResumeGeneratorService.convertHtmlToPdf(html);
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resume.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (ResourceNotFoundException | AccessDeniedException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("Error exporting PDF from HTML", e);
            throw new RuntimeException("Internal server error");
        }
    }
}
