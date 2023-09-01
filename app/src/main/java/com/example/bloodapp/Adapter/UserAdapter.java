package com.example.bloodapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bloodapp.R;
import com.example.bloodapp.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context context;
    private List<User> userList;
    private RecyclerViewClickListener listener;

    public UserAdapter(Context context, List<User> userList,RecyclerViewClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(
               R.layout.user_display_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user=userList.get(position);
         holder.type.setText(user.getType());
//        if(user.getType().equals("donor")){
//            holder.email_btn.setVisibility(View.VISIBLE);
//        }
        holder.fullname.setText(user.getName());
       // holder.phoneNumber.setText(user.getPhonenumber());
     //   holder.bloodgroup.setText(user.getBloodgroup());
        holder.email.setText(user.getEmail());
        if(user.getProfilepictureuri()!=null){
            Glide.with(context).load(user.getProfilepictureuri().toString()).into(holder.profile_image);
        }else{
            holder.profile_image.setImageResource(R.drawable.profile_picture);
        }

//        holder.profile_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context,DonorOrRecipientActivity.class);
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView profile_image;
        private TextView fullname, email, bloodgroup, type, phoneNumber;
        private Button  email_btn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image=itemView.findViewById(R.id.User_image);
            fullname=itemView.findViewById(R.id.user_name);
            email=itemView.findViewById(R.id.user_email);
           // bloodgroup=itemView.findViewById(R.id.user_bloodGroup);
            type=itemView.findViewById(R.id.user_type);
            //phoneNumber=itemView.findViewById(R.id.user_Number);
           // email_btn=itemView.findViewById(R.id.email_me);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view,getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view,int pos);
    }
}
