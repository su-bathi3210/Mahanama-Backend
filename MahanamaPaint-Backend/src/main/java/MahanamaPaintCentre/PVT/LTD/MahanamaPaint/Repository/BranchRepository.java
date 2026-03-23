package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Branch;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface BranchRepository extends MongoRepository<Branch, String> {
    Optional<Branch> findByUsername(String username);
    Optional<Branch> findByLocation(String location);
}