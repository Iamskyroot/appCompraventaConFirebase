package com.example.compraeintercambia.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.compraeintercambia.R;
import com.example.compraeintercambia.model.User;

import java.util.ArrayList;
import java.util.List;

public class RepairAdapter extends RecyclerView.Adapter<RepairAdapter.RAHolder> {

    List<User> userList;
    User user;

    public RepairAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public RAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_repair,parent,false);
        return new RAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RAHolder holder, int position) {
        holder.imprimir(position);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class RAHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvuserType,tvCounter,tvShowDetail,tvCloseDetails, tvDescription;
        LinearLayout hiddenLayout;
        ImageView like;
        int counter = 0;
        public RAHolder(@NonNull View itemView) {
            super(itemView);

            tvName=itemView.findViewById(R.id.tvUserName);
            tvuserType=itemView.findViewById(R.id.tvUserTel);
//            tvCounter=itemView.findViewById(R.id.tvCounter);
            tvShowDetail=itemView.findViewById(R.id.tvShowDetails);
            tvDescription=itemView.findViewById(R.id.tvRepairDescription);
            tvCloseDetails=itemView.findViewById(R.id.tvCloseDetails);
            hiddenLayout=itemView.findViewById(R.id.hidenLayout);
            /*like=itemView.findViewById(R.id.ivLike);*/

            /*like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user = userList.get(getLayoutPosition());
                    counter += 1;
                }
            });*/
            tvShowDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hiddenLayout.setVisibility(View.VISIBLE);
                    tvShowDetail.setVisibility(View.GONE);
                    tvCloseDetails.setText("Ver menos");
                    tvCloseDetails.setVisibility(View.VISIBLE);
                }
            });
            tvCloseDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hiddenLayout.setVisibility(View.GONE);
                    tvShowDetail.setVisibility(View.VISIBLE);
                    tvCloseDetails.setText("Ver menos");
                    tvCloseDetails.setVisibility(View.GONE);
                }
            });

        }

        public void imprimir(int position) {

            tvName.setText(userList.get(position).getName());
            tvuserType.setText("Usuario técnico");
            tvDescription.setText("\nInformacion de lo que repara.\n "+userList.get(position).getEspecification());
            /*tvCounter.setText(String.valueOf(counter));*/

        }
    }
}
