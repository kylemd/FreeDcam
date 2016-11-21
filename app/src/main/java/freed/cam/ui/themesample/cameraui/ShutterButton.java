/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.ui.themesample.cameraui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import com.troop.freedcam.R;

import freed.ActivityInterface;
import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.modules.CaptureStates;
import freed.utils.Logger;

/**
 * Created by troop on 20.06.2015.
 */
public class ShutterButton extends Button
{
    private final String TAG = ShutterButton.class.getSimpleName();
    private int currentShow = CaptureStates.IMAGE_CAPTURE_STOP;
    private final Drawable shutterImage;
    private final Paint transparent;
    private Paint red;

    //shutter_open_radius for the Transparent Radius to draw to simulate shutter open
    private float shutter_open_radius = 0.0f;
    //frames to draw
    private final int MAXFRAMES = 5;
    //holds the currentframe number
    private int currentframe = 0;
    //handler to call the animaiont frames
    private Handler animationHandler = new Handler();

    private Handler drawingLock = new Handler();
    //size to calculate the shutter_open_step for Transparent shutter_open_radius aka shutteropen
    private int size;
    //the step wich the shutter_open_radius gets increased/decrased
    private int shutter_open_step;
    //true when the red recording button should get shown
    private boolean drawRecordingImage =false;
    //the size for the recrodingbutton to calc the shutter
    private int recordingSize;

    private int recordingRadiusCircle;
    private int recordingRadiusRectangle;

    private CaptureStateReciever captureStateReciever = new CaptureStateReciever();
    private ModuleChangedReciever moduleChangedReciever = new ModuleChangedReciever();
    private ActivityInterface activityInterface;

