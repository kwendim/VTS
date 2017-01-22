package com.project.groupone.vehicletrackingsystem;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.GoogleMap;
import com.project.groupone.vehicletrackingsystem.helper.SQLiteHandler;
import com.project.groupone.vehicletrackingsystem.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NetworkCommunication extends IntentService {
    private SQLiteHandler db;
    private HashMap<String, String> user;
    private String vehiclestring;
    private CountDownLatch latch;
    private List<HashMap<String,String>> vehicles = new ArrayList<>();
    private static String pic_location,temp_holder,vehicle_id;
    private List<HashMap<String,String>> vehicles_list = new ArrayList<>();
    private static final String TAG = NetworkCommunication.class.getSimpleName();



    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.project.groupone.vehicletrackingsystem.action.FOO";
    private static final String ACTION_BAZ = "com.project.groupone.vehicletrackingsystem.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.project.groupone.vehicletrackingsystem.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.project.groupone.vehicletrackingsystem.extra.PARAM2";

    public NetworkCommunication() {
        super("NetworkCommunication");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NetworkCommunication.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        Log.d("Service", "Starting Action Foo");
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NetworkCommunication.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            db = new SQLiteHandler(this);

            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(final String param1, String param2) {

        Log.d("Service","Handling Foo");
        List<HashMap<String,String>> db_vehicles = db.getVehicleDetails();
        HashSet<String> vehicle_id_list = new HashSet<>();
        for(HashMap<String, String> vehicle : db_vehicles){
            vehicle_id_list.add(vehicle.get("VID"));
        }

        final StringRequest vehicledata= new StringRequest(Request.Method.POST, AppConfig.URL_VEHICLES_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                vehiclestring = response;
                latch.countDown();

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
/*                    Toast.makeText(getActivity().getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();*/
                latch.countDown();

            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("UID", param1);
                return pars;
            }

        };
        latch = new CountDownLatch(1);
        AppController.getInstance().addToRequestQueue(vehicledata, "getting location data");
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(!TextUtils.isEmpty(vehiclestring)){
            String VID,UID,GID,DID,BrandName,ModelNumber,Name,EngineCC,Color,Status;

            try {
                JSONObject jObj;
                JSONArray jsonArray = new JSONArray(vehiclestring);
                for (int x = 0; x < jsonArray.length(); x++) {
                    jObj = jsonArray.getJSONObject(x);
                    VID = jObj.getString("VID");
                    Status = jObj.getString("Status");


                    if (vehicle_id_list.contains(VID) ){
                        vehicle_id_list.remove(VID);
                        continue;
                    }

/*                        for (int i = 0 ; i < db_vehicles.size(); i++){
                            if(VID.equals(db_vehicles.get(i).get("VID"))) {
                                HashMap<String,String> data = db_vehicles.get(i);
                                db_vehicles.remove(i);
                                break;
                            }
                        }
*/

                    UID = param1;
                    GID = jObj.getString("GID");
                    DID = jObj.getString("DID");
                    BrandName = jObj.getString("BrandName");
                    ModelNumber = jObj.getString("ModelNumber");
                    Name = jObj.getString("Name");
                    pic_location = jObj.getString("Image");
                    EngineCC = jObj.getString("EngineCC");
                    Color = jObj.getString("Color");
                    db.addVehicle(VID,UID,GID,DID,BrandName,ModelNumber,EngineCC,Color,pic_location,Name,Status);

                    if (!TextUtils.isEmpty(pic_location)){
                        String url = AppConfig.MAIN_URL + pic_location;
                        temp_holder = pic_location;
                        vehicle_id = VID;

                        ImageRequest request = new ImageRequest(url,
                                new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap bitmap) {

                                        String[] name = temp_holder.split("/");
                                        String file_name = name[name.length - 1];
                                        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                        File file = new File(path, file_name);
                                        try {
                                            FileOutputStream fos= new FileOutputStream(file);
                                            bitmap.compress(Bitmap.CompressFormat.PNG ,90,fos);
                                            fos.close();
                                            Log.d("HereIs", vehicle_id + ", " + file_name);
                                            db.getVehicleDetails();
                                            db.insertVehiclePhoto(file.getAbsolutePath(),vehicle_id);
                                            latch.countDown();

                                        } catch (FileNotFoundException e) {
                                            Log.e(TAG,"File NOT FOUND" + e.getMessage());
                                            e.printStackTrace();
                                            latch.countDown();
                                        } catch (IOException e) {
                                            Log.e(TAG,"IO PROBLEM" + e.getMessage());
                                            e.printStackTrace();
                                            latch.countDown();
                                        }

                                    }
                                }, 0, 0, null,
                                new Response.ErrorListener() {
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e(TAG, "Problem loading image: " + error.getMessage());
                                        Toast.makeText(getApplicationContext(),
                                                error.getMessage(), Toast.LENGTH_LONG).show();
                                        latch.countDown();

                                    }
                                });
                        latch = new CountDownLatch(1);
                        AppController.getInstance().addToRequestQueue(request, "profile pic request");
                        try{
                            latch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    }
                }

                if(vehicle_id_list.isEmpty()){
                    Log.d("Here", "NOTHIN IN THIS BITCH NO MORE");
                }
                else {
                    for(String vid : vehicle_id_list){
                        db.removeVehicle(vid);

                    }
                }

            } catch (JSONException e) {
                Log.d("JSON ERROR: ", e.getMessage());
            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
