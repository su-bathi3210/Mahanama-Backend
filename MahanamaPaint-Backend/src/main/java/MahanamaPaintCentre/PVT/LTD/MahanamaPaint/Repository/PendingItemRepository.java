package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.PendingItemRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PendingItemRepository extends MongoRepository<PendingItemRequest, String> {
    List<PendingItemRequest> findByBranchUsername(String branchUsername);
    List<PendingItemRequest> findByBranchUsernameAndStatus(String branchUsername, String status);
}