package com.alien.bank.management.system.service.impl;

import com.alien.bank.management.system.entity.Transaction;
import com.alien.bank.management.system.entity.TransactionType;
import com.alien.bank.management.system.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class PdfStatementServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private PdfStatementServiceImpl pdfStatementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateAccountStatement() {
        // Arrange: mock transactions
        Transaction tx1 = Transaction.builder()
                .id(1L)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .notes("Initial deposit")
                .timestamp(new Date())
                .build();

        Transaction tx2 = Transaction.builder()
                .id(2L)
                .type(TransactionType.WITHDRAW)
                .amount(50.0)
                .notes("ATM withdrawal")
                .timestamp(new Date())
                .build();

        List<Transaction> transactions = Arrays.asList(tx1, tx2);

        when(transactionRepository.findByAccountId(1L)).thenReturn(transactions);

        // Act
        ByteArrayInputStream pdfStream = pdfStatementService.generateAccountStatement(1L);

        // Assert
        assertNotNull(pdfStream, "The generated PDF stream should not be null");
    }
}
