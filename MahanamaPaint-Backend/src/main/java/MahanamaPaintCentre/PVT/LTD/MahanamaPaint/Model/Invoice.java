package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "Invoices")
@Data
public class Invoice {
    @Id
    private String id;
    private String invoiceNumber;
    private String branchName;
    private String customerName;
    private String customerAddress;
    private String customerContact;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private List<InvoiceItem> items;
    private double subtotal;
    private double discountAmount;
    private double vatAmount;
    private double totalFinalAmount;
    private double advancePayment;
    private String advancePaymentMethod;
    private String advancePaymentDetails;
    private double vatRate;
    private double fullBillDiscountRate;
    private String status;

    @Data
    public static class InvoiceItem {
        private String code;
        private String company;
        private String productName;
        private String category;
        private String size;
        private int quantity;
        private double unitPrice;
        private int alreadyReturnedQty;
        private double productWiseDiscountTotal;
    }
}