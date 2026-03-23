package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Controller;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Bill;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('BRANCH')")
    public ResponseEntity<?> createBill(@RequestBody Bill billRequest) {
        try {
            Bill savedBill = billService.createBill(billRequest);
            return ResponseEntity.ok(savedBill);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating bill: " + e.getMessage());
        }
    }

    @GetMapping("/search/{billNumber}")
    @PreAuthorize("hasAnyRole('BRANCH', 'ADMIN')")
    public ResponseEntity<?> getBillByNumber(@PathVariable String billNumber) {
        try {
            Bill bill = billService.getBillByNumber(billNumber);
            if (bill != null) {
                return ResponseEntity.ok(bill);
            } else {
                return ResponseEntity.status(404).body("Bill not found with number: " + billNumber);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error searching bill: " + e.getMessage());
        }
    }

    @GetMapping("/branch/{branchName}")
    public ResponseEntity<?> getBillsByBranch(@PathVariable String branchName) {
        try {
            return ResponseEntity.ok(billService.getBillsByBranchName(branchName));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching bills: " + e.getMessage());
        }
    }

    @GetMapping("/today-stats/{branchName}")
    public ResponseEntity<?> getTodayPaymentStats(@PathVariable String branchName) {
        try {
            return ResponseEntity.ok(billService.getTodayPaymentStats(branchName));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching stats: " + e.getMessage());
        }
    }
}