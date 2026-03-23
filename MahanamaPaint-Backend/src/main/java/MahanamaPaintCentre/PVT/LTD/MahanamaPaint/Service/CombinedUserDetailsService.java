package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Branch;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CombinedUserDetailsService implements UserDetailsService {

    @Autowired
    private BranchRepository branchRepository;

    public static final String ADMIN_USERNAME = "MAHANAMA PAINT CENTRE (PVT) LTD";
    public static final String ADMIN_PASSWORD = "@9719";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (ADMIN_USERNAME.equalsIgnoreCase(username)) {
            return new User(ADMIN_USERNAME, "{noop}" + ADMIN_PASSWORD, new ArrayList<>());
        }

        Optional<Branch> branch = branchRepository.findByUsername(username);
        if (branch.isPresent()) {
            return new User(branch.get().getUsername(), "{noop}" + branch.get().getPassword(), new ArrayList<>());
        }
        throw new UsernameNotFoundException("User or Branch not found: " + username);
    }
}