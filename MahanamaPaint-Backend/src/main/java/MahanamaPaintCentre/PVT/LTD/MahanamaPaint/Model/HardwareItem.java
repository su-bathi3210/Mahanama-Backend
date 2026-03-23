package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Hardware")
public class HardwareItem {
    @Id
    private String id;
    private String company;
    private String productName;
    private String category;
    private String size;
    private double quantity;
    private double gross;
    private double mrp;
    private double discount;
    private double maxDiscount;

    private String branchName;
}