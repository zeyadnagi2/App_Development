package com.example.bloodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.bloodapp.databinding.ActivityDonorOrRecipientAvtivityBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonorOrRecipientActivity extends AppCompatActivity {

    ActivityDonorOrRecipientAvtivityBinding binding;
    private String smail;
    private Toolbar toolbar;
    private TextView type ,name,userId,userNumber,bloodGroup,mail;
    private CircleImageView circleImageView;
    private Button back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_donor_or_recipient_avtivity);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Person Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String userID = "";
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            userID = extras.getString("id");
        }

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("users").child(
                userID
        );



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.type.setText(snapshot.child("type").getValue().toString());
                binding.name.setText(snapshot.child("name").getValue().toString());
                binding.userId.setText(snapshot.child("idnumber").getValue().toString());
                binding.userNumber.setText(snapshot.child("phonenumber").getValue().toString());
                smail = snapshot.child("email").getValue().toString();
                binding.email.setText(snapshot.child("email").getValue().toString());
                binding.bloodGroup.setText(snapshot.child("bloodgroup").getValue().toString());
                if(snapshot.hasChild("profilepictureuri")){
                    Glide.with(getApplicationContext()).load(snapshot.child("profilepictureuri").
                            getValue().toString()).into(binding.profileImage);
                }else{
                    binding.profileImage.setImageResource(R.drawable.profile_picture);
                }
//

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {;

                sendEmail(smail,"Blood Donation".toString().trim());
            }
        });

    }

    private void sendEmail(String recipient , String subject){
        Intent mailIntent=new Intent(Intent.ACTION_SEND);
        mailIntent.setData(Uri.parse("mailto:"));
        mailIntent.setType("text/plain");
        mailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{smail});
        mailIntent.putExtra(Intent.EXTRA_SUBJECT,subject);


        try{
            startActivity(mailIntent);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}