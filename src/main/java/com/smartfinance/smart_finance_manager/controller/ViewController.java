package com.smartfinance.smart_finance_manager.controller;

import com.smartfinance.smart_finance_manager.dto.DashboardChartDTO;
import com.smartfinance.smart_finance_manager.model.Transaction;
import com.smartfinance.smart_finance_manager.model.TransactionType;
import com.smartfinance.smart_finance_manager.model.User;
import com.smartfinance.smart_finance_manager.service.TransactionService;
import com.smartfinance.smart_finance_manager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class ViewController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;


    @GetMapping("/")
    public String showHomePage() {
        return "home";
    }


    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }


    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("user") User user,
                                 BindingResult result,
                                 @RequestParam("photoFile") MultipartFile photoFile,
                                 Model model) throws IOException {

        if (result.hasErrors()) {
            return "register";
        }

        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Email already registered. Please use a different one.");
            return "register";
        }

        String photoPath = "uploads/default-user.png";
        if (!photoFile.isEmpty()) {
            String uploadDir = new File("uploads").getAbsolutePath();
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID() + "_" + photoFile.getOriginalFilename();
            File dest = new File(dir, fileName);
            photoFile.transferTo(dest);

            photoPath = "uploads/" + fileName;
        }

        user.setProfilePhoto(photoPath);
        userService.registerUser(user);

        return "redirect:/login";
    }



    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }


    @GetMapping("/profile")
    public String showProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        model.addAttribute("user", currentUser);
        return "profile";
    }


    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") User updatedUser,
                                @RequestParam("photoFile") MultipartFile photoFile,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) throws IOException {

        User currentUser = userService.getUserByEmail(userDetails.getUsername());

        currentUser.setName(updatedUser.getName());
        currentUser.setPhoneNumber(updatedUser.getPhoneNumber());
        currentUser.setAddress(updatedUser.getAddress());

        if (!photoFile.isEmpty()) {
            String uploadDir = new File("uploads").getAbsolutePath();
            new File(uploadDir).mkdirs();

            String fileName = UUID.randomUUID() + "_" + photoFile.getOriginalFilename();
            File dest = new File(uploadDir, fileName);
            photoFile.transferTo(dest);

            currentUser.setProfilePhoto("uploads/" + fileName);
        }

        userService.update(currentUser);

        model.addAttribute("user", currentUser);
        model.addAttribute("success", "Profile updated successfully.");

        return "profile";
    }

    @GetMapping("/income")
    public String showIncomePage(Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("user", user);
        return "income";
    }

    @GetMapping("/expense")
    public String showExpensePage(Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("user", user);
        return "expense";
    }

    @GetMapping("/export")
    public String exportPage(Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("user", user);
        return "export";
    }

    @GetMapping("/debt")
    public String showDebtPage(Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("user", user);
        return "debt";
    }


    @GetMapping("/dashboard")
    public String dashboardPage(Model model, Principal principal) {
        String username = principal.getName();

        User user = userService.getUserByEmail(username);
        model.addAttribute("user", user);

        List<Transaction> transactions = transactionService.getAll(user);
        model.addAttribute("transactions", transactions);

        double income = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double expense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double balance = income - expense;

        model.addAttribute("income", income);
        model.addAttribute("expense", expense);
        model.addAttribute("balance", balance);

        DashboardChartDTO chartData = transactionService.getMonthlyChartData(user.getId());
        model.addAttribute("chartData", chartData);
        return "dashboard";
    }

    @GetMapping("/api/dashboard/monthly-chart")
    @ResponseBody
    public DashboardChartDTO getMonthlyChartData(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        return transactionService.getMonthlyChartData(user.getId());
    }

}

