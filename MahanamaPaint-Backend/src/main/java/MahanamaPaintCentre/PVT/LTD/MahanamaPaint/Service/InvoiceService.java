package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.HardwareItem;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Invoice;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.HardwareRepository;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.InvoiceRepository;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private HardwareRepository hardwareRepository;

    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        String branchName = invoice.getBranchName();
        int currentYear = LocalDateTime.now().getYear();

        Optional<Invoice> lastInvoice = invoiceRepository.findFirstByBranchNameOrderByCreatedAtDesc(branchName);
        int nextNumber = 1;
        if (lastInvoice.isPresent()) {
            String lastNo = lastInvoice.get().getInvoiceNumber();
            try {
                String[] parts = lastNo.split("-");
                nextNumber = Integer.parseInt(parts[2]) + 1;
            } catch (Exception e) {
                nextNumber = 1;
            }
        }

        String finalInvoiceNumber = String.format("INV-%d-%03d", currentYear, nextNumber);
        invoice.setInvoiceNumber(finalInvoiceNumber);
        invoice.setCreatedAt(LocalDateTime.now());

        if (invoice.getItems() != null) {
            for (Invoice.InvoiceItem item : invoice.getItems()) {
                String itemCode = item.getCode();
                String itemName = item.getProductName();

                if (itemCode != null && !itemCode.isEmpty()) {
                    productRepository.findByCodeAndBranchName(itemCode, branchName)
                            .ifPresent(product -> {
                                product.setQuantity(product.getQuantity() - item.getQuantity());
                                productRepository.save(product);
                            });
                }

                HardwareItem h = hardwareRepository.findByProductNameAndBranchName(itemName, branchName);
                if (h != null) {
                    h.setQuantity(h.getQuantity() - item.getQuantity());
                    hardwareRepository.save(h);
                }
            }
        }

        if (invoice.getAdvancePayment() > 0) {
            String method = (invoice.getAdvancePaymentMethod() != null)
                    ? invoice.getAdvancePaymentMethod()
                    : "CASH";

            String details = String.format("%s-%.0f", method, invoice.getAdvancePayment());
            invoice.setAdvancePaymentDetails(details);
        }

        invoice.setStatus("CASH PAID INCOMPLETE");
        invoice.setCreatedAt(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getInvoicesByBranch(String branchName) {
        return invoiceRepository.findByBranchNameOrderByCreatedAtDesc(branchName);
    }

    public void markAsComplete(String invoiceNumber) {
        invoiceRepository.findByInvoiceNumber(invoiceNumber).ifPresent(invoice -> {
            invoice.setStatus("CASH PAID COMPLETE");
            invoiceRepository.save(invoice);
        });
    }

    public List<Map<String, Object>> getIncompleteInvoices(String branchName) {
        LocalDate today = LocalDate.now();

        return invoiceRepository.findByBranchName(branchName).stream()
                .filter(inv -> "CASH PAID INCOMPLETE".equalsIgnoreCase(inv.getStatus()))
                .map(inv -> {
                    double pendingAmount = inv.getTotalFinalAmount() - inv.getAdvancePayment();

                    long overdueDays = 0;
                    if (inv.getDueDate() != null) {
                        overdueDays = ChronoUnit.DAYS.between(inv.getDueDate().toLocalDate(), today);
                    }

                    Map<String, Object> map = new HashMap<>();
                    map.put("customerName", inv.getCustomerName());
                    map.put("totalAmount", inv.getTotalFinalAmount());
                    map.put("pendingAmount", pendingAmount);
                    map.put("invoiceNumber", inv.getInvoiceNumber());
                    map.put("dueDate", inv.getDueDate() != null ? inv.getDueDate().toLocalDate().toString() : "N/A");
                    map.put("overdueDays", overdueDays > 0 ? overdueDays + " days overdue" : "Due soon");
                    return map;
                })
                .collect(Collectors.toList());
    }
}
