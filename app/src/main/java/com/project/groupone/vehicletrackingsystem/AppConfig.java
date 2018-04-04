package com.project.groupone.vehicletrackingsystem;

/**
 * Created by kidus on 1/2/17.
 */

public class AppConfig {
    // Server user login url
    public static String MAIN_URL = "http://10.24.29.253:8888/android_api/";
    public static String URL_LOGIN = MAIN_URL + "login.php";

    // Server user register url
    public static String URL_REGISTER = MAIN_URL  + "register.php";
    public static String URL_VEHICLES_DATA= MAIN_URL + "vehicles.php";
    public static String URL_LOCATION_DATA = MAIN_URL + "location.php";
    public static String URL_DRIVERS_DATA = MAIN_URL + "drivers.php";

}
