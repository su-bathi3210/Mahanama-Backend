package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Controller;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.DTO.ItemDTO;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.*;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.*;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service.ExcelUploadService;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ExcelUploadService excelService;

    @Autowired
    private PendingItemRepository pendingRepo;

    @Autowired
    private BranchRepository branchRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private RestockRepository restockRepository;

    @Autowired
    private HardwareRepository hardwareRepository;

    @Autowired
    private ProductService productService;

    @PostMapping("/upload-excel")
    @PreAuthorize("hasRole('BRANCH')")
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            excelService.processExcelUpload(file, principal.getName());
            return ResponseEntity.ok("File uploaded for admin approval!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/hardware/upload-excel")
    @PreAuthorize("hasRole('BRANCH')")
    public ResponseEntity<?> uploadHardwareExcel(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            excelService.processHardwareUpload(file, principal.getName());
            return ResponseEntity.ok("Hardware file uploaded for admin approval!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/hardware/upload-to-store/{requestId}")
    @PreAuthorize("hasRole('BRANCH')")
    public ResponseEntity<?> uploadHardwareToStore(@PathVariable String requestId) {
        try {
            PendingItemRequest request = pendingRepo.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("Request not found"));

            if (!"APPROVED".equals(request.getStatus())) {
                return ResponseEntity.badRequest().body("Request not approved yet!");
            }

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> hardwareDataList = mapper.readValue(
                    request.getItemDataJson(), new TypeReference<List<Map<String, Object>>>(){}
            );

            String branchName = request.getBranchUsername();

            for (Map<String, Object> data : hardwareDataList) {
                String productName = String.valueOf(data.getOrDefault("productName", ""));

                HardwareItem existing = hardwareRepository.findByProductNameAndBranchName(productName, branchName);

                if (existing != null) {
                    int addQty = parseInteger(data.get("quantity"));
                    existing.setQuantity(existing.getQuantity() + addQty);
                    hardwareRepository.save(existing);
                } else {
                    HardwareItem newItem = new HardwareItem();
                    newItem.setCompany(String.valueOf(data.getOrDefault("company", "")));
                    newItem.setProductName(String.valueOf(data.getOrDefault("productName", "")));
                    newItem.setCategory(String.valueOf(data.getOrDefault("category", "")));
                    newItem.setSize(String.valueOf(data.getOrDefault("size", "")));
                    newItem.setQuantity(parseInteger(data.get("quantity")));
                    newItem.setGross(parseDouble(data.get("gross")));
                    newItem.setMrp(parseDouble(data.get("mrp")));
                    newItem.setDiscount(parseDouble(data.get("discount")));
                    newItem.setMaxDiscount(parseDouble(data.get("maxDiscount")));
                    newItem.setBranchName(branchName);
                    hardwareRepository.save(newItem);
                }
            }

            request.setStatus("COMPLETED");
            pendingRepo.save(request);
            return ResponseEntity.ok("Hardware successfully added to store!");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/branch/{branchName}")
    @PreAuthorize("hasRole('BRANCH') or hasRole('ADMIN')")
    public ResponseEntity<List<Product>> getProductsByBranch(@PathVariable String branchName) {
        try {
            List<Product> products = productRepo.findByBranchName(branchName);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/hardware/branch/{branchName}")
    @PreAuthorize("hasRole('BRANCH') or hasRole('ADMIN')")
    public ResponseEntity<List<HardwareItem>> getHardwareByBranch(@PathVariable String branchName) {
        try {
            List<HardwareItem> hardwareItems = hardwareRepository.findByBranchName(branchName);
            return ResponseEntity.ok(hardwareItems);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/my-status")
    @PreAuthorize("hasRole('BRANCH')")
    public List<PendingItemRequest> getMyBranchRequests(Principal principal) {
        return pendingRepo.findByBranchUsername(principal.getName());
    }

    @GetMapping("/my-approved-requests")
    @PreAuthorize("hasRole('BRANCH')")
    public ResponseEntity<List<PendingItemRequest>> getMyApprovedRequests(Authentication auth) {
        String branchUsername = auth.getName();
        return ResponseEntity.ok(pendingRepo.findByBranchUsernameAndStatus(branchUsername, "APPROVED"));
    }

    @GetMapping("/pending-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPendingStats() {
        List<Branch> allBranches = branchRepo.findAll();
        List<Map<String, Object>> stats = new ArrayList<>();
        for (Branch branch : allBranches) {
            long count = pendingRepo.findByBranchUsername(branch.getUsername())
                    .stream()
                    .filter(r -> "PENDING".equals(r.getStatus()))
                    .count();
            Map<String, Object> branchData = new HashMap<>();
            branchData.put("branchName", branch.getLocation());
            branchData.put("count", count);
            stats.add(branchData);
        }
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/pending-by-branch/{location}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PendingItemRequest>> getPendingByBranch(@PathVariable String location) {
        Optional<Branch> branchOpt = branchRepo.findByLocation(location);
        if (branchOpt.isPresent()) {
            String fullUsername = branchOpt.get().getUsername();
            List<PendingItemRequest> pendingOnly = pendingRepo.findByBranchUsername(fullUsername)
                    .stream()
                    .filter(r -> "PENDING".equals(r.getStatus()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(pendingOnly);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveRequest(@PathVariable String id, @RequestBody List<Map<String, Object>> updatedItems) {
        try {
            PendingItemRequest request = pendingRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Request not found"));
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(updatedItems);
            request.setItemDataJson(json);
            request.setStatus("APPROVED");
            request.setItemCount(updatedItems.size());
            pendingRepo.save(request);
            return ResponseEntity.ok("Request Approved and Status Updated!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/upload-to-store/{requestId}")
    @PreAuthorize("hasRole('BRANCH')")
    public ResponseEntity<?> uploadToStore(@PathVariable String requestId) {
        try {
            PendingItemRequest request = pendingRepo.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("Request not found with ID: " + requestId));

            if (!"APPROVED".equals(request.getStatus())) {
                return ResponseEntity.badRequest().body("Request is not approved yet!");
            }

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> productDataList = mapper.readValue(
                    request.getItemDataJson(),
                    new TypeReference<List<Map<String, Object>>>(){}
            );

            String branchUsername = request.getBranchUsername();

            for (Map<String, Object> data : productDataList) {
                String productCode = String.valueOf(data.getOrDefault("code", ""));

                Optional<Product> existingProductOpt = productRepo.findByCodeAndBranchName(productCode, branchUsername);

                if (existingProductOpt.isPresent()) {
                    Product existingProduct = existingProductOpt.get();
                    int additionalQuantity = parseInteger(data.get("quantity"));

                    existingProduct.setQuantity(existingProduct.getQuantity() + additionalQuantity);

                    existingProduct.setGross(parseDouble(data.get("gross")));
                    existingProduct.setMrp(parseDouble(data.get("mrp")));
                    existingProduct.setDiscount(parseDouble(data.get("discount")));
                    existingProduct.setMaxDiscount(parseDouble(data.get("maxDiscount")));

                    productRepo.save(existingProduct);
                } else {
                    Product p = new Product();
                    p.setCode(productCode);
                    p.setProductName(String.valueOf(data.getOrDefault("productName", "")));
                    p.setCompany(String.valueOf(data.getOrDefault("company", "")));
                    p.setCategory(String.valueOf(data.getOrDefault("category", "")));
                    p.setSize(String.valueOf(data.getOrDefault("size", "")));
                    p.setQuantity(parseInteger(data.get("quantity")));
                    p.setGross(parseDouble(data.get("gross")));
                    p.setMrp(parseDouble(data.get("mrp")));
                    p.setDiscount(parseDouble(data.get("discount")));
                    p.setMaxDiscount(parseDouble(data.get("maxDiscount")));
                    p.setBranchName(branchUsername);

                    productRepo.save(p);
                }
            }

            request.setStatus("COMPLETED");
            pendingRepo.save(request);

            return ResponseEntity.ok("Successfully updated store quantities and products!");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing store upload: " + e.getMessage());
        }
    }

    private double parseDouble(Object val) {
        try {
            if (val == null) return 0.0;
            return Double.parseDouble(val.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int parseInteger(Object val) {
        try {
            if (val == null) return 0;
            return (int) Double.parseDouble(val.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    @DeleteMapping("/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectRequest(@PathVariable String id) {
        pendingRepo.deleteById(id);
        return ResponseEntity.ok("Request Rejected Successfully");
    }

    @PostMapping("/add-manual")
    @PreAuthorize("hasRole('BRANCH')")
    public ResponseEntity<?> addManualProducts(@RequestBody List<ItemDTO> items, Principal principal) {
        try {
            if (items == null || items.isEmpty()) {
                return ResponseEntity.badRequest().body("Product list is empty!");
            }

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(items);

            PendingItemRequest request = new PendingItemRequest();
            request.setBranchUsername(principal.getName());
            request.setItemDataJson(json);
            request.setFileName("MANUAL_ENTRY");
            request.setItemCount(items.size());
            request.setStatus("PENDING");
            request.setRequestedAt(LocalDateTime.now());

            pendingRepo.save(request);

            return ResponseEntity.ok("Manual products submitted for admin approval!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/status-by-branch/{location}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getStatusByBranch(@PathVariable String location) {
        try {
            Optional<Branch> branchOpt = branchRepo.findByLocation(location);

            if (branchOpt.isPresent()) {
                String fullUsername = branchOpt.get().getUsername();

                List<PendingItemRequest> history = pendingRepo.findByBranchUsername(fullUsername)
                        .stream()
                        .filter(r -> !"PENDING".equals(r.getStatus()))
                        .collect(Collectors.toList());

                return ResponseEntity.ok(history);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/request-restock")
    @PreAuthorize("hasRole('BRANCH')")
    public ResponseEntity<?> requestRestock(@RequestBody RestockRequest restockRequest) {
        try {
            restockRequest.setStatus("PENDING");
            restockRequest.setRequestedAt(LocalDateTime.now());
            restockRepository.save(restockRequest);
            return ResponseEntity.ok("Restock request submitted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/requests/branch/{branchName}")
    @PreAuthorize("hasRole('BRANCH') or hasRole('ADMIN')")
    public ResponseEntity<List<RestockRequest>> getBranchRequests(@PathVariable String branchName) {
        try {
            List<RestockRequest> requests = restockRepository.findAll().stream()
                    .filter(r -> r.getBranchName().toUpperCase().contains(branchName.toUpperCase()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/all-restock-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RestockRequest> getAllRestockRequests() {
        return restockRepository.findAll();
    }

    @PostMapping("/admin/approve-restock/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminApproveRestock(@PathVariable String id, @RequestBody Map<String, Object> updateData) {
        try {
            RestockRequest request = restockRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Request not found"));

            if (updateData.containsKey("approvedQuantity")) {
                request.setRequestedQuantity(Integer.parseInt(updateData.get("approvedQuantity").toString()));
            }

            request.setStatus("APPROVED");
            restockRepository.save(request);

            return ResponseEntity.ok("Request Approved by Admin!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/finalize-restock")
    @PreAuthorize("hasRole('BRANCH')")
    public ResponseEntity<?> finalizeRestock(@RequestBody Map<String, Object> payload) {
        try {
            String branchName = payload.get("branchName").toString();
            int addQuantity = Integer.parseInt(payload.get("addQuantity").toString());
            String type = payload.getOrDefault("type", "PRODUCT").toString();

            if ("HARDWARE".equalsIgnoreCase(type)) {
                String productName = payload.get("productName").toString();
                HardwareItem hardware = hardwareRepository.findByProductNameAndBranchName(productName, branchName);

                if (hardware != null) {
                    hardware.setQuantity(hardware.getQuantity() + addQuantity);
                    hardwareRepository.save(hardware);

                    updateRestockStatus(branchName, productName, "HARDWARE");
                    return ResponseEntity.ok("Hardware Store Updated Successfully!");
                } else {
                    return ResponseEntity.status(404).body("Hardware Item not found in this branch");
                }

            } else {
                String productCode = payload.get("productCode").toString();
                Optional<Product> productOpt = productRepo.findByCodeAndBranchName(productCode, branchName);

                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    product.setQuantity(product.getQuantity() + addQuantity);
                    productRepo.save(product);

                    updateRestockStatus(branchName, productCode, "PRODUCT");
                    return ResponseEntity.ok("Product Store Updated Successfully!");
                } else {
                    return ResponseEntity.status(404).body("Product not found in this branch");
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    private void updateRestockStatus(String branchName, String identifier, String type) {
        List<RestockRequest> requests = restockRepository.findByBranchName(branchName);
        for (RestockRequest req : requests) {
            boolean match = false;
            if ("HARDWARE".equalsIgnoreCase(type)) {
                match = identifier.equals(req.getProductName()) && "APPROVED".equals(req.getStatus());
            } else {
                match = identifier.equals(req.getProductCode()) && "APPROVED".equals(req.getStatus());
            }

            if (match) {
                req.setStatus("COMPLETED");
                restockRepository.save(req);
            }
        }
    }

    @DeleteMapping("/reject-restock/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectRestockRequest(@PathVariable String id) {
        try {
            restockRepository.deleteById(id);
            return ResponseEntity.ok("Restock Request Rejected Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/check-stock-generic/{identifier}/{branchName}")
    @PreAuthorize("hasRole('BRANCH') or hasRole('ADMIN')")
    public ResponseEntity<Double> getExistingStock(@PathVariable String identifier, @PathVariable String branchName) {

        Optional<Product> product = productRepo.findByCodeAndBranchName(identifier, branchName);
        if (product.isPresent()) {

            return ResponseEntity.ok((double) product.get().getQuantity());
        }

        HardwareItem hardware = hardwareRepository.findByProductNameAndBranchName(identifier, branchName);
        if (hardware != null) {
            return ResponseEntity.ok((double) hardware.getQuantity());
        }

        return ResponseEntity.ok(0.0);
    }

    @GetMapping("/next-code/{company}")
    @PreAuthorize("hasRole('BRANCH') or hasRole('ADMIN')")
    public String getNextProductCode(@PathVariable String company, Principal principal) {
        if (company == null || company.length() < 3) {
            return "GEN0001";
        }

        String branchName = principal.getName();
        String prefix = company.substring(0, 3).toUpperCase();

        String lastCode = productService.getLastCodeByBranchAndPrefix(branchName, prefix);

        return productService.generateNextCodeFrom(prefix, lastCode);
    }
}