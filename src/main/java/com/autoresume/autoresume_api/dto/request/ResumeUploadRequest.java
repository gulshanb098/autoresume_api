package com.autoresume.autoresume_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResumeUploadRequest {

    @Schema(description = "PDF resume file", type = "string", format = "binary", required = true)
    private MultipartFile file;
}
