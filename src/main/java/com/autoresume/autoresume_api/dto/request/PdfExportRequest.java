package com.autoresume.autoresume_api.dto.request;

import java.util.UUID;

import lombok.Data;

@Data
public class PdfExportRequest {
    private UUID resumeId;
    private String html;
}
