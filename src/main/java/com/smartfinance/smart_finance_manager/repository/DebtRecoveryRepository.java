package com.smartfinance.smart_finance_manager.repository;


import com.smartfinance.smart_finance_manager.model.DebtRecovery;
import com.smartfinance.smart_finance_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebtRecoveryRepository extends JpaRepository<DebtRecovery, Long> {
    List<DebtRecovery> findByUser(User user);
}

