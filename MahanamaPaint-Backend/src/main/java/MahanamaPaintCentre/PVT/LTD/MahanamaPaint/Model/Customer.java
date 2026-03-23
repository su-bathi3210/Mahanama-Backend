package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Setter
@Getter
@Document(collection = "Customers")
public class Customer {
    @Id
    private String id;
    private String name;
    private String phoneNumber;
    private String address;
    private String nic;
    private String customerType;
    private String paymentMethod;
    private String branchName;
    private LocalDateTime registeredAt;
}