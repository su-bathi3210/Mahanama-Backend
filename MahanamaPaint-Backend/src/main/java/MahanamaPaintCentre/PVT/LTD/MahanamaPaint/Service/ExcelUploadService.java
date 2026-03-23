package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.DTO.ItemDTO;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.HardwareItem;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.PendingItemRequest;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.PendingItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelUploadService {

    @Autowired
    private PendingItemRepository pendingRepo;

    @Autowired
    private ProductService productService;

    public void processExcelUpload(MultipartFile file, String branchName) throws Exception {
        List<ItemDTO> items = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        Map<String, String> latestCodesInLoop = new HashMap<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            ItemDTO item = new ItemDTO();

            String excelCode = getCellValueAsString(row.getCell(0), "").trim();
            String company = getCellValueAsString(row.getCell(1), "").trim();

            if (excelCode.isEmpty() || excelCode.equals("-")) {
                if (!company.isEmpty()) {
                    String prefix = company.substring(0, Math.min(company.length(), 3)).toUpperCase();

                    String lastUsedCode = latestCodesInLoop.getOrDefault(
                            prefix,
                            productService.getLastCodeByBranchAndPrefix(branchName, prefix)
                    );

                    String nextGeneratedCode = productService.generateNextCodeFrom(prefix, lastUsedCode);

                    item.setCode(nextGeneratedCode);

                    latestCodesInLoop.put(prefix, nextGeneratedCode);
                } else {
                    item.setCode("-");
                }
            } else {
                item.setCode(excelCode);
            }

            item.setCompany(company);
            item.setProductName(getCellValueAsString(row.getCell(2), ""));
            item.setCategory(getCellValueAsString(row.getCell(3), ""));
            item.setSize(getCellValueAsString(row.getCell(4), "N/A"));
            item.setQuantity((int) getNumericCellValueSafe(row.getCell(5)));
            item.setGross(getNumericCellValueSafe(row.getCell(6)));
            item.setMrp(getNumericCellValueSafe(row.getCell(7)));
            item.setDiscount(getNumericCellValueSafe(row.getCell(8)));
            item.setMaxDiscount(getNumericCellValueSafe(row.getCell(9)));

            items.add(item);
        }

        savePendingRequest(items, file, branchName, "PRODUCT");
        workbook.close();
    }

    public void processHardwareUpload(MultipartFile file, String branchName) throws Exception {
        List<HardwareItem> items = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            HardwareItem item = new HardwareItem();
            item.setCompany(getCellValueAsString(row.getCell(0), ""));
            item.setProductName(getCellValueAsString(row.getCell(1), ""));
            item.setCategory(getCellValueAsString(row.getCell(2), ""));
            item.setSize(getCellValueAsString(row.getCell(3), "N/A"));
            item.setQuantity((int) getNumericCellValueSafe(row.getCell(4)));
            item.setGross(getNumericCellValueSafe(row.getCell(5)));
            item.setMrp(getNumericCellValueSafe(row.getCell(6)));
            item.setDiscount(getNumericCellValueSafe(row.getCell(7)));
            item.setMaxDiscount(getNumericCellValueSafe(row.getCell(8)));

            items.add(item);
        }

        savePendingRequest(items, file, branchName, "HARDWARE");
    }

    private void savePendingRequest(List<?> items, MultipartFile file, String branchName, String type) throws Exception {
        String json = new ObjectMapper().writeValueAsString(items);
        PendingItemRequest request = new PendingItemRequest();
        request.setBranchUsername(branchName);
        request.setItemDataJson(json);
        request.setFileName(file.getOriginalFilename());
        request.setItemCount(items.size());
        request.setStatus("PENDING");
        request.setRequestedAt(LocalDateTime.now());
        request.setType(type);
        pendingRepo.save(request);
    }

    private double getNumericCellValueSafe(Cell cell) {
        if (cell == null) return 0.0;
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        if (cell.getCellType() == CellType.STRING) {
            try { return Double.parseDouble(cell.getStringCellValue().trim()); }
            catch (Exception e) { return 0.0; }
        }
        return 0.0;
    }

    private String getCellValueAsString(Cell cell, String defaultValue) {
        if (cell == null) return defaultValue;
        switch (cell.getCellType()) {
            case STRING:
                String val = cell.getStringCellValue().trim();
                return val.isEmpty() ? defaultValue : val;
            case NUMERIC:
                return String.valueOf((int)cell.getNumericCellValue());
            default:
                return defaultValue;
        }
    }
}