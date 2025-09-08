package com.alien.bank.management.system.service;

public interface NotificationService {
    void sendTransactionEmail(String to, String subject, String body);
}