    public ShutterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.shutter5);
        shutterImage = getBackground();
        transparent = new Paint();
        transparent.setColor(Color.TRANSPARENT);
        transparent.setStyle(Paint.Style.FILL);
        transparent.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        transparent.setAntiAlias(true);
        this.init();
    }

    public ShutterButton(Context context) {
        super(context);
        setBackgroundResource(R.drawable.shutter5);
        shutterImage = getBackground();
        setBackgroundDrawable(null);
        transparent = new Paint(Color.TRANSPARENT);
        transparent.setStyle(Paint.Style.FILL);
        transparent.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        transparent.setAntiAlias(true);
        this.init();
    }

    private void init()
    {
        activityInterface = (ActivityInterface)getContext();
        red = new Paint();
        red.setColor(Color.RED);
        red.setStyle(Paint.Style.FILL);
        red.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        red.setAntiAlias(true);
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendBroadcastDoWork();
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter(getResources().getString(R.string.INTENT_CAPTURESTATE));
        activityInterface.RegisterLocalReciever(captureStateReciever,intentFilter);
        intentFilter = new IntentFilter(getResources().getString(R.string.INTENT_MODULECHANGED));
        activityInterface.RegisterLocalReciever(moduleChangedReciever,intentFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        activityInterface.UnregisterLocalReciever(captureStateReciever);
        activityInterface.UnregisterLocalReciever(moduleChangedReciever);
    }

    private class CaptureStateReciever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int state = intent.getIntExtra(getResources().getString(R.string.INTENT_EXTRA_CAPTURESTATE),2);
            switchBackground(state,true);
        }
    }

    private void sendBroadcastDoWork()
    {
        Intent intent = new Intent(getResources().getString(R.string.INTENT_CAMERADOWORK));
        activityInterface.SendLocalBroadCast(intent);
    }

    private class ModuleChangedReciever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String module = intent.getStringExtra(getResources().getString(R.string.INTENT_EXTRA_MODULECHANGED));
            if (module.equals(KEYS.MODULE_VIDEO))
            {
                switchBackground(CaptureStates.RECORDING_STOP, true);
            }
            else  if((module.equals(KEYS.MODULE_PICTURE)
                    || module.equals(KEYS.MODULE_HDR)
                    || module.equals(KEYS.MODULE_AFBRACKET))) {
                switchBackground(CaptureStates.IMAGE_CAPTURE_STOP,true);
            }
            else if (module.equals(KEYS.MODULE_INTERVAL)
                    || module.equals(KEYS.MODULE_STACKING))
                switchBackground(CaptureStates.CONTINOUSE_CAPTURE_STOP,false);
        }
    }

    private void switchBackground(final int showstate, final boolean animate)
    {
        if (currentShow != showstate) {
            currentShow = showstate;
            Logger.d(TAG, "switchBackground:" +currentShow);
            drawingLock.post(startAnimation);
        }
    }

    private Runnable startAnimation = new Runnable() {
        @Override
        public void run() {
            //animationHandler.removeCallbacks(animationRunnable);
            size = (getWidth()-100) /2;
            shutter_open_step = (size) / MAXFRAMES;
            recordingSize = getWidth()/4;
            recordingRadiusCircle = recordingSize;
            recordingRadiusRectangle = recordingSize;
            currentframe = 0;
            animationHandler.post(animationRunnable);
        }
    };

    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            draw();
            currentframe++;
            if (currentframe < MAXFRAMES)
                animationHandler.post(animationRunnable);
        }
    };

    private void draw()
    {
        switch (currentShow) {
            case CaptureStates.RECORDING_STOP:
                shutter_open_radius = 0;
                recordingRadiusCircle +=currentframe;
                recordingRadiusRectangle -= currentframe;
                drawRecordingImage = true;
                break;
            case CaptureStates.RECORDING_START:
                shutter_open_radius = 0;
                recordingRadiusCircle -=currentframe;
                recordingRadiusRectangle += currentframe;
                drawRecordingImage = true;
                break;
            case CaptureStates.IMAGE_CAPTURE_STOP:
                drawRecordingImage = false;
                shutter_open_radius -= shutter_open_step;
                break;
            case CaptureStates.IMAGE_CAPTURE_START:
                drawRecordingImage = false;
                shutter_open_radius += shutter_open_step;
                break;
            case CaptureStates.CONTINOUSE_CAPTURE_START:
                drawRecordingImage = true;
                if (shutter_open_radius <size)
                    shutter_open_radius += shutter_open_step;
                recordingRadiusCircle -=currentframe;
                recordingRadiusRectangle += currentframe;
                break;
            case CaptureStates.CONTINOUSE_CAPTURE_STOP_WHILE_WORKING:
                drawRecordingImage = true;
                //shutter_open_radius += shutter_open_step;
                recordingRadiusCircle +=currentframe;
                recordingRadiusRectangle -= currentframe;
                break;
            case CaptureStates.CONTINOUSE_CAPTURE_STOP_WHILE_NOTWORKING:
                shutter_open_radius = 0;
                recordingRadiusCircle +=currentframe;
                recordingRadiusRectangle -= currentframe;
                drawRecordingImage = true;
                break;
            case CaptureStates.CONTINOUSE_CAPTURE_STOP:
                recordingRadiusCircle +=currentframe;
                recordingRadiusRectangle -= currentframe;
                drawRecordingImage = true;
                break;
            case CaptureStates.CONTINOUSE_CAPTURE_WORK_START:
                drawRecordingImage = true;
                if (shutter_open_radius < size)
                    shutter_open_radius += shutter_open_step;
                break;
            case CaptureStates.CONTINOUSE_CAPTURE_WORK_STOP:
                drawRecordingImage = true;
                shutter_open_radius -= shutter_open_step;
                break;
        }
        //Logger.d(TAG,"shutter_open:" + shutter_open_radius + " recCircle:" + recordingRadiusCircle + " recRect:" + recordingRadiusRectangle +  " captureState:" + currentShow);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int halfSize = canvas.getWidth()/2;
        shutterImage.draw(canvas);
        canvas.drawCircle(halfSize,halfSize, shutter_open_radius, transparent);
        if (drawRecordingImage)
        {
            canvas.drawCircle(halfSize,halfSize,recordingRadiusCircle/2, red);
            int top = halfSize - recordingRadiusRectangle/2;
            int bottom = halfSize + recordingRadiusRectangle/2;
            int left = halfSize - recordingRadiusRectangle/2;
            int right = halfSize + recordingRadiusRectangle/2;
            canvas.drawRect(left,top,right,bottom,red);
        }
    }
}