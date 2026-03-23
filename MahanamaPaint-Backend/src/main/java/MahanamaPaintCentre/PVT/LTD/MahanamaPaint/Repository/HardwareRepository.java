package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.HardwareItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface HardwareRepository extends MongoRepository<HardwareItem, String> {
    HardwareItem findByProductNameAndBranchName(String productName, String branchName);
    List<HardwareItem> findByBranchName(String branchName);
}