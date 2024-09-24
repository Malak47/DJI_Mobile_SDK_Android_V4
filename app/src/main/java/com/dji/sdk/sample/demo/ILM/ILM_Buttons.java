package com.dji.sdk.sample.demo.ILM;

import static com.dji.sdk.sample.internal.utils.ToastUtils.showToast;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dji.sdk.sample.R;

import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.utils.DialogUtils;
import com.dji.sdk.sample.internal.utils.ModuleVerificationUtil;
import com.dji.sdk.sample.internal.utils.ToastUtils;

import java.util.Locale;

import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;
import dji.common.util.CommonCallbacks.CompletionCallback;
import dji.sdk.flightcontroller.FlightController;
import dji.common.mission.waypoint.Waypoint;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.sdkmanager.DJISDKManager;

public class ILM_Buttons {
    private Context context;
    private View view;
    private FlightController flightController = ModuleVerificationUtil.getFlightController();
    private ILM_AdjustCamera cameraAdjust = new ILM_AdjustCamera();
    private Waypoint waypoint = new Waypoint();
    protected boolean isRecording = false;
    protected Button goToBtn;
    protected Button stopBtn;
    protected Button landBtn;
    protected Button takeOffBtn;
    protected Button enableVirtualStickBtn;
    protected Button panicStopBtn;
    protected Button recordBtn;
    protected Button waypointBtn;
    protected Button addWaypointBtn;
    protected Button removeWaypointBtn;
    protected Button cameraAdjustBtn;
    protected Button adjustPitchPlusBtn, adjustPitchMinusBtn, adjustRollPlusBtn, adjustRollMinusBtn, adjustYawPlusBtn, adjustYawMinusBtn;
    private int pitch_adjust = 0;
    private int yaw_adjust = 0;
    private int roll_adjust = 0;
    protected TextView recordText;
    private CompletionCallback callback = new CompletionCallback() {
        @Override
        public void onResult(DJIError djiError) {
            if (djiError == null) {
                ToastUtils.setResultToToast(context.getResources().getString(R.string.success));
//                DialogUtils.showDialog(context, context.getResources().getString(R.string.success));
            } else {
                ToastUtils.setResultToToast(djiError.getDescription());
//                DialogUtils.showDialog(context, djiError.getDescription());
            }
        }
    };
    private CountDownTimer recordingTimer;
    private long recordingTimeMillis = 0;

    public ILM_Buttons(Context context, View view) {
        this.context = context;
        this.view = view;
        initUI();
    }

