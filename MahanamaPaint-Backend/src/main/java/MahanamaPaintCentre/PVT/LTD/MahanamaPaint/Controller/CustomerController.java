package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Controller;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Customer;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping("/add")
    @PreAuthorize("hasRole('BRANCH') or hasRole('ADMIN')")
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
        try {
            if(customerRepository.findByPhoneNumber(customer.getPhoneNumber()).isPresent()) {
                return ResponseEntity.badRequest().body("Customer with this phone number already exists!");
            }

            customer.setRegisteredAt(LocalDateTime.now());
            Customer savedCustomer = customerRepository.save(customer);
            return ResponseEntity.ok(savedCustomer);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/branch/{branchName}")
    @PreAuthorize("hasRole('BRANCH') or hasRole('ADMIN')")
    public ResponseEntity<List<Customer>> getCustomersByBranch(@PathVariable String branchName) {
        List<Customer> customers = customerRepository.findByBranchName(branchName);
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('BRANCH') or hasRole('ADMIN')")
    public ResponseEntity<?> updateCustomer(@PathVariable String id, @RequestBody Customer customerDetails) {
        return customerRepository.findById(id).map(customer -> {
            customer.setName(customerDetails.getName());
            customer.setPhoneNumber(customerDetails.getPhoneNumber());
            customer.setAddress(customerDetails.getAddress());
            customer.setNic(customerDetails.getNic());
            customer.setPaymentMethod(customerDetails.getPaymentMethod());

            Customer updatedCustomer = customerRepository.save(customer);
            return ResponseEntity.ok(updatedCustomer);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('BRANCH') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id) {
        return customerRepository.findById(id).map(customer -> {
            customerRepository.delete(customer);
            return ResponseEntity.ok("Customer deleted successfully!");
        }).orElse(ResponseEntity.notFound().build());
    }
}