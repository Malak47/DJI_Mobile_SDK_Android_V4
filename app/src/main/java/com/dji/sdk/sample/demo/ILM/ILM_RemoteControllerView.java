package com.dji.sdk.sample.demo.ILM;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

import android.content.Context;

import com.dji.sdk.sample.R;

import org.osmdroid.views.MapView;

import android.app.Service;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.controller.MainActivity;

import com.dji.sdk.sample.internal.view.PresentableView;

import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;

public class ILM_RemoteControllerView extends RelativeLayout implements TextureView.SurfaceTextureListener, View.OnClickListener, PresentableView {
    private MapView mapView;
    private Context context;
    private ILM_Map mapController;
    private ILM_StatusBar statusBar;
    private ILM_CSVLog csvLog;
    private ILM_Buttons buttons;
    private ILM_VirtualStickView virtualStickView;
    protected VideoFeeder.VideoDataListener mReceivedVideoDataListener = null;
    protected DJICodecManager mCodecManager = null;
    protected TextureView mVideoSurface = null;
    private TextView battery, speed, x, y, z, pitch, roll, yaw, date, distance, latitude, longitude, altitude;

    public ILM_RemoteControllerView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    private void init(Context context) {
        setClickable(true);
        //<<=====================Status Bar View==========================>>//
        statusBar = new ILM_StatusBar();
        //<<==========================Virtual Stick=========================>>//
        virtualStickView = new ILM_VirtualStickView(context);
        addView(virtualStickView);
        virtualStickView.setVisibility(View.INVISIBLE);
        virtualStickView.setClickable(false);
        //<<==============================================================>>//
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_ilm_remote_controller, this, true);
        initUI();

