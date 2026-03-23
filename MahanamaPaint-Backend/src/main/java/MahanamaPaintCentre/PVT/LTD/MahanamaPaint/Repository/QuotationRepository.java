package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Quotation;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface QuotationRepository extends MongoRepository<Quotation, String> {
    Optional<Quotation> findByQuoteNumber(String quoteNumber);
    List<Quotation> findByCreatedByBranch(String branchName);
}