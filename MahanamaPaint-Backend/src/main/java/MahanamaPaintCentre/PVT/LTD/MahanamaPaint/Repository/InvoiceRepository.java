package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    Optional<Invoice> findFirstByBranchNameOrderByCreatedAtDesc(String branchName);
    List<Invoice> findByBranchNameOrderByCreatedAtDesc(String branchName);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByBranchName(String branchName);
}
