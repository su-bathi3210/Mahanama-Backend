package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Controller;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Bill;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.SalesReturn;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.BillRepository;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.SalesReturnRepository;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service.ReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/returns")
public class ReturnController {

    @Autowired
    private SalesReturnRepository returnRepository;

    @Autowired
    private ReturnService returnService;

    @PostMapping("/process")
    public ResponseEntity<?> processReturn(@RequestBody SalesReturn salesReturn) {
        try {
            SalesReturn savedReturn = returnService.processReturn(salesReturn);
            return ResponseEntity.ok(savedReturn);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllReturns() {
        try {
            List<SalesReturn> returns = returnRepository.findAllByOrderByReturnedAtDesc();
            return ResponseEntity.ok(returns);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching returns: " + e.getMessage());
        }
    }

    @GetMapping("/count/{branchName}")
    public ResponseEntity<Map<String, Long>> getReturnCount(@PathVariable String branchName) {
        long count = returnService.getCountByBranch(branchName);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveReturn(@PathVariable String id) {
        try {
            returnService.approveReturn(id);
            return ResponseEntity.ok(Map.of("message", "Store Quantity Updated Successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/branch/{branchName}/pending")
    public List<SalesReturn> getPendingReturns(@PathVariable String branchName) {
        return returnService.getPendingReturnsByBranch(branchName);
    }

    @GetMapping("/count/pending/{branchName}")
    public ResponseEntity<Map<String, Long>> getPendingReturnCount(@PathVariable String branchName) {
        long count = returnService.getPendingCountByBranch(branchName);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getReturnsByIds(@RequestParam List<String> ids) {
        try {
            List<SalesReturn> returns = returnRepository.findAllById(ids);
            return ResponseEntity.ok(returns);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching specific returns: " + e.getMessage());
        }
    }
}