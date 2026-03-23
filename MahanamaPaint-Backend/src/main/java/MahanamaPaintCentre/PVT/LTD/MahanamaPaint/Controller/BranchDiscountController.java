package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Controller;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Branch;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/branch-settings")
public class BranchDiscountController {

    @Autowired
    private BranchRepository branchRepo;

    @PutMapping("/update-discount")
    @PreAuthorize("hasRole('BRANCH')")
    public ResponseEntity<?> updateDiscount(@RequestBody Map<String, Double> payload, Principal principal) {
        String username = principal.getName();
        Double newDiscount = payload.get("discountRate");

        Optional<Branch> branchOpt = branchRepo.findByUsername(username);

        if (branchOpt.isPresent()) {
            Branch branch = branchOpt.get();
            branch.setBranchDiscountRate(newDiscount);
            branchRepo.save(branch);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Discount Rate Updated Successfully!");
            response.put("newRate", newDiscount);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Branch not found"));
        }
    }

    @GetMapping("/current-discount")
    @PreAuthorize("hasRole('BRANCH')")
    public ResponseEntity<?> getCurrentDiscount(Principal principal) {
        Optional<Branch> branchOpt = branchRepo.findByUsername(principal.getName());

        if (branchOpt.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("discountRate", branchOpt.get().getBranchDiscountRate());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Branch not found"));
        }
    }
}