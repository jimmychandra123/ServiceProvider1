package com.example.fungus.serviceprovider1;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fungus.serviceprovider1.model.Booking;
import com.example.fungus.serviceprovider1.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CustomAdapterBookingList extends RecyclerView.Adapter<CustomAdapterBookingList.ViewHolder> {
    List<Booking> listBooking;
    OnItemClickListener listener;
    int userType;

    public CustomAdapterBookingList(List<Booking> listBooking, OnItemClickListener listener,int userType) {
        this.listBooking = listBooking;
        this.listener = listener;
        this.userType = userType;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomAdapterBookingList.ViewHolder holder, final int position) {
        holder.bind(listBooking.get(position),listener);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        if(userType==0) {
            db.child("users").child(listBooking.get(position).getSp_id()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Booking booking = listBooking.get(position);
                    holder.txtName.setText(user.getName());
                    holder.txtDate.setText(String.valueOf(booking.getDate()));
                    holder.txtTime.setText(booking.getTime());
                    holder.txtStatus.setText(booking.getStatus());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            db.child("users").child(listBooking.get(position).getU_id()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Booking booking = listBooking.get(position);
                    holder.txtName.setText(user.getName());
                    holder.txtDate.setText(String.valueOf(booking.getDate()));
                    holder.txtTime.setText(booking.getTime());
                    holder.txtStatus.setText(booking.getStatus());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

//        holder.txtStatus.setText(booking.getStatus());
    }

    public interface OnItemClickListener {
        void onItemClick(Booking item);
    }

    @Override
    public int getItemCount() {
        return listBooking.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtDate, txtTime, txtStatus;
        private String mItem;

        public ViewHolder(View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.sBookingName);
            txtDate = itemView.findViewById(R.id.sBookingDate);
            txtTime = itemView.findViewById(R.id.sBookingTime);
            txtStatus = itemView.findViewById(R.id.sBookingStatus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("click",txtName.getText().toString());
                }
            });
        }

        public void bind(final Booking item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        public void setItem(String item) {
            mItem = item;
            txtName.setText(item);
        }

    }

}


