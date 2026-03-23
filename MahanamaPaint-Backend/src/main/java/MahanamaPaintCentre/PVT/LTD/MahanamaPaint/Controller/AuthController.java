package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Controller;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Config.JwtUtil;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Branch;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.BranchRepository;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service.CombinedUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BranchRepository branchRepository;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Map<String, String> response = new HashMap<>();

        if (CombinedUserDetailsService.ADMIN_USERNAME.equalsIgnoreCase(username) &&
                CombinedUserDetailsService.ADMIN_PASSWORD.equals(password)) {

            String token = jwtUtil.generateToken(username, "ROLE_ADMIN");
            response.put("token", token);
            response.put("username", username);
            response.put("role", "ADMIN");
            return response;
        }

        Optional<Branch> branch = branchRepository.findByUsername(username);
        if (branch.isPresent() && branch.get().getPassword().equals(password)) {
            Branch b = branch.get();
            String token = jwtUtil.generateToken(username, "ROLE_BRANCH");

            response.put("token", token);
            response.put("username", username);
            response.put("role", "BRANCH");

            response.put("branchLocation", b.getLocation());
            response.put("branchAddress", b.getAddress());
            response.put("branchPhones", String.join(", ", b.getPhoneNumbers()));

            return response;
        }

        throw new RuntimeException("Unauthorized Access: Invalid Credentials");
    }

    @GetMapping("/branches/public")
    public List<String> getPublicBranchNames() {
        return branchRepository.findAll().stream()
                .map(Branch::getUsername)
                .toList();
    }
}