    private void initUI() {
        goToBtn = view.findViewById(R.id.btn_ILM_GoTo);
        stopBtn = view.findViewById(R.id.btn_ILM_Stop);
        landBtn = view.findViewById(R.id.btn_ILM_Land);
        takeOffBtn = view.findViewById(R.id.btn_ILM_Take_Off);
        enableVirtualStickBtn = view.findViewById(R.id.btn_ILM_Enable_VirtualStick);
        panicStopBtn = view.findViewById(R.id.btn_ILM_Panic_Stop);
        recordBtn = view.findViewById(R.id.btn_ILM_Record);
        recordText = view.findViewById(R.id.textView_ILM_Record);
        waypointBtn = view.findViewById(R.id.btn_ILM_Waypoint);
        addWaypointBtn = view.findViewById(R.id.btn_ILM_AddWaypoint);
        removeWaypointBtn = view.findViewById(R.id.btn_ILM_RemoveWaypoint);

        cameraAdjustBtn = view.findViewById(R.id.btn_ILM_CameraAdjust);
        adjustPitchPlusBtn = view.findViewById(R.id.btn_ILM_AdjustPitchPlus);
        adjustPitchMinusBtn = view.findViewById(R.id.btn_ILM_AdjustPitchMinus);
        adjustRollPlusBtn = view.findViewById(R.id.btn_ILM_AdjustRollPlus);
        adjustRollMinusBtn = view.findViewById(R.id.btn_ILM_AdjustRollMinus);
        adjustYawPlusBtn = view.findViewById(R.id.btn_ILM_AdjustYawPlus);
        adjustYawMinusBtn = view.findViewById(R.id.btn_ILM_AdjustYawMinus);

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record();
            }
        });
    }

    protected void addWaypoint() {

    }

    protected void removeWaypoint() {

    }

    protected void takeOff() {
        flightController.startTakeoff(callback);
    }

    protected void stop() {
        flightController.cancelGoHome(callback);
        flightController.cancelTakeoff(callback);
        flightController.cancelLanding(callback);
    }

    protected void panicStop() {
        // Stop any ongoing mission
        // TODO: 1/23/2024 Complete this
        // Set all relevant parameters to 0
        FlightControlData flightControlData = new FlightControlData(0, 0, 0, 0);
        flightController.sendVirtualStickFlightControlData(flightControlData, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError != null) {
                    showToast("Failed to send virtual stick data: " + djiError.getDescription());
                }
            }
        });
    }

    protected void land() {
        flightController.startLanding(callback);
    }

    protected void goTo() {
        waypoint.coordinate = new LocationCoordinate2D(32.000010, 35.000002);
    }

    public void EnableVirtualStick() {
        flightController.setVirtualStickModeEnabled(true, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                flightController.setVirtualStickAdvancedModeEnabled(true);
                ToastUtils.setResultToToast(djiError.toString());
//                DialogUtils.showDialogBasedOnError(context, djiError);
            }
        });
    }

    public void record() {
        if (isRecording) {
            startRecording();
            startRecordingTimer();

        } else {
            stopRecording();
            recordText.setText("Start Recording");
            stopRecordingTimer();
        }
    }

    private void startRecordingTimer() {
        recordingTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                recordingTimeMillis += 1000;
                updateTimerText();
            }

            @Override
            public void onFinish() {
            }
        };
        recordingTimer.start();
    }

    private void stopRecordingTimer() {
        if (recordingTimer != null) {
            recordingTimer.cancel();
            recordingTimeMillis = 0;
        }
    }

    private void updateTimerText() {
        long minutes = (recordingTimeMillis / 1000) / 60;
        long seconds = (recordingTimeMillis / 1000) % 60;
        recordText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    protected void startRecording() {
        if (isRecording) {
            DJISampleApplication.getProductInstance().getCamera().startRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        showToast("Recording started");
                        isRecording = true;
                        recordBtn.setBackgroundResource(R.drawable.ilm_drone_capture_video_on);
                    } else {
                        showToast("Failed to start recording: " + djiError.getDescription());
                    }
                }
            });

        } else {
            showToast("Camera is already recording.");
        }
    }

    protected void stopRecording() {
        DJISampleApplication.getProductInstance().getCamera().stopRecordVideo(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    showToast("Recording stopped");
                    isRecording = false;
                    recordBtn.setBackgroundResource(R.drawable.ilm_drone_capture_video_off);
                } else {
                    showToast("Failed to stop recording: " + djiError.getDescription());
                }
            }
        });
    }

    protected void waypointBtn() {
        LinearLayout waypointLayout = view.findViewById(R.id.layout_ILM_AddRemoveWaypoint);
        if (waypointLayout.getVisibility() == View.VISIBLE) {
            waypointLayout.setVisibility(View.GONE);
        } else {
            waypointLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void cameraAdjustVisibility() {
        LinearLayout cameraAdjustLayout = view.findViewById(R.id.layout_ILM_AdjustPitchRollYaw);
        if (cameraAdjustLayout.getVisibility() == View.VISIBLE) {
            cameraAdjustLayout.setVisibility(View.GONE);
        } else {
            cameraAdjustLayout.setVisibility(View.VISIBLE);
        }
    }

    public void cameraAdjust(String str, char symbol) {
        Gimbal gimbal = DJISDKManager.getInstance().getProduct().getGimbal();
        if (gimbal != null) {
            switch (str) {
                case "yaw":
                    if (symbol == '+') {
                        yaw_adjust += 1;
                    } else if (symbol == '-') {
                        yaw_adjust -= 1;
                    }
                    cameraAdjust.setYaw(yaw_adjust);
                    break;
                case "roll":
                    if (symbol == '+') {
                        roll_adjust += 1;
                    } else if (symbol == '-') {
                        roll_adjust -= 1;
                    }
                    cameraAdjust.setRoll(roll_adjust);
                    break;
                case "pitch":
                    if (symbol == '+') {
                        pitch_adjust += 1;
                    } else if (symbol == '-') {
                        pitch_adjust -= 1;
                    }
                    cameraAdjust.setPitch(pitch_adjust);
                    break;
            }
        }
    }
}

