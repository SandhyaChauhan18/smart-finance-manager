package com.smartfinance.smart_finance_manager.service;

import com.smartfinance.smart_finance_manager.dto.DashboardChartDTO;
import com.smartfinance.smart_finance_manager.model.Transaction;
import com.smartfinance.smart_finance_manager.model.TransactionType;
import com.smartfinance.smart_finance_manager.model.User;
import com.smartfinance.smart_finance_manager.repository.TransactionRepository;
import com.smartfinance.smart_finance_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAll(User user) {
        return transactionRepository.findByUser(user);
    }

    public Transaction update(Long id, Transaction updatedTransaction) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setTitle(updatedTransaction.getTitle());
        transaction.setAmount(updatedTransaction.getAmount());
        transaction.setType(updatedTransaction.getType());
        transaction.setDate(updatedTransaction.getDate());
        transaction.setCategory(updatedTransaction.getCategory());

        return transactionRepository.save(transaction);
    }

    public void delete(Long id) {
        transactionRepository.deleteById(id);
    }

    public DashboardChartDTO getMonthlyChartData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions = transactionRepository.findByUser(user);

        Map<Month, Double> incomeMap = new TreeMap<>();
        Map<Month, Double> expenseMap = new TreeMap<>();

        for (Transaction txn : transactions) {
            Month month = txn.getDate().getMonth();

            incomeMap.putIfAbsent(month, 0.0);
            expenseMap.putIfAbsent(month, 0.0);

            if (txn.getType() == TransactionType.INCOME) {
                incomeMap.put(month, incomeMap.get(month) + txn.getAmount());
            } else if (txn.getType() == TransactionType.EXPENSE) {
                expenseMap.put(month, expenseMap.get(month) + txn.getAmount());
            }
        }

        List<String> months = new ArrayList<>();
        List<Double> income = new ArrayList<>();
        List<Double> expense = new ArrayList<>();

        for (Month m : Month.values()) {
            months.add(m.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));

            income.add(incomeMap.getOrDefault(m, 0.0));
            expense.add(expenseMap.getOrDefault(m, 0.0));
        }

        return new DashboardChartDTO(months, income, expense);
    }

    public List<Transaction> getByMonthAndYear(User user, int year, int month) {
        return transactionRepository.findByUserAndMonthAndYear(user, year, month);
    }

    public List<Transaction> getByYear(User user, int year) {
        return transactionRepository.findByUserAndYear(user, year);
    }


}
