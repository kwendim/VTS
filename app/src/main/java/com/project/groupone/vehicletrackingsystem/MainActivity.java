package com.project.groupone.vehicletrackingsystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;
import com.project.groupone.vehicletrackingsystem.helper.SQLiteHandler;
import com.project.groupone.vehicletrackingsystem.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , OnMapReadyCallback {


    private GoogleMap mMap;
    private static final String TAG = MainActivity.class.getSimpleName();
    private SQLiteHandler db;
    private SessionManager session;
    private HashMap<String, String> user;
    private String locationString = "";
    private CountDownLatch latch;
    TimerTask doAsyncTask;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Vectra VTS");
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        user = db.getUserDetails();

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.usernametextview);
        TextView email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.emailtextview);
        RoundedImageView profile_pic = (RoundedImageView) navigationView.getHeaderView(0).findViewById(R.id.profilepic);

        username.setText(user.get("FName") + " " + user.get("MName")+ " " + user.get("LName") );
        email.setText(user.get("Email"));
        if(!TextUtils.isEmpty(user.get("Photo"))){
            profile_pic.setImageBitmap(BitmapFactory.decodeFile(user.get("Photo")));

        }

    }

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        File file = new File(user.get("Photo"));
        file.delete();
        doAsyncTask.cancel();



        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        callAsynctask();
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String t = marker.getId();
                if(t.equals("m0")){
                    Toast.makeText(getApplicationContext(), "you pressed " + marker.getId()  , Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "you done messed up nigga", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });





    }

    public class getLocation extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {

            StringRequest locationdata = new StringRequest(Method.POST, AppConfig.URL_LOCATION_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    locationString = response;
                    latch.countDown();

                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Login Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
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
            return locationString;
        }


        @Override
            protected void onPostExecute (String response){
            //Toast.makeText(getApplicationContext(), "here: " + response, Toast.LENGTH_LONG).show();
            mMap.clear();

            if(!TextUtils.isEmpty(response)){

                String GID,Lat,Lon,Bearing,Time;

                try {
                    JSONObject jObj;
                    JSONArray jsonArray = new JSONArray(response);
                    LatLng place = null;
                    for (int x = 0; x < jsonArray.length(); x++) {
                        jObj = jsonArray.getJSONObject(x);

                        GID = jObj.getString("GID");
                        Lat = jObj.getString("Lat");
                        Lon = jObj.getString("Lon");
                        Bearing = jObj.getString("Bearing");
                        Time = jObj.getString("Time");
                        place = new LatLng(Double.valueOf(Lat), Double.valueOf(Lon));
                        int height = 150;
                        int width = 150;
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.marker);
                        Bitmap b = bitmapDrawable.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                        Marker mark = mMap.addMarker(new MarkerOptions().position(place).title(GID));
                        mark.setSnippet("Wazneeeep");
                        mark.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                    }
                    Toast.makeText(getApplicationContext(),"updated",Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    Log.d("JSON ERROR: ", e.getMessage());
                }
            }


        }

    }

    public void callAsynctask(){
        final Handler handler = new Handler();
        Timer timer = new Timer();
        doAsyncTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getLocation locationupdate = new getLocation();
                            locationupdate.execute();
                        }
                        catch (Exception e){
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsyncTask,0,10000);
    }

}
