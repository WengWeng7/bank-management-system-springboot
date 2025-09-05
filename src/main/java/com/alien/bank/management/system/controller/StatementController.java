package com.alien.bank.management.system.controller;

import com.alien.bank.management.system.service.PdfStatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/statements")
@RequiredArgsConstructor
public class StatementController {

    private final PdfStatementService pdfStatementService;

    @GetMapping("/{accountId}")
    public ResponseEntity<InputStreamResource> downloadStatement(@PathVariable Long accountId) {
        ByteArrayInputStream pdf = pdfStatementService.generateAccountStatement(accountId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=statement.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }
}
