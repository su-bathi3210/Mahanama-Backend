package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Controller;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Expense;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @PostMapping("/add")
    @PreAuthorize("hasRole('BRANCH') or hasRole('ADMIN')")
    public ResponseEntity<?> addExpense(@RequestBody Expense.ExpenseItem newItem, @RequestParam String branchName) {
        try {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

            Expense expense = expenseRepository.findByBranchNameAndCreatedAtBetween(branchName, startOfDay, endOfDay)
                    .orElse(null);

            if (expense == null) {
                expense = new Expense();
                expense.setBranchName(branchName);
                expense.setCreatedAt(LocalDateTime.now());
                expense.setItems(new ArrayList<>());
                expense.setDailyTotal(0.0);
            }

            newItem.setTime(LocalDateTime.now());
            expense.getItems().add(newItem);

            expense.setDailyTotal(expense.getDailyTotal() + newItem.getAmount());

            expenseRepository.save(expense);
            return ResponseEntity.ok(expense);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/my-branch/{branchName}")
    @PreAuthorize("hasRole('BRANCH') or hasRole('ADMIN')")
    public ResponseEntity<List<Expense>> getMyBranchExpenses(@PathVariable String branchName) {
        return ResponseEntity.ok(expenseRepository.findByBranchNameOrderByCreatedAtDesc(branchName));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(expenseRepository.findAll());
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteExpense(@PathVariable String id) {
        expenseRepository.deleteById(id);
        return ResponseEntity.ok("Expense deleted successfully!");
    }
}