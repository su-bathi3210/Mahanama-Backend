package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Product;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public String getLastCodeByBranchAndPrefix(String branchName, String prefix) {
        return productRepository.findFirstByBranchNameAndCodeStartingWithOrderByCodeDesc(branchName, prefix)
                .map(Product::getCode)
                .orElse(null);
    }

    public String generateNextCodeFrom(String prefix, String lastCode) {
        if (lastCode == null || lastCode.isEmpty()) return prefix + "0001";
        try {
            String numberPart = lastCode.substring(prefix.length());
            int nextNumber = Integer.parseInt(numberPart) + 1;
            return prefix + String.format("%04d", nextNumber);
        } catch (Exception e) {
            return prefix + "0001";
        }
    }
}