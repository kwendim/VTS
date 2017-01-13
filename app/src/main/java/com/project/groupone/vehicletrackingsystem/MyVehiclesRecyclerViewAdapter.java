package com.project.groupone.vehicletrackingsystem;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.groupone.vehicletrackingsystem.VehiclesFragment.OnListFragmentInteractionListener;

import java.util.HashMap;
import java.util.List;


public class MyVehiclesRecyclerViewAdapter extends RecyclerView.Adapter<MyVehiclesRecyclerViewAdapter.ViewHolder> {
    private final List<HashMap<String, String>> values;
    private final OnListFragmentInteractionListener mListener;


    public MyVehiclesRecyclerViewAdapter(List<HashMap<String,String>> items, OnListFragmentInteractionListener listener) {
        values = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_vehicles2, parent, false);
        view.setBackgroundResource(R.drawable.card_view_background);
        return new ViewHolder(view);
    }

/*    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }*/

        @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = values.get(position).get("VID");
        holder.mIdView.setText(values.get(position).get("Name"));
        holder.mContentView.setText(values.get(position).get("BrandName"));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
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
        public final TextView mIdView;
        public final TextView mContentView;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
