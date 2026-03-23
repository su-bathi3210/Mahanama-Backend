package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Controller;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Quotation;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.QuotationRepository;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service.QuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quotations")
public class QuotationController {

    @Autowired
    private QuotationService quotationService;

    @Autowired
    private QuotationRepository quotationRepository;

    @PostMapping("/create")
    public ResponseEntity<Quotation> createQuotation(
            @RequestBody Quotation quote,
            @RequestParam String branchName) {
        return ResponseEntity.ok(quotationService.createQuotation(quote, branchName));
    }

    @GetMapping("/number/{quoteNumber}")
    public ResponseEntity<?> getQuotationByNumber(@PathVariable String quoteNumber) {
        return quotationRepository.findByQuoteNumber(quoteNumber)
                .map(quote -> {
                    if ("PAID QUOTATION".equals(quote.getStatus())) {
                        return ResponseEntity.badRequest()
                                .body("This quotation has already been billed (PAID). It cannot be used again.");
                    }
                    return ResponseEntity.ok((Object) quote);
                })
                .orElse(ResponseEntity.status(404).body("The quotation number is incorrect. Please check again."));
    }

    @GetMapping("/branch/{branchName}")
    public ResponseEntity<List<Quotation>> getQuotationsByBranch(@PathVariable String branchName) {
        return ResponseEntity.ok(quotationRepository.findByCreatedByBranch(branchName));
    }
}