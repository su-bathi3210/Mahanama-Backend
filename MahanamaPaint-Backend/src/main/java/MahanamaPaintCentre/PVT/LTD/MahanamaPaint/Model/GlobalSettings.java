package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "GlobalSettings")
public class GlobalSettings {
    @Id
    private String id;
    private Double vatPercentage;
}