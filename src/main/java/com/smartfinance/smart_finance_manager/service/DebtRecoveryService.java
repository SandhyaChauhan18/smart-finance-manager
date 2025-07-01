package com.smartfinance.smart_finance_manager.service;

import com.smartfinance.smart_finance_manager.dto.DebtRecoveryDTO;
import com.smartfinance.smart_finance_manager.model.DebtRecovery;
import com.smartfinance.smart_finance_manager.model.User;
import com.smartfinance.smart_finance_manager.repository.DebtRecoveryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DebtRecoveryService {

    private final DebtRecoveryRepository repository;

    public DebtRecoveryService(DebtRecoveryRepository repository) {
        this.repository = repository;
    }

    private DebtRecoveryDTO toDTO(DebtRecovery d) {
        return new DebtRecoveryDTO(
                d.getId(),
                d.getBorrowerName(),
                d.getAmount(),
                d.getDateLent(),
                d.getDescription(),
                d.getStatus().name()
        );
    }

    public DebtRecoveryDTO addDebt(DebtRecoveryDTO dto, User user) {
        DebtRecovery debt = new DebtRecovery();
        debt.setUser(user);
        debt.setBorrowerName(dto.getBorrowerName());
        debt.setAmount(dto.getAmount());
        debt.setDateLent(dto.getDateLent());
        debt.setDescription(dto.getDescription());
        debt.setStatus(DebtRecovery.Status.PENDING);

        return toDTO(repository.save(debt));
    }

    public List<DebtRecoveryDTO> getAllDebts(User user) {
        List<DebtRecoveryDTO> result = new ArrayList<>();
        for (DebtRecovery debt : repository.findByUser(user)) {
            result.add(toDTO(debt));
        }
        return result;
    }

    public DebtRecoveryDTO updateDebt(Long id, DebtRecoveryDTO dto, User user) {
        DebtRecovery debt = repository.findById(id)
                .filter(d -> d.getUser().equals(user))
                .orElseThrow(() -> new NoSuchElementException("Debt not found"));

        debt.setBorrowerName(dto.getBorrowerName());
        debt.setAmount(dto.getAmount());
        debt.setDateLent(dto.getDateLent());
        debt.setDescription(dto.getDescription());

        return toDTO(repository.save(debt));
    }

    public void deleteDebt(Long id, User user) {
        DebtRecovery debt = repository.findById(id)
                .filter(d -> d.getUser().equals(user))
                .orElseThrow(() -> new NoSuchElementException("Debt not found"));
        repository.delete(debt);
    }

    public DebtRecoveryDTO markAsPaid(Long id, User user) {
        DebtRecovery debt = repository.findById(id)
                .filter(d -> d.getUser().equals(user))
                .orElseThrow(() -> new NoSuchElementException("Debt not found"));

        debt.setStatus(DebtRecovery.Status.PAID);
        return toDTO(repository.save(debt));
    }
}

