package com.example.fungus.serviceprovider1;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fungus.serviceprovider1.model.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
        private List<Message> listMessage;
        private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();

public MessageAdapter(List<Message> listMessage) {
        this.listMessage = listMessage;
        }



@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    if (viewType == 0) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
        return new ViewHolder(view);
    }else{
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
        return new ViewHolder(view);
    }
}

@Override
public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, final int position) {
        Message message = listMessage.get(position);
        holder.txtMessage.setText(String.valueOf(message.getMessage()));
        }


    @Override
    public int getItemCount() {
        return listMessage.size();
    }

    @Override
    public int getItemViewType(int position) {
        String id = firebaseAuth.getCurrentUser().getUid();
        if(listMessage.get(position).getSender().equals(id)){
            return 0;
        }else{
            return 1;
        }
//        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
    TextView txtMessage;
    private String mItem;

    public ViewHolder(View itemView) {
        super(itemView);

        txtMessage = itemView.findViewById(R.id.showMessage);
    }

    public void setItem(String item) {
        mItem = item;
        txtMessage.setText(item);
    }

}

}