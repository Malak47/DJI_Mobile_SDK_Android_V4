package com.dji.sdk.sample.demo.ILM;

import static com.dji.sdk.sample.internal.utils.ToastUtils.showToast;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.dji.sdk.sample.R;

import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.utils.ModuleVerificationUtil;
import com.dji.sdk.sample.internal.utils.ToastUtils;

import org.osmdroid.views.MapView;

import java.util.Locale;

import dji.common.error.DJIError;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.common.util.CommonCallbacks;
import dji.common.util.CommonCallbacks.CompletionCallback;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.sdkmanager.DJISDKManager;

/**
 * The ILM_Buttons class contains all the buttons for the ILM_RemoteController and its functionalities.
 */
public class ILM_Buttons {
    private final Context context;
    private final View view;

    private final FlightController flightController = ModuleVerificationUtil.getFlightController();
    private final ILM_AdjustCamera cameraAdjust = new ILM_AdjustCamera();
    private ILM_GoTo goTo;
    private ILM_FollowMe followMe;
    protected Button returnToHomeBtn, landBtn, takeOffBtn, goToBtn, followMeBtn, stopBtn, enableVirtualStickBtn, recordBtn, peopleDetectionBtn;
    protected Button waypointBtn, addWaypointBtn, removeWaypointBtn, repeatRouteBtn;
    protected Button cameraAdjustBtn, adjustPitchPlusBtn, adjustPitchMinusBtn, adjustRollPlusBtn, adjustRollMinusBtn, adjustYawPlusBtn, adjustYawMinusBtn;
    protected Button missionsBtn, mission1Btn, mission2Btn, mission3Btn;
    protected Button waypointsBtn, waypoint1Btn, waypoint2Btn, waypoint3Btn, waypoint4Btn, waypoint5Btn, waypoint6Btn, waypoint7Btn, waypoint8Btn;
    protected Button mapResizeBtn;

    private int pitch_adjust = 0;
    private int yaw_adjust = 0;
    private int roll_adjust = 0;

    protected int count = 0;
    protected int setCounter = 0;
    private int counter = 0;

    protected boolean isRecording = false;
    private CountDownTimer recordingTimer;
    private long recordingTimeMillis = 0;
    protected TextView recordText;


