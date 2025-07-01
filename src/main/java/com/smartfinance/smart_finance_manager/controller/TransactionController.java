package com.smartfinance.smart_finance_manager.controller;

import com.smartfinance.smart_finance_manager.model.Transaction;
import com.smartfinance.smart_finance_manager.model.User;
import com.smartfinance.smart_finance_manager.service.TransactionService;
import com.smartfinance.smart_finance_manager.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        transaction.setUser(user);
        return ResponseEntity.ok(transactionService.save(transaction));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(transactionService.getAll(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.update(id, transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/monthly")
    public void exportMonthlyCSV(
            @RequestParam int year,
            @RequestParam int month,
            Principal principal,
            HttpServletResponse response) throws IOException {

        User user = userService.getUserByEmail(principal.getName());
        List<Transaction> transactions = transactionService.getByMonthAndYear(user, year, month);

        String fileName = "monthly_report_" + year + "_" + month + ".csv";
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        PrintWriter writer = response.getWriter();
        writer.println("Date,Type,Category,Amount,Title");

        for (Transaction t : transactions) {
            writer.printf("%s,%s,%s,%.2f,%s%n",
                    t.getDate(),
                    t.getType(),
                    t.getCategory(),
                    t.getAmount(),
                    t.getTitle().replace(",", " "));
        }

        writer.flush();
        writer.close();
    }

    @GetMapping("/export/yearly")
    public void exportYearlyCSV(
            @RequestParam int year,
            Principal principal,
            HttpServletResponse response) throws IOException {

        User user = userService.getUserByEmail(principal.getName());
        List<Transaction> transactions = transactionService.getByYear(user, year);

        String fileName = "yearly_report_" + year + ".csv";
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        PrintWriter writer = response.getWriter();
        writer.println("Date,Type,Category,Amount,Title");

        for (Transaction t : transactions) {
            writer.printf("%s,%s,%s,%.2f,%s%n",
                    t.getDate(),
                    t.getType(),
                    t.getCategory(),
                    t.getAmount(),
                    t.getTitle().replace(",", " "));
        }

        writer.flush();
        writer.close();
    }


}