        mReceivedVideoDataListener = new VideoFeeder.VideoDataListener() {

            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                if (mCodecManager != null) {
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
            }
        };
    }

    private void initUI() {
        //<<=========================CSV Log==========================>>//
        csvLog = new ILM_CSVLog(context, statusBar);
        csvLog.createLogBrain();
        //<<==========================Map==========================>>//
        mapView = findViewById(R.id.mapView_ILM);
        mapController = new ILM_Map(context, mapView);
        mapController.initMap();
        //<<==========================Video==========================>>//
        mVideoSurface = findViewById(R.id.video_previewer_surface);
        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);
        }
        //<<==========================Status Bar==========================>>//
        latitude = findViewById(R.id.textView_ILM_LatitudeInt1);
        longitude = findViewById(R.id.textView_ILM_LongitudeInt1);
        altitude = findViewById(R.id.textView_ILM_AltitudeInt1);

        x = findViewById(R.id.textView_ILM_XInt1);
        y = findViewById(R.id.textView_ILM_YInt1);
        z = findViewById(R.id.textView_ILM_ZInt1);

        speed = findViewById(R.id.textView_ILM_SpeedInt1);
        distance = findViewById(R.id.textView_ILM_DistanceInt1);
        battery = findViewById(R.id.textView_ILM_BatteryInt1);
        date = findViewById(R.id.textView_ILM_DateInt1);

        pitch = findViewById(R.id.textView_ILM_PitchInt1);
        roll = findViewById(R.id.textView_ILM_RollInt1);
        yaw = findViewById(R.id.textView_ILM_YawInt1);
        //<<==========================Status Bar Updates==========================>>//
        statusBar.updateDateTime(date);
        statusBar.updateBattery(battery);
        statusBar.updateSpeed(speed);
        statusBar.updateXYZ(x, y, z);
        statusBar.updateLatitudeLongitudeAltitude(latitude, longitude, altitude);
        statusBar.updatePitchRollYaw(pitch, roll, yaw);
        //<<==========================Buttons==========================>>//
        buttons = new ILM_Buttons(context, this);
        buttons.takeOffBtn.setOnClickListener(this);
        buttons.stopBtn.setOnClickListener(this);
        buttons.landBtn.setOnClickListener(this);
        buttons.goToBtn.setOnClickListener(this);
        buttons.enableVirtualStickBtn.setOnClickListener(this);
        buttons.panicStopBtn.setOnClickListener(this);
        buttons.recordBtn.setOnClickListener(this);
        buttons.waypointBtn.setOnClickListener(this);

        buttons.cameraAdjustBtn.setOnClickListener(this);
        buttons.adjustPitchPlusBtn.setOnClickListener(this);
        buttons.adjustPitchMinusBtn.setOnClickListener(this);
        buttons.adjustRollPlusBtn.setOnClickListener(this);
        buttons.adjustRollMinusBtn.setOnClickListener(this);
        buttons.adjustYawPlusBtn.setOnClickListener(this);
        buttons.adjustYawMinusBtn.setOnClickListener(this);

    }

    public void switchToVirtualStickLayout() {
        //findViewById(R.id.buttons_relativeLayout).setClickable(false);
        findViewById(R.id.buttons_relativeLayout).setVisibility(View.INVISIBLE);
        virtualStickView = new ILM_VirtualStickView(context);
        addView(virtualStickView);
        buttons.EnableVirtualStick();
    }

    public void switchToMainLayout() {
        removeView(virtualStickView);

        //findViewById(R.id.buttons_relativeLayout).setClickable(true);
        findViewById(R.id.buttons_relativeLayout).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ILM_Take_Off:
                buttons.takeOff();
                break;
            case R.id.btn_ILM_Stop:
                buttons.stop();
                break;
            case R.id.btn_ILM_Land:
                buttons.land();
                break;
            case R.id.btn_ILM_GoTo:
                buttons.goTo();
                break;
            case R.id.btn_ILM_Enable_VirtualStick:
                switchToVirtualStickLayout();
                break;
            case R.id.btn_ILM_Panic_Stop:
                buttons.panicStop();
                break;
            case R.id.btn_ILM_Record:
                buttons.isRecording = !buttons.isRecording;
                buttons.record();
                break;
            case R.id.btn_ILM_Waypoint:
                buttons.waypointBtn();
                break;
            case R.id.btn_ILM_AddWaypoint:
                buttons.addWaypoint();
                break;
            case R.id.btn_ILM_RemoveWaypoint:
                buttons.removeWaypoint();
                break;
            case R.id.btn_ILM_CameraAdjust:
                buttons.cameraAdjustVisibility();
                break;
            case R.id.btn_ILM_AdjustPitchPlus:
                buttons.cameraAdjust("pitch", '+');
                Log.e("AdjustPitchPlus", "AdjustPitchPlus");
                break;
            case R.id.btn_ILM_AdjustPitchMinus:
                buttons.cameraAdjust("pitch", '-');
                Log.e("AdjustPitchMinus", "AdjustPitchMinus");
                break;
            case R.id.btn_ILM_AdjustRollPlus:
                buttons.cameraAdjust("roll", '+');
                Log.e("AdjustRollPlus", "AdjustRollPlus");
                break;
            case R.id.btn_ILM_AdjustRollMinus:
                buttons.cameraAdjust("roll", '-');
                Log.e("AdjustRollMinus", "AdjustRollMinus");
                break;
            case R.id.btn_ILM_AdjustYawPlus:
                buttons.cameraAdjust("yaw", '+');
                Log.e("AdjustYawPlus", "AdjustYawPlus");
                break;
            case R.id.btn_ILM_AdjustYawMinus:
                buttons.cameraAdjust("yaw", '-');
                Log.e("AdjustYawMinus", "AdjustYawMinus");
                break;
            default:
                break;
        }
    }

    @Override
    public int getDescription() {
        return R.string.component_listview_ilm_remote_controller;
    }

    @NonNull
    @Override
    public String getHint() {
        return this.getClass().getSimpleName() + ".java";
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        refreshView();
        DJISampleApplication.getEventBus().post(new MainActivity.RequestStartFullScreenEvent());
    }

    // Refresh the view
    public void refreshView() {
        invalidate(); // Invalidate the view, forcing a redraw
    }

    @Override
    protected void onDetachedFromWindow() {
        if (csvLog != null)
            csvLog.closeLogBrain();     //Closing CSV
        DJISampleApplication.getEventBus().post(new MainActivity.RequestEndFullScreenEvent());
        super.onDetachedFromWindow();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mCodecManager == null) {
            mCodecManager = new DJICodecManager(this.getContext(), surface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}


