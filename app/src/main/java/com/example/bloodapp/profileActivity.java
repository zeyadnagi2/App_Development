package com.example.bloodapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.bloodapp.databinding.ActivityProfileBinding;
import com.example.bloodapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;

    private Toolbar toolbar;
    private TextView type, name, userId, userNumber, bloodGroup, mail;
    private CircleImageView circleImageView;
    private Button back_btn;
    private User user;
    private DatabaseReference reference, imgRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("User Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid());
        imgRef = FirebaseDatabase.getInstance().getReference().child("profilepictureuri");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.type.setText(snapshot.child("type").getValue().toString());
                binding.name.setText(snapshot.child("name").getValue().toString());
                binding.userId.setText(snapshot.child("idnumber").getValue().toString());
                binding.userNumber.setText(snapshot.child("phonenumber").getValue().toString());
                binding.email.setText(snapshot.child("email").getValue().toString());
                binding.bloodGroup.setText(snapshot.child("bloodgroup").getValue().toString());
                if (snapshot.hasChild("profilepictureuri")) {
                    Glide.with(getApplicationContext()).load(snapshot.child("profilepictureuri").
                            getValue().toString()).into(binding.profileImage);
                } else {
                    binding.profileImage.setImageResource(R.drawable.profile_picture);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(profileActivity.this,UpdateUserInfo.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
