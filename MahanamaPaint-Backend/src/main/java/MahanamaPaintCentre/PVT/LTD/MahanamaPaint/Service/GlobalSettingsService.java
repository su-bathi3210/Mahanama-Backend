package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.GlobalSettings;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GlobalSettingsService {

    @Autowired
    private GlobalSettingsRepository repository;

    private static final String SETTINGS_ID = "GLOBAL_CONFIG";

    public GlobalSettings getSettings() {
        return repository.findById(SETTINGS_ID).orElseGet(() -> {
            GlobalSettings defaultSettings = new GlobalSettings();
            defaultSettings.setId(SETTINGS_ID);
            defaultSettings.setVatPercentage(0.0);
            return repository.save(defaultSettings);
        });
    }

    public GlobalSettings updateSettings(GlobalSettings newSettings) {
        GlobalSettings existing = getSettings();

        if (newSettings.getVatPercentage() != null) {
            existing.setVatPercentage(newSettings.getVatPercentage());
        }

        return repository.save(existing);
    }
}