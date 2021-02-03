package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class AeBracketDetector extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectMode(cameraCharacteristics, R.string.ae_bracket_hdr,R.string.ae_bracket_hdr_values, SettingsManager.get(SettingKeys.AE_Bracket));
    }
}
