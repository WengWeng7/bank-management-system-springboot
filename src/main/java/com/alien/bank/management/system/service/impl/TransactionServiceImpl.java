package com.alien.bank.management.system.service.impl;

import com.alien.bank.management.system.entity.Account;
import com.alien.bank.management.system.entity.Transaction;
import com.alien.bank.management.system.entity.TransactionType;
import com.alien.bank.management.system.exception.LowBalanceException;
import com.alien.bank.management.system.mapper.TransactionMapper;
import com.alien.bank.management.system.model.transaction.DepositRequestModel;
import com.alien.bank.management.system.model.transaction.TransactionResponseModel;
import com.alien.bank.management.system.model.transaction.WithdrawRequestModel;
import com.alien.bank.management.system.repository.AccountRepository;
import com.alien.bank.management.system.repository.TransactionRepository;
import com.alien.bank.management.system.service.NotificationService;
import com.alien.bank.management.system.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final NotificationService notificationService;

    @Override
    public TransactionResponseModel deposit(DepositRequestModel request) {
        Account account = accountRepository
                .findByCardNumber(request.getCard_number())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        Long transactionId = performDeposit(account, request.getAmount());

        // Email Notification
        String subject = "Deposit Successful";
        String body = "You deposited $" + request.getAmount();
        notificationService.sendTransactionEmail("demo@inbox.mailtrap.io", subject, body);

        return transactionMapper.toResponseModel(transactionId, request.getAmount(), account.getBalance());
    }

    @Override
    public TransactionResponseModel withdraw(WithdrawRequestModel request) {
        Account account = accountRepository
                .findByCardNumberAndCvv(request.getCard_number(), request.getCvv())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        Long transactionId = performWithdrawal(account, request.getAmount());

        // Email Notification
        String subject = "Withdrawal Successful";
        String body = "You withdrew $" + request.getAmount();
        notificationService.sendTransactionEmail("demo@inbox.mailtrap.io", subject, body);

        return transactionMapper.toResponseModel(transactionId, request.getAmount(), account.getBalance());
    }

    private Long performDeposit(Account account, double amount) {
        updateAccountBalance(account, amount);
        Transaction transaction = transactionRepository.save(transactionMapper.toEntity(amount, account, TransactionType.DEPOSIT));
        return transaction.getId();
    }

    private Long performWithdrawal(Account account, double amount) {
        if (account.getBalance() < amount) {
            throw new LowBalanceException("Your Balance " + account.getBalance() + " is not enough to withdraw " + amount);
        }

        updateAccountBalance(account, -amount);
        Transaction transaction = transactionRepository.save(transactionMapper.toEntity(amount, account, TransactionType.WITHDRAW));
        return transaction.getId();
    }

    private void updateAccountBalance(Account account, double amount) {
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
    }
}