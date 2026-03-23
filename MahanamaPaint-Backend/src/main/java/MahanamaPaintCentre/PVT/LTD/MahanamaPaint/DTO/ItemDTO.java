package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.DTO;

import lombok.Data;

@Data
public class ItemDTO {
    private String code;
    private String company;
    private String productName;
    private String category;
    private String size;
    private Integer quantity;
    private Double gross;
    private Double mrp;
    private Double discount;
    private Double maxDiscount;
}