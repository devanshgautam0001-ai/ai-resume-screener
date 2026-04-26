package com.resumeai.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ResumeParserService {

    private static final int MAX_SECTION_ITEM_LENGTH = 280;

    private static final List<String> KNOWN_SKILLS = List.of(
        "java", "spring boot", "react", "tailwind", "postgresql", "docker", "kubernetes",
        "python", "machine learning", "nlp", "aws", "rest api", "microservices", "hibernate",
        "git", "sql", "mongodb", "redis", "figma", "node.js", "typescript", "javascript",
        "spring security", "jwt", "mysql", "c++", "data structures", "algorithms"
    );

    private final Tika tika = new Tika();

    public ParsedResumeDraft parse(MultipartFile file) throws Exception {
        String rawText = extractRawText(file);
        String normalized = rawText == null ? "" : rawText.replaceAll("\\s+", " ").trim();

        return new ParsedResumeDraft(
            extractName(rawText),
            extractFirstMatch(normalized, "[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+"),
            extractFirstMatch(normalized, "(\\+?\\d[\\d\\s-]{8,}\\d)"),
            extractSkills(normalized),
            extractSection(rawText, List.of("experience", "work history", "employment")),
            extractSection(rawText, List.of("education", "academic")),
            extractSection(rawText, List.of("projects", "portfolio")),
            estimateExperienceYears(normalized),
            normalized
        );
    }

    private String extractRawText(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        String contentType = Optional.ofNullable(file.getContentType()).orElse("");
        String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse("").toLowerCase(Locale.ROOT);

        if (contentType.contains("pdf") || fileName.endsWith(".pdf")) {
            try (PDDocument document = Loader.loadPDF(bytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                return stripper.getText(document);
            }
        }

        return tika.parseToString(new ByteArrayInputStream(bytes));
    }

    private String extractName(String text) {
        if (text == null || text.isBlank()) {
            return "Unknown Candidate";
        }
        return Arrays.stream(text.split("\\r?\\n"))
            .map(String::trim)
            .filter(line -> !line.isBlank())
            .filter(line -> line.length() <= 70)
            .findFirst()
            .orElse("Unknown Candidate");
    }

    private String extractFirstMatch(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (!matcher.find()) {
            return "";
        }
        return matcher.groupCount() >= 1 && matcher.group(1) != null ? matcher.group(1) : matcher.group();
    }

    private List<String> extractSkills(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        return KNOWN_SKILLS.stream()
            .filter(lower::contains)
            .map(this::titleCase)
            .distinct()
            .toList();
    }

    private List<String> extractSection(String text, List<String> headings) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String lower = text.toLowerCase(Locale.ROOT);
        return headings.stream()
            .map(lower::indexOf)
            .filter(index -> index >= 0)
            .findFirst()
            .map(index -> {
                int end = Math.min(text.length(), index + 600);
                String snippet = text.substring(index, end);
                return Arrays.stream(snippet.split("[-\\u2022\\n]"))
                    .map(String::trim)
                    .filter(line -> line.length() > 10)
                    .map(this::limitLength)
                    .limit(6)
                    .collect(Collectors.toList());
            })
            .orElseGet(ArrayList::new);
    }

    private Integer estimateExperienceYears(String text) {
        Matcher matcher = Pattern.compile("(\\d{1,2})\\+?\\s+years").matcher(text.toLowerCase(Locale.ROOT));
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    private String limitLength(String value) {
        if (value.length() <= MAX_SECTION_ITEM_LENGTH) {
            return value;
        }
        return value.substring(0, MAX_SECTION_ITEM_LENGTH - 3).trim() + "...";
    }

    private String titleCase(String value) {
        return Arrays.stream(value.split(" "))
            .map(word -> word.isBlank() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1))
            .collect(Collectors.joining(" "));
    }

    public record ParsedResumeDraft(
        String candidateName,
        String email,
        String phone,
        List<String> skills,
        List<String> experiences,
        List<String> education,
        List<String> projects,
        Integer experienceYears,
        String rawText
    ) {
    }
}
