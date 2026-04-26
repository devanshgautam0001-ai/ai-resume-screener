package com.resumeai.service;

import com.resumeai.model.AnalysisResult;
import com.resumeai.model.ParsedResumeData;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PdfExportService {

    public byte[] exportAnalysis(AnalysisResult result) throws IOException {
        ParsedResumeData parsedData = result.getResume().getParsedData();
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                PDType1Font bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                stream.beginText();
                stream.setFont(bold, 18);
                stream.newLineAtOffset(50, 780);
                stream.showText("AI Resume Screening Report");
                stream.setFont(regular, 12);

                List<String> lines = List.of(
                    "Candidate: " + parsedData.getCandidateName(),
                    "Email: " + safe(parsedData.getEmail()),
                    "Role: " + result.getJob().getTitle(),
                    "Company: " + result.getJob().getCompany(),
                    "Overall Score: " + result.getOverallScore(),
                    "Match Score: " + result.getMatchScore(),
                    "ATS Score: " + result.getAtsScore(),
                    "Missing Skills: " + safe(result.getMissingSkillsText()).replace("\n", ", "),
                    "Suggestions: " + safe(result.getImprovementSuggestionsText()).replace("\n", " | ")
                );

                for (String line : lines) {
                    stream.newLineAtOffset(0, -20);
                    stream.showText(trim(line));
                }
                stream.endText();
            }

            document.save(output);
            return output.toByteArray();
        }
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }

    private String trim(String value) {
        return value.length() <= 110 ? value : value.substring(0, 107) + "...";
    }
}
