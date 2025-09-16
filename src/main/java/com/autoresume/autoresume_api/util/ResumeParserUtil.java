package com.autoresume.autoresume_api.util;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.web.multipart.MultipartFile;

import com.autoresume.autoresume_api.model.ParsedResumeData;

public class ResumeParserUtil {

    public static ParsedResumeData parse(MultipartFile file) throws Exception {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        try (InputStream stream = file.getInputStream()) {
            parser.parse(stream, handler, metadata, context);
            String content = handler.toString().replaceAll("[\\r\\f]+", "").trim();

            return ParsedResumeData.builder()
                    .name(extractName(content))
                    .email(extractEmail(content))
                    .phone(extractPhone(content))
                    .address(extractAddress(content))
                    .summary(extractSummary(content))
                    .education(extractSection(content, "education"))
                    .experience(extractSection(content, "experience|work experience|employment"))
                    .skills(extractSection(content, "skills"))
                    .build();
        }
    }

    private static String extractName(String content) {
        // Assume first non-empty line is the name
        for (String line : content.split("\n")) {
            line = line.trim();
            if (!line.isEmpty() && !line.toLowerCase().contains("email") && line.length() <= 60) {
                return line;
            }
        }
        return null;
    }

    private static String extractEmail(String content) {
        Matcher matcher = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(content);
        return matcher.find() ? matcher.group() : null;
    }

    private static String extractPhone(String content) {
        Matcher matcher = Pattern.compile("(?:(?:\\+\\d{1,3}[\\s-]?)?(\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}))")
                .matcher(content);
        return matcher.find() ? matcher.group() : null;
    }

    private static String extractAddress(String content) {
        // Heuristic: Address is usually near the top, right after name
        String[] lines = content.split("\n");
        StringBuilder address = new StringBuilder();
        for (int i = 1; i < Math.min(lines.length, 6); i++) {
            String line = lines[i].trim();
            if (line.isEmpty())
                break;
            if (line.toLowerCase().contains("education"))
                break;
            address.append(line).append(" ");
        }
        return address.toString().trim();
    }

    private static String extractSummary(String content) {
        String[] lines = content.split("\n");
        StringBuilder summary = new StringBuilder();
        boolean started = false;

        for (String line : lines) {
            line = line.trim();
            if (line.toLowerCase().contains("summary")) {
                started = true;
                continue;
            }
            if (started && line.toLowerCase().matches("^(education|experience|skills).*"))
                break;
            if (started && !line.isEmpty())
                summary.append(line).append(" ");
        }

        return summary.toString().trim();
    }

    private static String extractSection(String content, String sectionNamePattern) {
        Pattern pattern = Pattern.compile("(?i)(" + sectionNamePattern + ")\\s*[:\\-\\n]+([\\s\\S]{0,1000})");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String data = matcher.group(2).trim();
            // Clean up next section heading accidentally caught
            data = data.replaceAll("(?i)(education|experience|skills|summary).*", "").trim();
            return data;
        }
        return null;
    }
}
