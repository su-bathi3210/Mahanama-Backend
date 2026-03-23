package MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Controller;

import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Model.GlobalSettings;
import MahanamaPaintCentre.PVT.LTD.MahanamaPaint.Service.GlobalSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
public class GlobalSettingsController {

    @Autowired
    private GlobalSettingsService service;

    @GetMapping("/global")
    public ResponseEntity<GlobalSettings> getGlobalSettings() {
        return ResponseEntity.ok(service.getSettings());
    }

    @PostMapping("/update")
    public ResponseEntity<GlobalSettings> updateGlobalSettings(@RequestBody GlobalSettings settings) {
        GlobalSettings updated = service.updateSettings(settings);
        return ResponseEntity.ok(updated);
    }
}