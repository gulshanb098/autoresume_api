package com.autoresume.autoresume_api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response returned after a resume is uploaded successfully.")
public class ResumeUploadResponse {
    @Schema(description = "Success message", example = "Resume uploaded successfully!")
    private String message;

    @Schema(description = "Uploaded file name", example = "my_resume.pdf")
    private String fileName;

    @Schema(description = "User email", example = "user@example.com")
    private String userEmail;

    @Schema(description = "Timestamp of upload")
    private LocalDateTime uploadedAt;
}
