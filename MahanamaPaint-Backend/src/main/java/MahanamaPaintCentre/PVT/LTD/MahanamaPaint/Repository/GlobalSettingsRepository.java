package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.GlobalSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalSettingsRepository extends MongoRepository<GlobalSettings, String> { }