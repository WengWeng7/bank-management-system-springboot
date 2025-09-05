package com.alien.bank.management.system.service;

import java.io.ByteArrayInputStream;

public interface PdfStatementService {
    ByteArrayInputStream generateAccountStatement(Long accountId);
}
