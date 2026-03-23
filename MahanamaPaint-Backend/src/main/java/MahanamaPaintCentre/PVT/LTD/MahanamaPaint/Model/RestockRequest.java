package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "RestockRequests")
public class RestockRequest {
    @Id
    private String id;
    private String productCode;
    private String productName;
    private String branchName;
    private String company;
    private String category;
    private String size;
    private double gross;
    private double mrp;
    private double discount;
    private double maxDiscount;
    private int currentQuantity;
    private int requestedQuantity;
    private String status;
    private LocalDateTime requestedAt;
}