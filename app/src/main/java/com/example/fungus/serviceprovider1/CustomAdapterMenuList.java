package com.example.fungus.serviceprovider1;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fungus.serviceprovider1.model.Service;

import java.util.List;

public class CustomAdapterMenuList extends RecyclerView.Adapter<CustomAdapterMenuList.ViewHolder> {
    List<Service> listService;
    OnItemClickListener listener;

    public CustomAdapterMenuList(List<Service> listService, OnItemClickListener listener) {
        this.listService = listService;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sp_menu_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapterMenuList.ViewHolder holder, final int position) {
        holder.bind(listService.get(position),listener);
        Service service = listService.get(position);
        holder.txtName.setText(service.getS_name());
        holder.txtType.setText(String.valueOf(service.getS_state()));
        holder.txtState.setText(service.getS_type());
    }

    public interface OnItemClickListener {
        void onItemClick(Service item);
    }

    @Override
    public int getItemCount() {
        return listService.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtType, txtState;
        private String mItem;

        public ViewHolder(View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.sMenuName);
            txtType = itemView.findViewById(R.id.sMenuType);
            txtState = itemView.findViewById(R.id.sMenuState);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("asd",txtName.getText().toString());
                }
            });
        }

        public void bind(final Service item, final OnItemClickListener listener) {
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

