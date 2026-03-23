package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Controller;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Invoice;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/create")
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        return ResponseEntity.ok(invoiceService.createInvoice(invoice));
    }

    @GetMapping("/branch/{branchName}")
    public ResponseEntity<List<Invoice>> getInvoices(@PathVariable String branchName) {
        return ResponseEntity.ok(invoiceService.getInvoicesByBranch(branchName));
    }

    @PutMapping("/update-status/{invoiceNumber}")
    public ResponseEntity<?> updateStatus(@PathVariable String invoiceNumber) {
        invoiceService.markAsComplete(invoiceNumber);
        return ResponseEntity.ok("Invoice status updated to COMPLETE");
    }

    @GetMapping("/incomplete/{branchName}")
    public ResponseEntity<List<Map<String, Object>>> getIncomplete(@PathVariable String branchName) {
        return ResponseEntity.ok(invoiceService.getIncompleteInvoices(branchName));
    }
}