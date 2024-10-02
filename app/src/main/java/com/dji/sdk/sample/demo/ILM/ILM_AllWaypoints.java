package com.dji.sdk.sample.demo.ILM;

import android.content.Context;

import java.util.HashMap;

/**
 * The ILM_AllWaypoints class extends ILM_Waypoints and provides functionality
 * to manage all waypoints in a consolidated manner. It stores waypoints with
 * their latitude, longitude, and altitude in a structured format.
 */
public class ILM_AllWaypoints extends ILM_Waypoints {
    HashMap<String, String> waypoints;

    /**
     * Constructs an ILM_AllWaypoints instance with the specified context and status bar.
     *
     * @param context   The application context.
     * @param statusBar An instance of ILM_StatusBar to display status information.
     */
    public ILM_AllWaypoints(Context context, ILM_StatusBar statusBar) {
        super(context, statusBar);
        waypoints = new HashMap<>();
    }

    /**
     * Retrieves the waypoints from the superclass and sets all waypoints into
     * a single HashMap. The key is in the format "WaypointX", and the value is a
     * string containing the latitude, longitude, and altitude separated by commas.
     */
    public void setAllWaypoints() {
        HashMap<String, String> current_waypoints = super.getWaypoints();
        for (int i = 0; i < current_waypoints.size(); i++) {
            waypoints.put("Waypoint" + i, current_waypoints.get("Latitude" + i) + "," + current_waypoints.get("Longitude" + i) + "," + current_waypoints.get("Altitude" + i));
        }
    }

    /**
     * Returns a HashMap containing all the waypoints with their details.
     *
     * @return A HashMap where the keys are "WaypointX" (e.g., "Waypoint0") and
     * the values are strings with latitude, longitude, and altitude
     * separated by commas.
     */
    public HashMap<String, String> getAllWaypoints() {
        return waypoints;
    }
}