# DJI Drone Autopilot Android Application

This application is primarily designed for creating and executing **waypoint missions** for drones, including importing waypoints and navigating to them.

# Functionalities:
- Take Off
- Land
- Stop
- Record the Flight
- Manual Flight Control using Virtual Sticks

The application displays a **mini-map** and a **status bar** that shows all the necessary parameters for the drone: **Battery, Speed, X, Y, Z, Pitch, Roll, Yaw, Time & Date, Latitude, Longitude, and Altitude**.

The flight data is saved to a **CSV** file on the Android device, containing all the above parameters.

# How To Run:
1) Download the released version file and extract it.
2) Open Android Studio (currently using Android Studio Koala | 2024.1.1 Patch 2).
3) Open DJI_Mobile_SDK_Android_V4-1.0.0.
4) Change Gradle JDK to 15.0.2 (In Settings -> Build, Execution, Deployment -> Build Tools -> Gradle).
5) Gradle Version 6.7.1 and Android Gradle Plugin Version 4.2.2 (In Project Structure -> Project).
6) Plug in your Android device and run the code.
7) The interface is named ILM Remote Controller.
