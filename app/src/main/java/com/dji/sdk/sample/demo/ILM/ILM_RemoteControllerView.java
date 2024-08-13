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

    public ILM_RemoteControllerView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    private void init(Context context) {
        setClickable(true);
        //<<=====================Status Bar View==========================>>//
        statusBar = new ILM_StatusBar(context);
        addView(statusBar);
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
        //<<==========================Status Bar Updates==========================>>//
        statusBar.updateDateTime();
        statusBar.updateBattery();
        statusBar.updateSpeed();
        statusBar.updateXYZ();
        statusBar.updateLatitudeLongitude();
        statusBar.updatePitchRollYaw();
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


