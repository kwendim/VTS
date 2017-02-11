package com.project.groupone.vehicletrackingsystem;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.groupone.vehicletrackingsystem.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_VEHICLE = null;

    // TODO: Rename and change types of parameters
    private String single_vehicle;
    private GoogleMap mMap;
    private static final String TAG = MapFragment.class.getSimpleName();
    private HashMap<String, String> user;
    private String locationString = "";
    private CountDownLatch latch;
    TimerTask doAsyncTask;
    private Timer timer;
    private SQLiteHandler db;
    private MapFragment.getLocation locationupdate;
    public List<MarkerOptions> marker_array = new ArrayList<>();
    private NavigationView navigationView;
    private boolean isJustBeingLoaded = true;




    public MapFragment() {
        // Required empty public constructor
    }



    public static MapFragment newInstance(String param1) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VEHICLE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            single_vehicle = getArguments().getString(ARG_VEHICLE);
        }
        db = new SQLiteHandler(getActivity().getApplicationContext());

        user = db.getUserDetails();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.content_main, container, false);
        SupportMapFragment mapfrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapfrag.getMapAsync(this);
//        SupportMapFragment mapFragment = ((SupportMapFragment) getFragmentManager()
//                .findFragmentById(R.id.map)).;
//        Toast.makeText(getActivity().getApplicationContext(),mapFragment.getTag(), Toast.LENGTH_LONG).show();
//        mapFragment.getMapAsync(this);
        return v;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //callAsynctask();
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
//                String t = marker.getId();
//                if(t.equals("m0")){
//                     Toast.makeText(getActivity().getApplicationContext(), "you pressed " + marker.getId()  , Toast.LENGTH_SHORT).show();
//
//                } else {
//                    Toast.makeText(getActivity().getApplicationContext(), "you done messed up nigga", Toast.LENGTH_SHORT).show();
//                }

                return false;
            }
        });

    }

    public class getLocation extends AsyncTask<Void, Void, List<MarkerOptions>> {


        @Override
        protected List<MarkerOptions> doInBackground(Void... params) {
            marker_array = new ArrayList<>();

            StringRequest locationdata = new StringRequest(Request.Method.POST, AppConfig.URL_LOCATION_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    locationString = response;
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
                    pars.put("UID", user.get("UID"));
                    return pars;
                }

            };
            latch = new CountDownLatch(1);
            AppController.getInstance().addToRequestQueue(locationdata, "getting location data");
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d("isCancelled", String.valueOf(isCancelled()));

            if(!TextUtils.isEmpty(locationString)){

                String GID,Lat,Lon,Bearing,Time;
                List<HashMap<String,String>> vehicles = db.getVehicleDetails();

                try {
                    HashMap<String,String> data = null;
                    JSONObject jObj;
                    JSONArray jsonArray = new JSONArray(locationString);
                    Log.d("theString",locationString);
                    LatLng place = null;
                    for (int x = 0; x < jsonArray.length(); x++) {
                        jObj = jsonArray.getJSONObject(x);

                        GID = jObj.getString("GID");
                        for (int i = 0 ; i < vehicles.size(); i++){
                            if(GID.equals(vehicles.get(i).get("GID"))) {
                                data = vehicles.get(i);
                                vehicles.remove(i);
                                break;
                            }
                        }

                        Lat = jObj.getString("Lat");
                        Lon = jObj.getString("Lon");
                        Bearing = jObj.getString("Bearing");
                        Time = jObj.getString("Time");
                        if (Lat == "null" || Lon == "null"){
                            continue;
                        }
                        place = new LatLng(Double.valueOf(Lat), Double.valueOf(Lon));

                        int height = 150;
                        int width = 150;

                        MarkerOptions mark = new MarkerOptions().position(place).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)).title(GID);
                        if (data != null) {
                            mark.snippet("Vehicle: " + data.get("VID") + " Name: " + data.get("Name"));
                        } else {
                            mark.snippet("Fetching Data");
                        }
                        marker_array.add(mark);
                    }

                } catch (JSONException e) {
                    Log.d("JSON ERROR: ", e.getMessage());
                }
            }
            return marker_array;

        }


        @Override
        protected void onPostExecute (List<MarkerOptions> response){
            //Toast.makeText(getActivity().getApplicationContext(), "here:" , Toast.LENGTH_LONG).show();
            if (isAdded()) {
                ArrayList<Marker> markers = new ArrayList<>();
                mMap.clear();
                for (MarkerOptions marker : response) {
                    markers.add(mMap.addMarker(marker));
                }
               // boolean lash = false;

                //Log.e("ISVALUE", single_vehicle);
                if (isJustBeingLoaded) {
                    if (single_vehicle != null) {
                        for (Marker marker : markers) {
                            if (marker.getTitle().equals(single_vehicle)) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14.5f));
                                marker.showInfoWindow();

                            }
                        }


                    } else {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        boolean isMarkerAvailable = false;
                        for (Marker marker : markers) {
                            builder.include(marker.getPosition());
                            isMarkerAvailable = true;
                        }
                        if (isMarkerAvailable) {
                            LatLngBounds bounds = builder.build();
                            int padding = 100;
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.animateCamera(cu);
                        }


                    }
                    isJustBeingLoaded = false;
                }
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(9.0405910,38.7620650)), 10.5f));
                Log.d("UpdateStatus", "Updated");

            }


        }

        @Override
        protected void onCancelled(List<MarkerOptions> lash) {
            super.onCancelled();
            Log.d("OnCANCELLED CALLED", "YES");
        }

    }

    public void callAsynctask(){
        final Handler handler = new Handler();
        timer = new Timer();
        doAsyncTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            locationupdate = new MapFragment.getLocation();
                            locationupdate.execute();
                        }
                        catch (Exception e){
                            Log.e("AysncTaskException", e.getMessage());
                            //Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsyncTask,0,10000);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(doAsyncTask == null){
            callAsynctask();
            Log.d("isresumed", "true");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        doAsyncTask.cancel();
        //locationupdate.cancel(true);
        //Toast.makeText(getActivity().getApplicationContext(), "Detaching" ,Toast.LENGTH_SHORT).show();
    }

}
