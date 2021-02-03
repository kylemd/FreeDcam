package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.featuredetector.Camera2Util;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AutoExposureModeDetector extends BaseParameter2Detector {

    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        Camera2Util.detectIntMode(cameraCharacteristics, CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES, SettingsManager.get(SettingKeys.ExposureMode), FreedApplication.getStringArrayFromRessource(R.array.aemodes));
        SettingsManager.get(SettingKeys.ExposureMode).set(FreedApplication.getStringFromRessources(R.string.on));

    }
}
