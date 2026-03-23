package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Setter
@Getter
@Document(collection = "Branches")
public class Branch {
    @Id
    private String id;
    private String branchName;
    private String address;
    private String location;
    private String username;
    private String password;
    private List<String> phoneNumbers;
    private Double branchDiscountRate = 0.0;
}