package com.project.groupone.vehicletrackingsystem;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.android.gms.maps.*;
import com.project.groupone.vehicletrackingsystem.helper.SQLiteHandler;
import com.project.groupone.vehicletrackingsystem.helper.SessionManager;
import java.io.File;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private GoogleMap mMap;
    private static final String TAG = MainActivity.class.getSimpleName();
    private SQLiteHandler db;
    private SessionManager session;
    private HashMap<String, String> user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        db.deleteUsers();
        File file = new File(user.get("Photo"));
        file.delete();


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
            transaction.replace(R.id.my_main_container, newFragment);
            transaction.commit();


            // Handle the camera action
        } else if (id == R.id.nav_vehicles) {

            //Fragment fragment = new BlankFragment();
            //Bundle args = new Bundle();
            //fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.map, fragment)
//                    .commit();
            VehiclesFragment newFragment = new VehiclesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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


}
