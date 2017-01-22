package com.project.groupone.vehicletrackingsystem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.vision.text.Text;
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
        //view.setBackgroundResource(R.drawable.card_view_background);
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
            holder.mVIdView.setText("ID: " + values.get(position).get("VID"));
            holder.mvehicleName.setText("Name: " + values.get(position).get("Name"));
            holder.mModelNumber.setText(values.get(position).get("ModelNumber"));
            holder.mBrandName.setText("Model: " + values.get(position).get("BrandName"));
            holder.mEngineCC.setText( "Engine: " + values.get(position).get("EngineCC") + "L");

            if(!TextUtils.isEmpty(values.get(position).get("Image"))) {
                holder.mImageView.setImageBitmap(BitmapFactory.decodeFile(values.get(position).get("Image")));
            }


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
        public final TextView mVIdView;
        public final TextView mvehicleName;
        public final RoundedImageView mImageView;
        public final TextView mBrandName;
        public final TextView mModelNumber;
        public final TextView mEngineCC;
        public String mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mVIdView = (TextView) view.findViewById(R.id.vid);
            mvehicleName = (TextView) view.findViewById(R.id.vehicle_name);
            mBrandName = (TextView) view.findViewById(R.id.brandname);
            mModelNumber = (TextView) view.findViewById(R.id.modelnumber);
            mEngineCC = (TextView) view.findViewById(R.id.enginecc);
            mImageView = (RoundedImageView) view.findViewById(R.id.vehicle_pic);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mvehicleName.getText() + "'";
        }
    }
}
