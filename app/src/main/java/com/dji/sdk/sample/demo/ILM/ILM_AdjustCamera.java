package com.dji.sdk.sample.demo.ILM;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

import android.util.Log;

import com.dji.sdk.sample.internal.controller.DJISampleApplication;

import dji.common.error.DJIError;
import dji.common.gimbal.Rotation;
import dji.common.gimbal.RotationMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.sdkmanager.DJISDKManager;

/**
 * The ILM_AdjustCamera class provides methods to adjust the gimbal's camera rotation
 * for DJI drones. It implements the ILM_iAdjustCamera interface and provides
 * functionalities to adjust yaw, pitch, and roll of the camera, as well as retrieve their values.
 */
public class ILM_AdjustCamera implements ILM_iAdjustCamera {
    private int yaw;
    private int roll;
    private int pitch;

    /**
     * Constructor for ILM_AdjustCamera.
     * Initializes the gimbal state and sets up a callback to update yaw, pitch, and roll values.
     */
    public ILM_AdjustCamera() {
        Gimbal gimbal = DJISampleApplication.getProductInstance().getGimbal();
        if (gimbal != null) {
            gimbal.setStateCallback(gimbalState -> {
                runOnUiThread(() -> {
                    this.pitch = (int) gimbalState.getAttitudeInDegrees().getPitch();
                    this.roll = (int) gimbalState.getAttitudeInDegrees().getRoll();
                    this.yaw = (int) gimbalState.getAttitudeInDegrees().getYaw();
                });
            });
        }
    }

    /**
     * Adjusts the camera's gimbal position based on the current yaw, pitch, and roll values.
     * The rotation is set to absolute angles.
     */
    @Override
    public void adjustCamera() {
        Gimbal gimbal = DJISDKManager.getInstance().getProduct().getGimbal();
        if (gimbal != null) {
            gimbal.rotate(new Rotation.Builder().yaw(this.yaw)
                    .roll(this.roll)
                    .pitch(this.pitch)
                    .mode(RotationMode.ABSOLUTE_ANGLE)
                    .build(), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        Log.d("Gimbal", "Rotation successful");
                    } else {
                        Log.e("Gimbal", "Rotation failed: " + djiError.getDescription());
                    }
                }
            });
        }
    }

    @Override
    public void setYaw(int value) {
        this.yaw = value;
        adjustCamera();
    }

    @Override
    public int getYaw() {
        return this.yaw;
    }

    @Override
    public void setRoll(int value) {
        this.roll = value;
        adjustCamera();
    }

    @Override
    public int getRoll() {
        return this.roll;
    }

    @Override
    public void setPitch(int value) {
        this.pitch = value;
        adjustCamera();
    }

    @Override
    public int getPitch() {
        return this.pitch;
    }
}

