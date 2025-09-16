package com.autoresume.autoresume_api.util;

import java.util.Arrays;

public enum AllowedFileType {
    PDF("application/pdf", ".pdf"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx"),
    TXT("text/plain", ".txt");

    private final String mimeType;
    private final String extension;

    AllowedFileType(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public static boolean isAllowed(String contentType, String fileName) {
        return Arrays.stream(values())
                .anyMatch(type -> type.getMimeType().equalsIgnoreCase(contentType) &&
                        fileName.toLowerCase().endsWith(type.getExtension()));
    }
}
