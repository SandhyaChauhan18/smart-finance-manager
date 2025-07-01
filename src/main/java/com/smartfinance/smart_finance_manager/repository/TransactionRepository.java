package com.smartfinance.smart_finance_manager.repository;

import com.smartfinance.smart_finance_manager.model.Transaction;
import com.smartfinance.smart_finance_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser(User user);


    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND YEAR(t.date) = :year AND MONTH(t.date) = :month")
    List<Transaction> findByUserAndMonthAndYear(@Param("user") User user,
                                                @Param("year") int year,
                                                @Param("month") int month);

    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND YEAR(t.date) = :year")
    List<Transaction> findByUserAndYear(@Param("user") User user,
                                        @Param("year") int year);

}

