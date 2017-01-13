package com.project.groupone.vehicletrackingsystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.project.groupone.vehicletrackingsystem.dummy.DummyContent;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import static com.project.groupone.vehicletrackingsystem.VehiclesFragment.*;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnListFragmentInteractionListener {


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
        getVehicles vehicleGetter= new getVehicles();
        vehicleGetter.execute();
       // List<HashMap<String,String>> lash = db.getVehicleDetails();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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


    }

    private void logoutUser() {
        session.setLogin(false);
        File file = new File(user.get("Photo"));
        file.delete();
        vehicles = db.getVehicleDetails();
        for (int i = 0 ; i < vehicles.size(); i++){
            file = new File(vehicles.get(i).get("Image"));
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

        } else if (id == R.id.nav_reports) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onListFragmentInteraction(String item) {
        Log.d("just pressed", item);


    }

    public class getVehicles extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {


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
                    pars.put("UID", user.get("UID"));
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
                String VID,UID,GID,BrandName,ModelNumber,Name,EngineCC,Color,Status;
                List<HashMap<String,String>> db_vehicles = db.getVehicleDetails();


                try {
                    JSONObject jObj;
                    JSONArray jsonArray = new JSONArray(vehiclestring);
                    for (int x = 0; x < jsonArray.length(); x++) {
                        jObj = jsonArray.getJSONObject(x);
                        VID = jObj.getString("VID");
                        //TODO: Implement check on already existing database data and make changes

/*                        for (int i = 0 ; i < db_vehicles.size(); i++){
                            if(VID.equals(db_vehicles.get(i).get("VID"))) {
                                HashMap<String,String> data = db_vehicles.get(i);
                                db_vehicles.remove(i);
                                break;
                            }
                        }
                        */

                        UID = user.get("UID");
                        GID = jObj.getString("GID");
                        BrandName = jObj.getString("BrandName");
                        ModelNumber = jObj.getString("ModelNumber");
                        Name = jObj.getString("Name");
                        pic_location = jObj.getString("Image");
                        EngineCC = jObj.getString("EngineCC");
                        Color = jObj.getString("Color");
                        Status = jObj.getString("Status");
                        db.addVehicle(VID,UID,GID,BrandName,ModelNumber,EngineCC,Color,pic_location,Name,Status);

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

                } catch (JSONException e) {
                    Log.d("JSON ERROR: ", e.getMessage());
                }
            }
            return null;

        }



    }
}
