package com.example.bloodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.bloodapp.Adapter.UserAdapter;
import com.example.bloodapp.databinding.ActivityMainBinding;
import com.example.bloodapp.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    ActivityMainBinding binding;

    private DatabaseReference mRef;
    private CircleImageView nav_profile_image;
    private TextView nav_fullname,nav_email,nav_bloodgroup,nav_type;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<User> userList;
    private UserAdapter userAdapter;
    private UserAdapter.RecyclerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Blood Donation");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this,binding.drawerLayout,
                binding.toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        setAdapter();


        DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type =snapshot.child("type").getValue().toString();
                if(type.equals("donor")){
                    readRecipient();
                }else{
                    readDonor();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        nav_profile_image = binding.navView.getHeaderView(0).findViewById(R.id.nav_user_image);
        nav_fullname = binding.navView.getHeaderView(0).findViewById(R.id.nav_user_fullname);
        nav_email = binding.navView.getHeaderView(0).findViewById(R.id.nav_user_email);
        nav_bloodgroup = binding.navView.getHeaderView(0).findViewById(R.id.nav_user_bloodgroup);
        nav_type = binding.navView.getHeaderView(0).findViewById(R.id.nav_user_type);

        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = snapshot.child("name").getValue().toString();
                    nav_fullname.setText(name);
                    String email = snapshot.child("email").getValue().toString();
                    nav_email.setText(email);
                    String bloodgroup = snapshot.child("bloodgroup").getValue().toString();
                    nav_bloodgroup.setText(bloodgroup);
                    String type = snapshot.child("type").getValue().toString();
                    nav_type.setText(type);
                    if(snapshot.hasChild("profilepictureuri")){
                        Glide.with(getApplicationContext()).load(snapshot.child("profilepictureuri").
                                getValue().toString()).into(nav_profile_image);
                    }else{
                        nav_profile_image.setImageResource(R.drawable.profile_picture);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAdapter() {
        setOnClickListener();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        userList=new ArrayList<>();
        userAdapter=new UserAdapter(MainActivity.this,userList,listener);
        binding.recyclerView.setAdapter(userAdapter);
    }

    private void setOnClickListener() {
        listener = new UserAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(getApplicationContext(),DonorOrRecipientActivity.class);
                intent.putExtra("id",userList.get(pos).getId());
                startActivity(intent);
            }
        };
    }

    private void readDonor() {

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("users");

        Query query =reference.orderByChild("type").equalTo("donor");
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user =dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                binding.progressbar.setVisibility(View.GONE);

                if(userList.isEmpty()){
                    Toast.makeText(MainActivity.this,"NO Donors !",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readRecipient() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("users");

        Query query =reference.orderByChild("type").equalTo("recipient");
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user =dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                binding.progressbar.setVisibility(View.GONE);


                if(userList.isEmpty()){
                    Toast.makeText(MainActivity.this,"NO Recipient !",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }







    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.aplus:
                Intent intent2 = new Intent(MainActivity.this, CatagorySelectedActivity.class);
                intent2.putExtra("group","A+");
                startActivity(intent2);
                break;

            case R.id.compatible:
                Intent intent10 = new Intent(MainActivity.this, CatagorySelectedActivity.class);
                intent10.putExtra("group","Compatible With Me");
                startActivity(intent10);
                break;

            case R.id.aminus:
                Intent intent3 = new Intent(MainActivity.this, CatagorySelectedActivity.class);
                intent3.putExtra("group","A-");
                startActivity(intent3);
                break;

            case R.id.bplus:
                Intent intent4 = new Intent(MainActivity.this, CatagorySelectedActivity.class);
                intent4.putExtra("group","B+");
                startActivity(intent4);
                break;

            case R.id.bminus:
                Intent intent5 = new Intent(MainActivity.this, CatagorySelectedActivity.class);
                intent5.putExtra("group","B-");
                startActivity(intent5);
                break;

            case R.id.abplus:
                Intent intent6 = new Intent(MainActivity.this, CatagorySelectedActivity.class);
                intent6.putExtra("group","AB+");
                startActivity(intent6);
                break;

            case R.id.abminus:
                Intent intent7 = new Intent(MainActivity.this, CatagorySelectedActivity.class);
                intent7.putExtra("group","AB-");
                startActivity(intent7);
                break;

            case R.id.oplus:
                Intent intent8 = new Intent(MainActivity.this, CatagorySelectedActivity.class);
                intent8.putExtra("group","O+");
                startActivity(intent8);
                break;

            case R.id.ominus:
                Intent intent9 = new Intent(MainActivity.this, CatagorySelectedActivity.class);
                intent9.putExtra("group","O-");
                startActivity(intent9);
                break;

            case R.id.profile:
                Intent intent=new Intent(MainActivity.this, profileActivity.class);
                startActivity(intent);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(MainActivity.this,loginActivity.class);
                startActivity(intent1);
                break;
        }
           binding.drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }
}