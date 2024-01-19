package com.dji.sdk.sample.demo.ILM;

import static com.dji.sdk.sample.internal.utils.ToastUtils.showToast;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dji.sdk.sample.R;

import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.utils.DialogUtils;
import com.dji.sdk.sample.internal.utils.ModuleVerificationUtil;

import java.util.Locale;

import dji.common.error.DJIError;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;
import dji.common.util.CommonCallbacks.CompletionCallback;
import dji.sdk.flightcontroller.FlightController;
import dji.common.mission.waypoint.Waypoint;


public class ILMButtons {
    protected Button goToBtn;
    protected Button stopBtn;
    protected Button landBtn;
    protected Button takeOffBtn;
    protected Button EnableVirtualStickBtn;
    protected Button panicStopBtn;
    private Context context;
    private View view;
    private FlightController flightController = ModuleVerificationUtil.getFlightController();
    private Waypoint waypoint = new Waypoint();
    protected boolean isRecording = false;
    protected Button stopRecordingBtn;
    protected Button startRecordingBtn;
    private TextView recordingCount;

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


    public ILMButtons(Context context, View view) {
        this.context = context;
        this.view = view;
        goToBtn = view.findViewById(R.id.btn_ILM_GoTo);
        stopBtn = view.findViewById(R.id.btn_ILM_Stop);
        landBtn = view.findViewById(R.id.btn_ILM_Land);
        takeOffBtn = view.findViewById(R.id.btn_ILM_Take_Off);
        EnableVirtualStickBtn = view.findViewById(R.id.btn_ILM_Enable_VirtualStick);
        panicStopBtn = view.findViewById(R.id.btn_ILM_Panic_Stop);

        startRecordingBtn = view.findViewById(R.id.btn_ILM_Start_Recording);
        stopRecordingBtn = view.findViewById(R.id.btn_ILM_Stop_Recording);
        recordingCount = view.findViewById(R.id.textView_ILM_Recording_Count);
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
        stop();
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

    protected void startRecording() {
        if (isRecording) {
            view.findViewById(R.id.ILM_RecordingLayout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.ILM_RecordingLayout).setClickable(true);
            startRecordingBtn.setVisibility(View.INVISIBLE);
            startRecordingBtn.setClickable(false);

            DJISampleApplication.getProductInstance().getCamera().startRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        showToast("Recording started");
                        isRecording = true;
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
        startRecordingBtn.setVisibility(View.VISIBLE);
        startRecordingBtn.setClickable(true);
        view.findViewById(R.id.ILM_RecordingLayout).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.ILM_RecordingLayout).setClickable(false);
        DJISampleApplication.getProductInstance().getCamera().stopRecordVideo(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    showToast("Recording stopped");
                    isRecording = false;
                    recordingCount.setText("00:00"); // Reset count on stop
                } else {
                    showToast("Failed to stop recording: " + djiError.getDescription());
                }
            }
        });
    }

}

