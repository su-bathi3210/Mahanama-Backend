package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Bill;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.HardwareItem;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.SalesReturn;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.BillRepository;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.HardwareRepository;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.ProductRepository;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.SalesReturnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReturnService {

    @Autowired
    private SalesReturnRepository returnRepo;

    @Autowired
    private BillRepository billRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private HardwareRepository hardwareRepository;

    @Transactional
    public SalesReturn processReturn(SalesReturn returnRequest) {
        String docNo = returnRequest.getBillNumber();
        long existingReturnsCount = returnRepo.countByBillNumber(docNo);
        String newReturnId = docNo + "-R" + (existingReturnsCount + 1);

        returnRequest.setId(newReturnId);
        returnRequest.setReturnedAt(LocalDateTime.now());
        returnRequest.setStockUpdated(false);

        Optional<Bill> billOptional = billRepo.findByBillNumber(docNo);

        if (billOptional.isPresent()) {
            Bill bill = billOptional.get();
            updateBillReturnQty(bill.getItems(), returnRequest.getReturnedItems());
            billRepo.save(bill);
        } else {
            throw new RuntimeException("Bill with number " + docNo + " not found!");
        }

        return returnRepo.save(returnRequest);
    }

    private void updateBillReturnQty(List<Bill.BillItem> docItems, List<SalesReturn.ReturnItem> returnItems) {
        for (SalesReturn.ReturnItem retItem : returnItems) {
            docItems.stream()
                    .filter(item -> {
                        boolean codeMatch = (item.getCode() != null && item.getCode().equals(retItem.getCode()));
                        boolean nameMatch = (item.getProductName() != null && item.getProductName().equals(retItem.getProductName()));
                        return codeMatch || nameMatch;
                    })
                    .findFirst()
                    .ifPresent(item -> item.setAlreadyReturnedQty(item.getAlreadyReturnedQty() + retItem.getQuantity()));
        }
    }

    @Transactional
    public void approveReturn(String returnId) {
        SalesReturn salesReturn = returnRepo.findById(returnId)
                .orElseThrow(() -> new RuntimeException("Return record not found"));

        if (salesReturn.isStockUpdated()) {
            throw new RuntimeException("This return is already updated in the store!");
        }

        for (SalesReturn.ReturnItem retItem : salesReturn.getReturnedItems()) {
            String itemCode = retItem.getCode();
            String itemName = retItem.getProductName();

            if (itemCode != null && !itemCode.isEmpty()) {
                productRepo.findByCodeAndBranchName(itemCode, salesReturn.getBranchName())
                        .ifPresent(product -> {
                            product.setQuantity(product.getQuantity() + retItem.getQuantity());
                            productRepo.save(product);
                        });
            }

            HardwareItem h = hardwareRepository.findByProductNameAndBranchName(itemName, salesReturn.getBranchName());

            if (h != null) {
                h.setQuantity(h.getQuantity() + retItem.getQuantity());
                hardwareRepository.save(h);
            }
        }

        salesReturn.setStockUpdated(true);
        returnRepo.save(salesReturn);
    }

    public long getCountByBranch(String branchName) {
        return returnRepo.countByBranchName(branchName);
    }

    public List<SalesReturn> getPendingReturnsByBranch(String branchName) {
        return returnRepo.findByBranchNameAndIsUsedFalse(branchName);
    }

    public long getPendingCountByBranch(String branchName) {
        return returnRepo.countByBranchNameAndStockUpdatedFalse(branchName);
    }
}