package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.RestockRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestockRepository extends MongoRepository<RestockRequest, String> {
    List<RestockRequest> findByBranchName(String branchName);
    List<RestockRequest> findByBranchNameContaining(String branchName);
}