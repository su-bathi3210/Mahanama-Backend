package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Bill;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Expense;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.BillRepository;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired private BillRepository billRepository;
    @Autowired private ExpenseRepository expenseRepository;

    public Map<String, Object> getDashboardReport(String branchName, String type, Integer year) {
        List<String> labels = new ArrayList<>();
        List<Double> incomeData = new ArrayList<>();
        List<Double> expenseData = new ArrayList<>();

        if ("THIS_MONTH".equals(type)) {
            LocalDate now = LocalDate.now();
            for (int i = 3; i >= 0; i--) {
                labels.add("Week " + (4 - i));
                LocalDate start = now.minusWeeks(i + 1);
                LocalDate end = now.minusWeeks(i);

                incomeData.add(calculateIncome(branchName, start, end));
                expenseData.add(calculateExpense(branchName, start, end));
            }
        } else if ("YEAR".equals(type)) {
            for (int i = 1; i <= 12; i++) {
                labels.add(LocalDate.of(year, i, 1).getMonth().name().substring(0, 3));
                LocalDate start = LocalDate.of(year, i, 1);
                LocalDate end = start.plusMonths(1);

                incomeData.add(calculateIncome(branchName, start, end));
                expenseData.add(calculateExpense(branchName, start, end));
            }
        } else {
            LocalDate now = LocalDate.now();
            for (int i = 5; i >= 0; i--) {
                LocalDate date = now.minusMonths(i);
                labels.add(date.getMonth().name().substring(0, 3));
                LocalDate start = date.withDayOfMonth(1);
                LocalDate end = start.plusMonths(1);

                incomeData.add(calculateIncome(branchName, start, end));
                expenseData.add(calculateExpense(branchName, start, end));
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("incomeData", incomeData);
        response.put("expenseData", expenseData);
        return response;
    }

    private double calculateIncome(String branch, LocalDate start, LocalDate end) {
        return billRepository.findAll().stream()
                .filter(b -> b.getBranchName().equals(branch))
                .filter(b -> !b.getCreatedAt().toLocalDate().isBefore(start) && b.getCreatedAt().toLocalDate().isBefore(end))
                .mapToDouble(Bill::getTotalFinalAmount).sum();
    }

    private double calculateExpense(String branch, LocalDate start, LocalDate end) {
        return expenseRepository.findByBranchNameOrderByCreatedAtDesc(branch).stream()
                .filter(e -> !e.getCreatedAt().toLocalDate().isBefore(start) && e.getCreatedAt().toLocalDate().isBefore(end))
                .mapToDouble(Expense::getDailyTotal).sum();
    }
}