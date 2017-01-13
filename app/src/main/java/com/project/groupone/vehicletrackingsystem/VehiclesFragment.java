package com.project.groupone.vehicletrackingsystem;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class VehiclesFragment extends Fragment {

    public VehiclesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vehicles, container, false);
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
/*        ImageButton ib=(ImageButton) getActivity().findViewById(R.id.pay);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast toast= Toast.makeText(getActivity().getApplicationContext(), "pay clicked nigga", Toast.LENGTH_LONG);
                toast.show();

               // ArticleFragment fraggg= new ArticleFragment();
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.my_main_container,fraggg).addToBackStack(null).commit();

            }
        });*/
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
