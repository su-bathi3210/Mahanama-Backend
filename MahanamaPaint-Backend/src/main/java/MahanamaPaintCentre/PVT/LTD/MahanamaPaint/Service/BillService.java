package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Bill;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.HardwareItem;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Product;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private SalesReturnRepository salesReturnRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private HardwareRepository hardwareRepository;

    @Transactional
    public Bill createBill(Bill billRequest) {
        String branchName = billRequest.getBranchName();

        branchRepository.findByUsername(branchName).ifPresent(branch -> {
            if (billRequest.getFullBillDiscountRate() == 0) {
                billRequest.setFullBillDiscountRate(branch.getBranchDiscountRate());
            }
        });

        String prefix = "B";

        if (branchName != null && branchName.contains("-")) {
            String afterHyphen = branchName.split("-")[1].trim();
            String locationName = afterHyphen.split(" ")[0];
            prefix = locationName.substring(0, 1).toUpperCase();
        }

        int currentYear = LocalDateTime.now().getYear();
        int nextNumber = 1;

        Optional<Bill> lastBill = billRepository.findFirstByBranchNameOrderByCreatedAtDesc(branchName);
        if (lastBill.isPresent()) {
            String lastNo = lastBill.get().getBillNumber();
            try {
                String[] noParts = lastNo.split("-");
                if (noParts.length == 3) {
                    nextNumber = Integer.parseInt(noParts[2]) + 1;
                }
            } catch (Exception e) {
                nextNumber = 1;
            }
        }

        String finalBillNumber = String.format("%s-%d-%03d", prefix, currentYear, nextNumber);

        billRequest.setBillNumber(finalBillNumber);
        billRequest.setCreatedAt(LocalDateTime.now());

        if (billRequest.getStatus() == null || billRequest.getStatus().isEmpty()) {
            billRequest.setStatus("PAID");
        }

        if (billRequest.getQuoteNumber() != null && !billRequest.getQuoteNumber().isEmpty()) {
            String refNumber = billRequest.getQuoteNumber();

            if (refNumber.startsWith("INV-")) {
                invoiceRepository.findByInvoiceNumber(refNumber)
                        .ifPresent(invoice -> {
                            invoice.setStatus("CASH PAID COMPLETE");
                            invoiceRepository.save(invoice);
                        });
            }
            else {
                quotationRepository.findByQuoteNumber(refNumber)
                        .ifPresent(quote -> {
                            quote.setStatus("PAID QUOTATION");
                            quotationRepository.save(quote);
                        });
            }
        }

        if (billRequest.getLinkedReturnIds() != null && !billRequest.getLinkedReturnIds().isEmpty()) {
            for (String returnId : billRequest.getLinkedReturnIds()) {
                salesReturnRepository.findById(returnId).ifPresent(salesReturn -> {
                    salesReturn.setUsed(true);
                    salesReturnRepository.save(salesReturn);
                });
            }
        }

        for (Bill.BillItem item : billRequest.getItems()) {
            String itemCode = item.getCode();
            String itemProductName = item.getProductName();

            if (itemCode != null && !itemCode.isEmpty()) {
                productRepository.findByCodeAndBranchName(itemCode, branchName).ifPresent(p -> {
                    p.setQuantity(p.getQuantity() - item.getQuantity());
                    productRepository.save(p);
                });
            }

            HardwareItem hardwareItem = hardwareRepository.findByProductNameAndBranchName(itemProductName, branchName);

            if (hardwareItem != null) {
                int updatedQty = (int) (hardwareItem.getQuantity() - item.getQuantity());
                hardwareItem.setQuantity(updatedQty);
                hardwareRepository.save(hardwareItem);
            }
        }

        billRequest.setCreatedAt(LocalDateTime.now());
        return billRepository.save(billRequest);
    }

    public Bill getBillByNumber(String billNumber) {
        return billRepository.findByBillNumber(billNumber).orElse(null);
    }

    public List<Bill> getBillsByBranchName(String branchName) {
        return billRepository.findByBranchName(branchName);
    }

    public Map<String, Double> getTodayPaymentStats(String branchName) {
        LocalDate today = LocalDate.now();
        List<Bill> todayBills = billRepository.findByBranchName(branchName).stream()
                .filter(b -> b.getCreatedAt().toLocalDate().isEqual(today))
                .collect(Collectors.toList());

        return todayBills.stream()
                .collect(Collectors.groupingBy(
                        Bill::getPaymentMethod,
                        Collectors.summingDouble(Bill::getTotalFinalAmount)
                ));
    }
}