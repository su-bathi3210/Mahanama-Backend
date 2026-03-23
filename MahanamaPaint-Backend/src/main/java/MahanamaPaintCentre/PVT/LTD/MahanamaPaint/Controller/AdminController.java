package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Controller;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Branch;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BranchRepository branchRepository;

    @PostMapping("/branches/add")
    public ResponseEntity<Branch> createBranch(@RequestBody Branch branch) {
        Branch savedBranch = branchRepository.save(branch);
        return ResponseEntity.ok(savedBranch);
    }

    @GetMapping("/branches/all")
    public ResponseEntity<List<Branch>> getAllBranches() {
        return ResponseEntity.ok(branchRepository.findAll());
    }

    @GetMapping("/branches/{id}")
    public ResponseEntity<Branch> getBranchById(@PathVariable String id) {
        return branchRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/branches/name/{name}")
    public ResponseEntity<Branch> getBranchByName(@PathVariable String name) {
        return branchRepository.findAll().stream()
                .filter(branch -> branch.getBranchName().equalsIgnoreCase(name))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/branches/update/{id}")
    public ResponseEntity<Branch> updateBranch(@PathVariable String id, @RequestBody Branch branchDetails) {
        return branchRepository.findById(id).map(branch -> {
            branch.setBranchName(branchDetails.getBranchName());
            branch.setAddress(branchDetails.getAddress());
            branch.setLocation(branchDetails.getLocation());
            branch.setUsername(branchDetails.getUsername());
            branch.setPassword(branchDetails.getPassword());
            branch.setPhoneNumbers(branchDetails.getPhoneNumbers());

            Branch updatedBranch = branchRepository.save(branch);
            return ResponseEntity.ok(updatedBranch);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/branches/delete/{id}")
    public ResponseEntity<String> deleteBranch(@PathVariable String id) {
        return branchRepository.findById(id).map(branch -> {
            branchRepository.delete(branch);
            return ResponseEntity.ok("Branch deleted successfully!");
        }).orElse(ResponseEntity.notFound().build());
    }
}