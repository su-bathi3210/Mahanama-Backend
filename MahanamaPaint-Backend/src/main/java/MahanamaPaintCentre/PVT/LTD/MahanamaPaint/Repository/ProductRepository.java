package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByBranchName(String branchName);
    Optional<Product> findByCodeAndBranchName(String code, String branchName);
    Optional<Product> findFirstByBranchNameAndCodeStartingWithOrderByCodeDesc(String branchName, String prefix);
    Optional<Product> findFirstByProductNameAndCompanyAndBranchName(String productName, String company, String branchName);
}