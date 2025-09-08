package com.alien.bank.management.system.service.impl;

import com.alien.bank.management.system.entity.Transaction;
import com.alien.bank.management.system.repository.TransactionRepository;
import com.alien.bank.management.system.service.PdfStatementService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfStatementServiceImpl implements PdfStatementService {

    private final TransactionRepository transactionRepository;

    @Override
    public ByteArrayInputStream generateAccountStatement(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Header
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph header = new Paragraph("Bank Account Statement", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(new Paragraph("Account ID: " + accountId));
            document.add(Chunk.NEWLINE);

            // Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            addTableHeader(table, "Date");
            addTableHeader(table, "Type");
            addTableHeader(table, "Amount");
            addTableHeader(table, "Notes");

            for (Transaction tx : transactions) {
                table.addCell(tx.getTimestamp().toString());
                table.addCell(tx.getType().toString());
                table.addCell(tx.getAmount().toString());
                table.addCell(tx.getNotes() != null ? tx.getNotes() : "-");
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTableHeader(PdfPTable table, String title) {
        PdfPCell header = new PdfPCell();
        header.setPhrase(new Phrase(title));
        table.addCell(header);
    }
}
