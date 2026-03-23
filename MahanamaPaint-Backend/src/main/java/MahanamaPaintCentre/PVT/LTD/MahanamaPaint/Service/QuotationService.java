package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Quotation;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.QuotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

@Service
public class QuotationService {

    @Autowired
    private QuotationRepository quotationRepository;

    public Quotation createQuotation(Quotation quote, String branchName) {
        long count = quotationRepository.count();
        int currentYear = LocalDate.now().getYear();
        String quoteNo = String.format("QU-%d-%03d", currentYear, count + 1);

        quote.setQuoteNumber(quoteNo);
        quote.setIssuedDate(new Date());

        quote.setCreatedByBranch(branchName);

        if(quote.getDueDate() == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, 30);
            quote.setDueDate(cal.getTime());
        }

        quote.setStatus("PENDING");
        return quotationRepository.save(quote);
    }
}