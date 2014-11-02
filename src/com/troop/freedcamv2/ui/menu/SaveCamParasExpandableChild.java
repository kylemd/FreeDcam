package com.troop.freedcamv2.ui.menu;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;

import com.troop.freedcamv2.camera.CameraUiWrapper;
import com.troop.freedcamv2.camera.parameters.modes.I_ModeParameter;
import com.troop.freedcamv2.ui.AppSettingsManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Ingo on 02.11.2014.
 */
public class SaveCamParasExpandableChild extends ExpandableChild {
    public SaveCamParasExpandableChild(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SaveCamParasExpandableChild(Context context) {
        super(context);
    }

    public SaveCamParasExpandableChild(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setParameterHolder(I_ModeParameter parameterHolder, AppSettingsManager appSettingsManager, String settingsname, ArrayList<String> modulesToShow, CameraUiWrapper cameraUiWrapper) {
        this.parameterHolder = parameterHolder;
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingsname;
        this.cameraUiWrapper = cameraUiWrapper;
        nameTextView.setText("Save CamParameter");
        valueTextView.setText("");
    }

    public void SaveCamParameters()
    {
        String[] paras = cameraUiWrapper.cameraHolder.GetCamera().getParameters().flatten().split(";");
        Arrays.sort(paras);

        FileOutputStream outputStream;

        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/CameraParameters.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            outputStream = new FileOutputStream(file);
            outputStream.write((Build.MODEL + "\r\n").getBytes());
            for (String s : paras)
            {
                outputStream.write((s+"\r\n").getBytes());
            }

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
