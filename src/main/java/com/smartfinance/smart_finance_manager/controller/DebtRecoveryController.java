package com.smartfinance.smart_finance_manager.controller;

import com.smartfinance.smart_finance_manager.dto.DebtRecoveryDTO;
import com.smartfinance.smart_finance_manager.model.User;
import com.smartfinance.smart_finance_manager.service.DebtRecoveryService;
import com.smartfinance.smart_finance_manager.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/debt")
public class DebtRecoveryController {

    private final DebtRecoveryService service;
    private final UserService userService;

    public DebtRecoveryController(DebtRecoveryService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @PostMapping
    public DebtRecoveryDTO addDebt(@RequestBody DebtRecoveryDTO dto) {
        User currentUser = userService.getCurrentUser();
        return service.addDebt(dto, currentUser);
    }

    @GetMapping
    public List<DebtRecoveryDTO> getAllDebts() {
        User currentUser = userService.getCurrentUser();
        return service.getAllDebts(currentUser);
    }

    @PutMapping("/{id}")
    public DebtRecoveryDTO updateDebt(@PathVariable Long id, @RequestBody DebtRecoveryDTO dto) {
        User currentUser = userService.getCurrentUser();
        return service.updateDebt(id, dto, currentUser);
    }

    @DeleteMapping("/{id}")
    public void deleteDebt(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        service.deleteDebt(id, currentUser);
    }

    @PutMapping("/{id}/paid")
    public DebtRecoveryDTO markAsPaid(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        return service.markAsPaid(id, currentUser);
    }
}
