package com.dji.sdk.sample.demo.ILM;

import static com.dji.sdk.sample.internal.utils.ToastUtils.showToast;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dji.sdk.sample.R;

import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.utils.DialogUtils;
import com.dji.sdk.sample.internal.utils.ModuleVerificationUtil;

import java.util.Locale;

import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;
import dji.common.util.CommonCallbacks.CompletionCallback;
import dji.sdk.flightcontroller.FlightController;
import dji.common.mission.waypoint.Waypoint;

public class ILMButtons {
    private Context context;
    private View view;
    private FlightController flightController = ModuleVerificationUtil.getFlightController();
    private Waypoint waypoint = new Waypoint();
    protected boolean isRecording = false;
    protected Button goToBtn;
    protected Button stopBtn;
    protected Button landBtn;
    protected Button takeOffBtn;
    protected Button EnableVirtualStickBtn;
    protected Button panicStopBtn;
    protected Button RecordBtn;
    protected TextView RecordText;
    private CompletionCallback callback = new CompletionCallback() {
        @Override
        public void onResult(DJIError djiError) {
            if (djiError == null) {
                DialogUtils.showDialog(context, context.getResources().getString(R.string.success));
            } else {
                DialogUtils.showDialog(context, djiError.getDescription());
            }
        }
    };
    private CountDownTimer recordingTimer;
    private long recordingTimeMillis = 0;

    public ILMButtons(Context context, View view) {
        this.context = context;
        this.view = view;
        initUI();
    }

    private void initUI() {
        goToBtn = view.findViewById(R.id.btn_ILM_GoTo);
        stopBtn = view.findViewById(R.id.btn_ILM_Stop);
        landBtn = view.findViewById(R.id.btn_ILM_Land);
        takeOffBtn = view.findViewById(R.id.btn_ILM_Take_Off);
        EnableVirtualStickBtn = view.findViewById(R.id.btn_ILM_Enable_VirtualStick);
        panicStopBtn = view.findViewById(R.id.btn_ILM_Panic_Stop);
        RecordBtn = view.findViewById(R.id.btn_ILM_Record);
        RecordText = view.findViewById(R.id.textView_ILM_Record);

        RecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record();
            }
        });
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
        waypoint.coordinate = new LocationCoordinate2D(32.101355, 35.202021);
    }

    public void EnableVirtualStick() {
        flightController.setVirtualStickModeEnabled(true, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                flightController.setVirtualStickAdvancedModeEnabled(true);
                DialogUtils.showDialogBasedOnError(context, djiError);
            }
        });
    }

    public void record() {
        if (isRecording) {
            startRecording();
            startRecordingTimer();

        } else {
            stopRecording();
            RecordText.setText("Start Recording");
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
        RecordText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    protected void startRecording() {
        if (isRecording) {
            DJISampleApplication.getProductInstance().getCamera().startRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        showToast("Recording started");
                        isRecording = true;
                        RecordBtn.setBackgroundResource(R.drawable.ilm_drone_capture_video_on);
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
                    RecordBtn.setBackgroundResource(R.drawable.ilm_drone_capture_video_off);
                } else {
                    showToast("Failed to stop recording: " + djiError.getDescription());
                }
            }
        });
    }


}

