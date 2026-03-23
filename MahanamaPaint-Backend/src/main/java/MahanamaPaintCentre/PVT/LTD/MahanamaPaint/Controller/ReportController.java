package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Controller;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/summary/{branchName}")
    public ResponseEntity<?> getSummary(
            @PathVariable String branchName,
            @RequestParam String type,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(reportService.getDashboardReport(branchName, type, year != null ? year : LocalDate.now().getYear()));
    }
}