package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.SalesReturn;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesReturnRepository extends MongoRepository<SalesReturn, String> {
    List<SalesReturn> findAllByOrderByReturnedAtDesc();
    long countByBranchName(String branchName);
    long countByBillNumber(String billNumber);
    List<SalesReturn> findByBranchNameAndIsUsedFalse(String branchName);
    long countByBranchNameAndStockUpdatedFalse(String branchName);
}