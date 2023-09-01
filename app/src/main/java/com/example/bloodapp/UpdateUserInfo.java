package com.example.bloodapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.bloodapp.databinding.ActivityUpdateUserInfoBinding;
import com.example.bloodapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UpdateUserInfo extends AppCompatActivity {

    ActivityUpdateUserInfoBinding binding;
    private Uri resultUri;
    private User user;
    private ProgressDialog loader;
    private DatabaseReference reference;
    private FirebaseAuth mauth;
    private Boolean flag;
    private AuthCredential authCredential;
    String name, email, phoneNumber, type, bloodGroup, IDnumber, passwordOld, passwordNew;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_user_info);
        setSupportActionBar(binding.edtToolbar);
        loader = new ProgressDialog(this);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Update Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        reference = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid());
        mauth = FirebaseAuth.getInstance();
        flag = false;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.registerFullName.setText(snapshot.child("name").getValue().toString());
                    binding.registerIdNumber.setText(snapshot.child("idnumber").getValue().toString());
                    binding.registerEmail.setText(snapshot.child("email").getValue().toString());
                    binding.registerPhoneNumber.setText(snapshot.child("phonenumber").getValue().toString());
                    if (snapshot.hasChild("profilepictureuri")) {
                        Glide.with(getApplicationContext()).load(snapshot.child("profilepictureuri").
                                getValue().toString()).into(binding.edtProfileImage);
                    } else {
                        binding.edtProfileImage.setImageResource(R.drawable.profile_picture);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.edtProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        binding.changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == false) {
                    binding.changePassword.setText("Hide");
                    flag = true;
                    binding.passNew.setVisibility(View.VISIBLE);
                    binding.passOld.setVisibility(View.VISIBLE);

                }
                else
                {
                    binding.changePassword.setText("Change Password?");
                    flag = false;
                    binding.passNew.setVisibility(View.GONE);
                    binding.passOld.setVisibility(View.GONE);

                }
            }
        });
        binding.btnUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                name = binding.registerFullName.getText().toString().trim();
                IDnumber = binding.registerIdNumber.getText().toString().trim();
                email = binding.registerEmail.getText().toString().trim();
                phoneNumber = binding.registerPhoneNumber.getText().toString().trim();
                bloodGroup = binding.BloodGroupSpinner.getSelectedItem().toString();
                type = binding.typeSpinner.getSelectedItem().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    binding.registerEmail.setError("Email is required!");
                    return;
                }
                if (flag) {
                    passwordOld = binding.registerPasswordOld.getText().toString().trim();
                    passwordNew = binding.registerPasswordNew.getText().toString().trim();

                    if (TextUtils.isEmpty(passwordOld)) {
                        binding.registerPasswordOld.setError("Password is required!");
                        return;
                    }
                    if (TextUtils.isEmpty(passwordNew)) {
                        binding.registerPasswordNew.setError("Password is required!");
                        return;
                    }
                    authCredential = EmailAuthProvider.getCredential(email, passwordOld);
                    Objects.requireNonNull(mauth.getCurrentUser()).reauthenticate(authCredential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mauth.getCurrentUser().updatePassword(passwordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "Update successful ", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Update fail ", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Update auth fail ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                if (TextUtils.isEmpty(name)) {
                    binding.registerFullName.setError("Full name is required!");
                    return;
                }
                if (TextUtils.isEmpty(IDnumber)) {
                    binding.registerIdNumber.setError("ID number is required!");
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)) {
                    binding.registerPhoneNumber.setError("Phone number is required!");
                    return;
                }
                if (bloodGroup.equals("Select your blood group")) {
                    Toast.makeText(UpdateUserInfo.this, "Select blood group", Toast.LENGTH_LONG).show();
                    return;
                }
                if (type.equals("Select Your New Type")) {
                    Toast.makeText(UpdateUserInfo.this, "Select Your Type", Toast.LENGTH_LONG).show();
                    return;
                }

                loader.setMessage("Updating you...");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                HashMap userInfo = new HashMap();
                userInfo.put("name", name);
                userInfo.put("email", email);
                userInfo.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                userInfo.put("idnumber", IDnumber);
                userInfo.put("phonenumber", phoneNumber);
                userInfo.put("bloodgroup", bloodGroup);
                userInfo.put("type", type);
                userInfo.put("search", type + bloodGroup);
                setImage(resultUri);
                updateEmail(email);
                // updatePassword(password);
                reference.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Update successful", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });


        binding.btnBackInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateUserInfo.this, profileActivity.class);
                startActivity(intent);
                finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            resultUri = data.getData();
            binding.edtProfileImage.setImageURI(resultUri);
        }
    }

    private void setImage(Uri resultUri) {

        if (resultUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference()
                    .child("profile images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateUserInfo.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUri = uri.toString();
                                Map newImageMap = new HashMap();
                                newImageMap.put("profilepictureuri", imageUri);
                                reference.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(UpdateUserInfo.this, "Image url added to database", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(UpdateUserInfo.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                finish();
                            }
                        });
                    }

                }
            });
        } else {
            resultUri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        }
    }

    private void updateEmail(String email) {
        mauth.getCurrentUser().updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loader.dismiss();

                }
            }
        });
    }

    private void updatePassword(String password) {
        mauth.getCurrentUser().updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loader.dismiss();
                }
            }
        });
    }
}


