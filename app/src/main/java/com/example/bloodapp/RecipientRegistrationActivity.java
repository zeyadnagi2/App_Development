package com.example.bloodapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloodapp.databinding.ActivityRecipientRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RecipientRegistrationActivity extends AppCompatActivity {

    ActivityRecipientRegistrationBinding binding;
    private Uri resultUri;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recipient_registration);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        binding.backbutton.setOnClickListener(view -> {
            Intent intent = new Intent(RecipientRegistrationActivity.this, loginActivity.class);
            startActivity(intent);
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        binding.registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = binding.registerEmail.getText().toString().trim();
                final String password = binding.registerPassword.getText().toString().trim();
                final String fullName = binding.registerFullName.getText().toString().trim();
                final String idNumber = binding.registerIdNumber.getText().toString().trim();
                final String phoneNumber = binding.registerPhoneNumber.getText().toString().trim();
                final String bloodGroup = binding.BloodGroupSpinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(email)) {
                    binding.registerEmail.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    binding.registerPassword.setError("Password is required!");
                    return;
                }
                if (TextUtils.isEmpty(fullName)) {
                    binding.registerFullName.setError("Full name is required!");
                    return;
                }
                if (TextUtils.isEmpty(idNumber)) {
                    binding.registerIdNumber.setError("ID number is required!");
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)) {
                    binding.registerPhoneNumber.setError("Phone number is required!");
                    return;
                }
                if (bloodGroup.equals("Select Your Blood Group")) {
                    Toast.makeText(RecipientRegistrationActivity.this, "Select blood group",Toast.LENGTH_LONG).show();
                    return;
                } else {
                    loader.setMessage("Registering you...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                String error = task.getException().toString();
                                Toast.makeText(RecipientRegistrationActivity.this,"Error "+ error,Toast.LENGTH_LONG).show();
                            }
                            else{
                                String currentUserID = mAuth.getCurrentUser().getUid();
                                mRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);
                                HashMap userInfo = new HashMap();
                                userInfo.put("id",currentUserID);
                                userInfo.put("name",fullName);
                                userInfo.put("email",email);
                                userInfo.put("idnumber",idNumber);
                                userInfo.put("phonenumber",phoneNumber);
                                userInfo.put("bloodgroup",bloodGroup);
                                userInfo.put("type","recipient");
                                userInfo.put("search","recipient"+ bloodGroup);

                                mRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(RecipientRegistrationActivity.this,"Data set Successfully",Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(RecipientRegistrationActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                                        }
                                        finish();
                                        //loader.dismiss();
                                    }
                                });
                                if(resultUri != null){
                                    final StorageReference filePath = FirebaseStorage.getInstance().getReference()
                                            .child("profile images").child(currentUserID);
                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);

                                    }catch(IOException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    assert bitmap != null;
                                    bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
                                    byte[] data = byteArrayOutputStream.toByteArray();
                                    UploadTask uploadTask = filePath.putBytes(data);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RecipientRegistrationActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            if(taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference()!= null){
                                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String imageUri = uri.toString();
                                                        Map newImageMap = new HashMap();
                                                        newImageMap.put("profilepictureuri",imageUri);
                                                        mRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(RecipientRegistrationActivity.this, "Image url added to database", Toast.LENGTH_SHORT).show();
                                                                }else{
                                                                    Toast.makeText(RecipientRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                        finish();
                                                    }
                                                });
                                            }

                                        }
                                    });
                                    Intent intent = new Intent(RecipientRegistrationActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    loader.dismiss();

                                }

                            }

                        }
                    });

                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data !=null)
        {
            resultUri = data.getData();
            binding.profileImage.setImageURI(resultUri);
        }
    }
}