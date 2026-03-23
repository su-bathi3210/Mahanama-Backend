package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "Bills")
public class Bill {
    @Id
    private String id;
    private String billNumber;
    private String branchName;
    private String customerName;
    private List<BillItem> items;
    private double subtotal;
    private double discountAmount;
    private double vatAmount;
    private double productWiseDiscountTotal;
    private double totalFinalAmount;
    private double vatRate;
    private double fullBillDiscountRate;
    private String paymentMethod;
    private double cashPaid;
    private double balance;
    private LocalDateTime createdAt;
    private String status;
    private String quoteNumber;
    private String invoiceNumber;
    private double advancePayment;
    private double returnDeduction = 0.0;
    private List<String> linkedReturnIds;
    private double returnBalance = 0.0;

    @Data
    public static class BillItem {
        private String code;
        private String productName;
        private String company;
        private String category;
        private String size;
        private int quantity;
        private double unitPrice;
        private double discountAmount;
        private double total;

        private int alreadyReturnedQty = 0;
    }
}