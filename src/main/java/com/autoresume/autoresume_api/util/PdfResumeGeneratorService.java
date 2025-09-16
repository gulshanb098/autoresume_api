package com.autoresume.autoresume_api.util;

import java.io.ByteArrayOutputStream;

import com.autoresume.autoresume_api.model.ParsedResumeData;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

public class PdfResumeGeneratorService {

    public static byte[] convertHtmlToPdf(String html) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        }
    }

    public static byte[] generateResumePdf(ParsedResumeData data) throws Exception {
        String html = buildHtmlFromParsedData(data);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        }
    }

    private static String buildHtmlFromParsedData(ParsedResumeData data) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset='UTF-8'>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; margin: 40px; }
                        h1, h2 { color: #333; }
                        .section { margin-bottom: 20px; }
                        .section h2 { border-bottom: 1px solid #ccc; padding-bottom: 5px; }
                    </style>
                </head>
                <body>
                    <h1>%s</h1>
                    <div class='section'>
                        <strong>Email:</strong> %s<br>
                        <strong>Phone:</strong> %s<br>
                        <strong>Address:</strong> %s
                    </div>
                    <div class='section'>
                        <h2>Summary</h2>
                        <p>%s</p>
                    </div>
                    <div class='section'>
                        <h2>Education</h2>
                        <p>%s</p>
                    </div>
                    <div class='section'>
                        <h2>Experience</h2>
                        <p>%s</p>
                    </div>
                    <div class='section'>
                        <h2>Skills</h2>
                        <p>%s</p>
                    </div>
                </body>
                </html>
                """,
                data.getName(),
                data.getEmail(),
                data.getPhone(),
                data.getAddress(),
                data.getSummary(),
                data.getEducation(),
                data.getExperience(),
                data.getSkills());
    }

}
