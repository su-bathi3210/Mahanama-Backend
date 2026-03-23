package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "Expenses")
public class Expense {
    @Id
    private String id;
    private String branchName;
    private LocalDateTime createdAt;
    private double dailyTotal;
    private List<ExpenseItem> items;

    @Data
    public static class ExpenseItem {
        private String description;
        private double amount;
        private LocalDateTime time;
    }
}