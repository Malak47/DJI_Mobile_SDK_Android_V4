package com.dji.sdk.sample.demo.ILM;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

import android.content.Context;

import com.dji.sdk.sample.R;

import android.app.Service;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.controller.MainActivity;
import com.dji.sdk.sample.internal.utils.ModuleVerificationUtil;
import com.dji.sdk.sample.internal.view.PresentableView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dji.common.battery.BatteryState;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.common.gimbal.GimbalState;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.gimbal.Gimbal;

public class ILM_StatusBar extends RelativeLayout implements PresentableView {
    private Context context;
    private TextView battery, speed, x, y, z, pitch, roll, yaw, date, distance, latitude, longitude, altitude;
    private Handler dateUpdateHandler = new Handler();
    private Handler locationUpdateHandler = new Handler();


    public ILM_StatusBar(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    private void init(Context context) {
        setClickable(true);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_ilm_status_bar, this, true);
        initUI();
    }


    private void initUI() {
        latitude = findViewById(R.id.textView_ILM_LatitudeInt);
        longitude = findViewById(R.id.textView_ILM_LongitudeInt);
        altitude = findViewById(R.id.textView_ILM_AltitudeInt);

        x = findViewById(R.id.textView_ILM_XInt);
        y = findViewById(R.id.textView_ILM_YInt);
        z = findViewById(R.id.textView_ILM_ZInt);

        speed = findViewById(R.id.textView_ILM_SpeedInt);
        distance = findViewById(R.id.textView_ILM_DistanceInt);
        battery = findViewById(R.id.textView_ILM_BatteryInt);
        date = findViewById(R.id.textView_ILM_DateInt);

        pitch = findViewById(R.id.textView_ILM_PitchInt);
        roll = findViewById(R.id.textView_ILM_RollInt);
        yaw = findViewById(R.id.textView_ILM_YawInt);
    }

    @Override
    public int getDescription() {
        return R.string.component_listview_ilm_status_bar;
    }

    @NonNull
    @Override
    public String getHint() {
        return this.getClass().getSimpleName() + ".java";
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        DJISampleApplication.getEventBus().post(new MainActivity.RequestStartFullScreenEvent());
        updateDateTime();
        updateBattery();
        updateSpeed();
        updateXYZ();
        updateLatitudeLongitude();
        updatePitchRollYaw();
    }

    @Override
    protected void onDetachedFromWindow() {
        DJISampleApplication.getEventBus().post(new MainActivity.RequestEndFullScreenEvent());
        super.onDetachedFromWindow();
    }

    public void updateDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss", Locale.getDefault());
        Runnable updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                String formattedDateTime = dateFormat.format(new Date());
                if (date != null) {
                    date.setText(formattedDateTime);
                }
                dateUpdateHandler.postDelayed(this, 1000);
            }
        };
        updateTimeRunnable.run();
    }

    public void updateBattery() {
        DJISampleApplication.getProductInstance().getBattery().setStateCallback(new BatteryState.Callback() {
            @Override
            public void onUpdate(BatteryState djiBatteryState) {
                int batteryPercentage = djiBatteryState.getChargeRemainingInPercent();
                if (battery != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            battery.setText(String.valueOf(batteryPercentage) + "%");

                        }
                    });
                }
            }
        });
    }

    public void updateSpeed() {
        final DecimalFormat decimalFormat = new DecimalFormat("0.0");

        if (ModuleVerificationUtil.isFlightControllerAvailable()) {
            DJISampleApplication.getAircraftInstance().getFlightController().setStateCallback(new FlightControllerState.Callback() {
                @Override
                public void onUpdate(FlightControllerState flightControllerState) {
                    if (flightControllerState != null) {
                        final float velocityX = flightControllerState.getVelocityX();
                        final float velocityY = flightControllerState.getVelocityY();
                        final float velocityZ = flightControllerState.getVelocityZ();
                        final String speedVal = decimalFormat.format(Math.sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                speed.setText(speedVal + "m/s");
                            }
                        });
                    }
                }
            });
        }
    }

    public void updateXYZ() {
        final DecimalFormat decimalFormat = new DecimalFormat("0.00000");

        if (ModuleVerificationUtil.isFlightControllerAvailable()) {
            DJISampleApplication.getAircraftInstance().getFlightController().setStateCallback(new FlightControllerState.Callback() {
                @Override
                public void onUpdate(FlightControllerState flightControllerState) {
                    if (flightControllerState != null) {
                        final float velocityX = flightControllerState.getVelocityX();
                        final float velocityY = flightControllerState.getVelocityY();
                        final float velocityZ = flightControllerState.getVelocityZ();


                        // Format velocity values using the DecimalFormat
                        final String formattedVelocityX = decimalFormat.format(velocityX);
                        final String formattedVelocityY = decimalFormat.format(velocityY);
                        final String formattedVelocityZ = decimalFormat.format(velocityZ);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                x.setText(formattedVelocityX);
                                y.setText(formattedVelocityY);
                                z.setText(formattedVelocityZ);
                            }
                        });
                    }
                }
            });
        }
    }

    public void updatePitchRollYaw() {
        if (ModuleVerificationUtil.isGimbalModuleAvailable()) {
            Gimbal gimbal = DJISampleApplication.getProductInstance().getGimbal();
            if (gimbal != null) {
                gimbal.setStateCallback(new GimbalState.Callback() {
                    @Override
                    public void onUpdate(@NonNull GimbalState gimbalState) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                float p = (float) gimbalState.getAttitudeInDegrees().getPitch();
                                float r = (float) gimbalState.getAttitudeInDegrees().getRoll();
                                float y = (float) gimbalState.getAttitudeInDegrees().getYaw();

                                pitch.setText(Float.toString(p));
                                roll.setText(Float.toString(r));
                                yaw.setText(Float.toString(y));
                            }
                        });
                    }
                });
            }
        }
    }

    public void updateLatitudeLongitude() {
        FlightController flightController = ModuleVerificationUtil.getFlightController();
        Runnable updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                if (flightController != null) {
                    LocationCoordinate3D aircraftLocation = flightController.getState().getAircraftLocation();
                    if (aircraftLocation != null) {
                        double lat = aircraftLocation.getLatitude();
                        double lon = aircraftLocation.getLongitude();
                        double alt = aircraftLocation.getAltitude();

                        latitude.setText(String.format(Locale.getDefault(), "%.6f", lat));
                        longitude.setText(String.format(Locale.getDefault(), "%.6f", lon));
                        altitude.setText(String.format(Locale.getDefault(), "%.6f", alt));
                    }
                }
                locationUpdateHandler.postDelayed(this, 100);
            }
        };
        updateTimeRunnable.run();
    }

    public String getBattery() {
        return battery.getText().toString();
    }

    public String getILMX() {
        return x.getText().toString();
    }

    public String getILMY() {
        return y.getText().toString();
    }

    public String getILMZ() {
        return z.getText().toString();
    }

    public String getLatitude() {
        return latitude.getText().toString();
    }

    public String getLongitude() {
        return longitude.getText().toString();
    }

    public String getAltitude() {
        return altitude.getText().toString();
    }

    public String getDate() {
        return date.getText().toString();
    }

    public String getSpeed() {
        return speed.getText().toString();
    }

    public String getDistance() {
        return distance.getText().toString();
    }

    public String getPitch() {
        return pitch.getText().toString();
    }

    public String getRoll() {
        return roll.getText().toString();
    }

    public String getYaw() {
        return yaw.getText().toString();
    }

}