    private CompletionCallback createCallback(final String action) {
        return new CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    ToastUtils.setResultToToast(action);
                } else {
                    ToastUtils.setResultToToast(djiError.getDescription());
                }
            }
        };
    }

    public ILM_Buttons(Context context, View view) {
        this.context = context;
        this.view = view;
        followMe = new ILM_FollowMe(context);
        initUI();
        panicStop();
    }

    private void initUI() {
        //<<=====================Main Buttons==========================>>//
        returnToHomeBtn = view.findViewById(R.id.btn_ILM_ReturnToHome);
        landBtn = view.findViewById(R.id.btn_ILM_Land);
        takeOffBtn = view.findViewById(R.id.btn_ILM_Take_Off);
        goToBtn = view.findViewById(R.id.btn_ILM_GoTo);
        followMeBtn = view.findViewById(R.id.btn_ILM_FollowMe);
        stopBtn = view.findViewById(R.id.btn_ILM_Stop);
        enableVirtualStickBtn = view.findViewById(R.id.btn_ILM_Enable_VirtualStick);
        recordBtn = view.findViewById(R.id.btn_ILM_Record);
        peopleDetectionBtn = view.findViewById(R.id.btn_ILM_PeopleDetection);
        //<<=====================Waypoints Buttons==========================>>//
        waypointBtn = view.findViewById(R.id.btn_ILM_Waypoint);
        addWaypointBtn = view.findViewById(R.id.btn_ILM_AddWaypoint);
        removeWaypointBtn = view.findViewById(R.id.btn_ILM_RemoveWaypoint);
        repeatRouteBtn = view.findViewById(R.id.btn_ILM_RepeatRoute);
        //<<=====================Camera Adjust Buttons==========================>>//
        cameraAdjustBtn = view.findViewById(R.id.btn_ILM_CameraAdjust);
        adjustPitchPlusBtn = view.findViewById(R.id.btn_ILM_AdjustPitchPlus);
        adjustPitchMinusBtn = view.findViewById(R.id.btn_ILM_AdjustPitchMinus);
        adjustRollPlusBtn = view.findViewById(R.id.btn_ILM_AdjustRollPlus);
        adjustRollMinusBtn = view.findViewById(R.id.btn_ILM_AdjustRollMinus);
        adjustYawPlusBtn = view.findViewById(R.id.btn_ILM_AdjustYawPlus);
        adjustYawMinusBtn = view.findViewById(R.id.btn_ILM_AdjustYawMinus);
        //<<=====================Missions Buttons==========================>>//
        missionsBtn = view.findViewById(R.id.btn_ILM_Missions);
        mission1Btn = view.findViewById(R.id.btn_ILM_Mission_1);
        mission2Btn = view.findViewById(R.id.btn_ILM_Mission_2);
        mission3Btn = view.findViewById(R.id.btn_ILM_Mission_3);
        //<<=====================Waypoints Buttons==========================>>//
        waypointsBtn = view.findViewById(R.id.btn_ILM_Waypoints);
        waypoint1Btn = view.findViewById(R.id.btn_ILM_Waypoint_1);
        waypoint2Btn = view.findViewById(R.id.btn_ILM_Waypoint_2);
        waypoint3Btn = view.findViewById(R.id.btn_ILM_Waypoint_3);
        waypoint4Btn = view.findViewById(R.id.btn_ILM_Waypoint_4);
        waypoint5Btn = view.findViewById(R.id.btn_ILM_Waypoint_5);
        waypoint6Btn = view.findViewById(R.id.btn_ILM_Waypoint_6);
        waypoint7Btn = view.findViewById(R.id.btn_ILM_Waypoint_7);
        waypoint8Btn = view.findViewById(R.id.btn_ILM_Waypoint_8);
        //<<==========================Other===============================>>//
        mapResizeBtn = view.findViewById(R.id.btn_ILM_MapResize);
        recordText = view.findViewById(R.id.textView_ILM_Record);
    }

    //<<=====================Main Functions==========================>>//
    protected void panicStop() {
        //ToDo: Ask Boaz about it
        if (flightController != null) {
            flightController.setConnectionFailSafeBehavior(ConnectionFailSafeBehavior.GO_HOME, createCallback("Panic Mode Enabled"));
        }
    }

    protected void returnToHome() {
        if (flightController != null) {
            flightController.startGoHome(createCallback("Returning To Home!"));
        }
    }

    protected void land() {
        if (flightController != null) {
            flightController.startLanding(createCallback("Landing!"));
        }
    }

    protected void takeOff() {
        if (flightController != null) {
            flightController.startTakeoff(createCallback("Taking Off!"));
        }
    }

    protected synchronized void goTo(ILM_Waypoints waypoints, ILM_MapController mapController) {
        if (waypoints.getWaypoints().isEmpty()) {
            showToast("Please add waypoints first");
            return;
        }
        if (goTo == null) {
            goTo = new ILM_GoTo(waypoints, mapController);
        }
        goTo.setMode(1);
        double lat = Double.parseDouble(waypoints.getWaypoints().get("Latitude" + count));
        double lon = Double.parseDouble(waypoints.getWaypoints().get("Longitude" + count));
        double alt = Double.parseDouble(waypoints.getWaypoints().get("Altitude" + count));
        count = setCounter;
        Log.e("&Altitude", String.valueOf(alt));
        Log.e("&Latitude", String.valueOf(lat));
        Log.e("&Longitude", String.valueOf(lon));

        goTo.isGoTo = true;
        goTo.setWaypoint(lat, lon, alt);
        goTo.goTo();
    }

    protected void followMe() {
        followMe.FollowMe();
    }

    protected void stop() {
        if (flightController != null) {
            flightController.cancelGoHome(null);
            flightController.cancelTakeoff(null);
            flightController.cancelLanding(createCallback("Drone is Stopped!"));
        }
        if (goTo != null) {
            goTo.isGoTo = false;
        }
        if (flightController != null) {
            flightController.setVirtualStickModeEnabled(false, null);
        }
        if (followMe.isFollowMe) {
            followMe.stopFollowing();
        }
    }

    public void EnableVirtualStick() {
        if (flightController != null) {
            flightController.setVirtualStickModeEnabled(true, new CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    flightController.setVirtualStickAdvancedModeEnabled(true);
                    if (djiError != null) {
                        ToastUtils.setResultToToast(djiError.toString());
                    }

                }
            });
        }
    }

    //<<=====================Recording Functions==========================>>//
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
    //<<=========================================================================>>//

    //<<=======================Waypoints Functions===============================>>//
    protected void waypointBtn() {
        LinearLayout waypointLayout = view.findViewById(R.id.layout_ILM_AddRemoveWaypoint);
        LinearLayout expandedMenuLayout = view.findViewById(R.id.layout_expandedMenuLayout);

        if (waypointLayout.getVisibility() == View.VISIBLE) {
            waypointLayout.setVisibility(View.GONE);
            expandedMenuLayout.setVisibility(View.GONE);
        } else {
            waypointLayout.setVisibility(View.VISIBLE);
            expandedMenuLayout.setVisibility(View.VISIBLE);
        }
    }

    public void addWaypoint(ILM_Waypoints waypoints, ILM_MapController mapController, ILM_AllWaypoints allWaypoints) {
        if (goTo == null)
            goTo = new ILM_GoTo(waypoints, mapController);
        if (count == 0) {
            LocationCoordinate3D aircraftLocation = null;
            if (flightController != null) {
                aircraftLocation = flightController.getState().getAircraftLocation();
            }
            double lat2 = 0;
            double lon2 = 0;
            double alt2 = 0;
            if (aircraftLocation != null) {
                lat2 = aircraftLocation.getLatitude();
                lon2 = aircraftLocation.getLongitude();
                alt2 = aircraftLocation.getAltitude();
            }
            if (counter < 8) {
                allWaypoints.addWaypoint(String.valueOf(lat2), String.valueOf(lon2), String.valueOf(alt2), String.valueOf(0), counter);
            }
        }
        waypoints.updateCSVInfo(mapController);
        counter++;
    }

    public void removeWaypoint(ILM_Waypoints waypoints, ILM_MapController mapController) {
        waypoints.removeWaypoint(mapController);
        ToastUtils.setResultToToast("Waypoint Removed!");
    }

    public void RepeatRoute(ILM_Waypoints waypoints, ILM_MapController mapController) {
        if (goTo == null)
            goTo = new ILM_GoTo(waypoints, mapController);
        goTo.setMode(1);
        goTo.isRepeatRoute = true;
        goTo(waypoints, mapController);
    }
    //<<=========================================================================>>//

    //<<=======================Missions & Waypoints list=========================>>//
    protected void missionListBtn() {
        ScrollView missionsList = view.findViewById(R.id.scrollView_missionsList);
        TableLayout LatLonAlt = view.findViewById(R.id.tableRow_ILM_LatLonAlt);
        if (missionsList.getVisibility() == View.VISIBLE) {
            missionsList.setVisibility(View.GONE);
            LatLonAlt.setVisibility(View.VISIBLE);
        } else {
            missionsList.setVisibility(View.VISIBLE);
            LatLonAlt.setVisibility(View.GONE);
        }
    }

    protected void waypointsBtn() {
        ScrollView waypointsList = view.findViewById(R.id.scrollView_waypointsList);
        TableLayout LatLonAlt = view.findViewById(R.id.tableRow_ILM_LatLonAlt);
        if (waypointsList.getVisibility() == View.VISIBLE) {
            waypointsList.setVisibility(View.GONE);
            LatLonAlt.setVisibility(View.VISIBLE);
        } else {
            waypointsList.setVisibility(View.VISIBLE);
            LatLonAlt.setVisibility(View.GONE);
        }
    }
    //<<=========================================================================>>//

    //<<=======================Camera Adjust Functions===========================>>//
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
    //<<=========================================================================>>//

    //<<=======================Map Functions=====================================>>//
    public void mapResize(boolean isExpanded, MapView mapView) {
        ViewGroup.LayoutParams params = mapView.getLayoutParams();
        if (isExpanded) {
            // Set to small size
            params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, context.getResources().getDisplayMetrics());
        } else {
            // Set to expanded size
            params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, context.getResources().getDisplayMetrics());
            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, context.getResources().getDisplayMetrics());
        }
        mapView.setLayoutParams(params);

    }
}

