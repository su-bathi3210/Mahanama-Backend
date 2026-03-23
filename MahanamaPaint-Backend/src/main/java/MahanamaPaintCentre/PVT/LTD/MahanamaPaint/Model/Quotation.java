package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "Quotations")
public class Quotation {
    @Id
    private String id;
    private String quoteNumber;
    private String customerName;
    private String customerAddress;
    private String customerEmail;
    private String customerPhone;

    private List<QuotationItem> items;

    private double subtotal;
    private double fullBillDiscount;
    private double vatAmount;
    private double totalAmount;

    private String discountType;
    private String status;

    private Date issuedDate;
    private Date dueDate;
    private String createdByBranch;
    private String notes;

    @Data
    public static class QuotationItem {
        private String code;
        private String productName;
        private String company;
        private String category;
        private String size;
        private int quantity;
        private double unitPrice;
        private double itemDiscount;
        private double lineTotal;
    }
}