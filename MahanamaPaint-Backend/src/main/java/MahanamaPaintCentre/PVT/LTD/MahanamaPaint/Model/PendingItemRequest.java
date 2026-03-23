package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "PendingItemRequests")
@Data
public class PendingItemRequest {
    @Id
    private String id;
    private String branchUsername;
    private String status;
    private String fileName;
    private int itemCount;
    private String itemDataJson;
    private LocalDateTime requestedAt;
    private String type;
}