package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {
    List<Expense> findByBranchNameOrderByCreatedAtDesc(String branchName);
    Optional<Expense> findByBranchNameAndCreatedAtBetween(String branchName, LocalDateTime start, LocalDateTime end);
}