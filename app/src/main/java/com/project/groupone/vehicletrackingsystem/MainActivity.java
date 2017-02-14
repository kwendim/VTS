package com.project.groupone.vehicletrackingsystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.*;
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
import java.util.List;
import java.util.Map;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import static com.project.groupone.vehicletrackingsystem.VehiclesFragment.*;
//import static com.project.groupone.vehicletrackingsystem.NetworkCommunication.*;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VehiclesFragment.OnListFragmentInteractionListener,DriversFragment.OnListFragmentInteractionListener {


    private GoogleMap mMap;
    private static final String TAG = MainActivity.class.getSimpleName();
    private SQLiteHandler db;
    private SessionManager session;
    private HashMap<String, String> user;
    private Toolbar toolbar;
    private String vehiclestring;
    private CountDownLatch latch;
    private List<HashMap<String,String>> vehicles = new ArrayList<>();
    private static String pic_location,temp_holder,vehicle_id;
    private List<HashMap<String,String>> vehicles_list = new ArrayList<>();
    private List<HashMap<String,String>> drivers = new ArrayList<>();
    private NavigationView navigationView;
    TimerTask doAsyncServiceTask;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Vectra VTS");
        setSupportActionBar(toolbar);


        //SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        user = db.getUserDetails();

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }
//        getVehicles vehicleGetter= new getVehicles();
//        vehicleGetter.execute();

        NetworkCommunication.startActionGetVehicles(this, user.get("UID"), "litlite");
        NetworkCommunication.startActionGetDrivers(this, user.get("UID"), "litlite");


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar.make(v, "Snackbar clicked", Snackbar.LENGTH_SHORT).show();
//            }
//        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        navigationView.setCheckedItem(R.id.nav_dashboard);
        TextView username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.usernametextview);
        TextView email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.emailtextview);
        RoundedImageView profile_pic = (RoundedImageView) navigationView.getHeaderView(0).findViewById(R.id.profilepic);

        username.setText(user.get("FName") + " " + user.get("MName")+ " " + user.get("LName") );
        email.setText(user.get("Email"));
        if(!TextUtils.isEmpty(user.get("Photo"))){
            profile_pic.setImageBitmap(BitmapFactory.decodeFile(user.get("Photo")));
        }

        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_dashboard));
        callAsyncServicetask();


    }

    private void logoutUser() {
        doAsyncServiceTask.cancel();
        session.setLogin(false);
        File file = new File(user.get("Photo"));
        file.delete();
        vehicles = db.getVehicleDetails();
        for (int i = 0 ; i < vehicles.size(); i++){
            file = new File(vehicles.get(i).get("Image"));
            file.delete();
        }
        drivers = db.getDriversDetails();
        for (int i = 0 ; i < drivers.size(); i++){
            file = new File(drivers.get(i).get("Photo"));
            file.delete();
        }
       db.deleteUsers();





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

        if (id == R.id.nav_dashboard) {
            MapFragment newFragment = new MapFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            toolbar.setTitle("Vectra VTS");
            transaction.replace(R.id.my_main_container, newFragment);
            transaction.commit();


            // Handle the camera action
        } else if (id == R.id.nav_vehicles) {

            VehiclesFragment newFragment = new VehiclesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            toolbar.setTitle("Vehicles");
            transaction.replace(R.id.my_main_container, newFragment);
            transaction.commit();


        } else if (id == R.id.nav_drivers) {
            DriversFragment newFragment = new DriversFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            toolbar.setTitle("Drivers");
            transaction.replace(R.id.my_main_container, newFragment);
            transaction.commit();

        }  else if (id == R.id.nav_help) {
            Intent help_intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(help_intent);

        } else if (id == R.id.nav_about) {
            Intent about_us = new Intent(MainActivity.this, AboutUsActivity.class);
            startActivity(about_us);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onListFragmentInteraction(String fragmentTAG, String item) {
        Log.d("just pressed", item);
        if(fragmentTAG.equals(DriversFragment.class.getSimpleName())){
            Log.d("just in", fragmentTAG);

        }
        else if (fragmentTAG.equals(VehiclesFragment.class.getSimpleName())){
            Log.d("just in", fragmentTAG);
            MapFragment newFragment = MapFragment.newInstance(item);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            toolbar.setTitle("Vehicles");
            navigationView.setCheckedItem(R.id.nav_dashboard);
            transaction.replace(R.id.my_main_container, newFragment);
            transaction.commit();

        }


    }
    public void callAsyncServicetask(){
        final Handler handler = new Handler();
        timer = new Timer();
        doAsyncServiceTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            NetworkCommunication.startActionGetVehicles(MainActivity.this, user.get("UID"), "litlite");
                            NetworkCommunication.startActionGetDrivers(MainActivity.this, user.get("UID"), "litlite");

                        }
                        catch (Exception e){
                            Log.e("AysncTaskException", e.getMessage());
                            //Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsyncServiceTask,0,10000);
    }



}
