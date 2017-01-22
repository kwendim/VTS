package com.project.groupone.vehicletrackingsystem;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.groupone.vehicletrackingsystem.DriversFragment.OnListFragmentInteractionListener;

import java.util.HashMap;
import java.util.List;


public class MyDriversRecyclerViewAdapter extends RecyclerView.Adapter<MyDriversRecyclerViewAdapter.ViewHolder> {

    private final List<HashMap<String, String>> values;
    private final DriversFragment.OnListFragmentInteractionListener driverlistener;

    public MyDriversRecyclerViewAdapter(List<HashMap<String,String>> items, OnListFragmentInteractionListener listener) {
        values = items;
        driverlistener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_drivers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = values.get(position).get("DID");
        holder.mDIdView.setText("DID: " + values.get(position).get("DID"));
        holder.mDriverName.setText("Name: " + values.get(position).get("FName") + " " +values.get(position).get("MName") + " " + values.get(position).get("LName"));
        holder.mTel.setText(values.get(position).get("Tel"));
        holder.mIsAssigned.setText("IsAssigned: " + values.get(position).get("IsAssigned"));

        if(!TextUtils.isEmpty(values.get(position).get("Photo"))) {
            Log.d("imageLocation", values.get(position).get("Photo"));
            holder.mImageView.setImageBitmap(BitmapFactory.decodeFile(values.get(position).get("Photo")));
        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != driverlistener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    driverlistener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDIdView;
        public final TextView mDriverName;
        public final RoundedImageView mImageView;
        public final TextView mTel;
        public final TextView mIsAssigned;
        public String mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mDIdView = (TextView) view.findViewById(R.id.did);
            mDriverName = (TextView) view.findViewById(R.id.driver_name);
            mTel = (TextView) view.findViewById(R.id.driver_tel);
            mIsAssigned = (TextView) view.findViewById(R.id.isassigned);
            mImageView = (RoundedImageView) view.findViewById(R.id.driver_pic);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDriverName.getText() + "'";
        }
    }
}
