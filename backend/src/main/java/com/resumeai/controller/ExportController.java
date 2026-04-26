package com.resumeai.controller;

import com.resumeai.model.AnalysisResult;
import com.resumeai.service.AiAnalysisService;
import com.resumeai.service.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final AiAnalysisService aiAnalysisService;
    private final PdfExportService pdfExportService;

    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) throws Exception {
        AnalysisResult result = aiAnalysisService.getAnalysisEntity(id);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("analysis-report-" + id + ".pdf").build().toString())
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfExportService.exportAnalysis(result));
    }
}
