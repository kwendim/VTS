package com.project.groupone.vehicletrackingsystem;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.groupone.vehicletrackingsystem.helper.SQLiteHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DriversFragment extends Fragment {

    private DriversFragment.OnListFragmentInteractionListener driverlistener;
    private List<HashMap<String,String>> drivers_list = new ArrayList<>();
    private String driverstring;
    private CountDownLatch latch;
    private static final String TAG = DriversFragment.class.getName();
    private HashMap<String,String> drivers = new HashMap<String, String>();
    private RecyclerView recyclerView;
    private SQLiteHandler db;
    private static String pic_location,temp_holder;
    private HashMap<String,String> user;



    private static final String ARG_COLUMN_COUNT = "column-count";


    public DriversFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DriversFragment newInstance(int columnCount) {
        DriversFragment fragment = new DriversFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getActivity().getApplicationContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drivers_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            List<HashMap<String,String>> db_drivers = db.getDriversDetails();
            recyclerView.setAdapter(new MyDriversRecyclerViewAdapter(db_drivers, driverlistener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            driverlistener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        driverlistener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(String item);
    }

}
