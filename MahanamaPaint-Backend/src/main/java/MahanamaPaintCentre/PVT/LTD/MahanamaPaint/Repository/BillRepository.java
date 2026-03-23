package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BillRepository extends MongoRepository<Bill, String> {
    Optional<Bill> findFirstByBranchNameOrderByCreatedAtDesc(String branchName);
    Optional<Bill> findByBillNumber(String billNumber);
    List<Bill> findByBranchName(String branchName);
}