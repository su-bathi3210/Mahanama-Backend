package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "SalesReturns")
public class SalesReturn {
    @Id
    private String id;
    private String billNumber;
    private String branchName;
    private String returnType;
    private List<ReturnItem> returnedItems;
    private double totalRefundAmount;
    private LocalDateTime returnedAt;
    private boolean stockUpdated = false;
    private boolean isUsed = false;

    @Data
    public static class ReturnItem {
        private int itemIndex;
        private String code;
        private String productName;
        private String company;
        private String category;
        private String size;
        private int quantity;
        private double refundPrice;
    }
}