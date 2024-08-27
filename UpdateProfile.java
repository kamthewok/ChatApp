package com.example.secchatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    private EditText mupdateusername;
    private ImageView mgetupdateuserimageview;

    private StorageReference storageReference;

    private String imageAccessTokenUri;

    private androidx.appcompat.widget.Toolbar mtoolbarofuppdate;

    private ImageButton mbackofupdate;

    Button mupdatebutton;

    ProgressBar mprogressbarofupdate;

    private Uri imagepath;

    Intent intent;

    private static int PICK_IMAGE=123;
    String updatename;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        mupdateusername = findViewById(R.id.getupdateusername);
        mgetupdateuserimageview = findViewById(R.id.getupdateuserimageview);
        mtoolbarofuppdate = findViewById(R.id.toolbarofupdate);
        mbackofupdate = findViewById(R.id.backofupdate);
        mprogressbarofupdate = findViewById(R.id.progressbarupdate);
        mupdatebutton = findViewById(R.id.updateyourprofilebutton);

        intent = getIntent();

        setSupportActionBar(mtoolbarofuppdate);

        mbackofupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(UpdateProfile.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mupdateusername.setText(intent.getStringExtra("nameofuser"));

        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        mupdatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updatename = mupdateusername.getText().toString();

                if(updatename.isEmpty()){

                    Toast.makeText(getApplicationContext(), "Name is empty", Toast.LENGTH_SHORT).show();

                } else if (imagepath != null) {

                    mprogressbarofupdate.setVisibility(View.VISIBLE);
                    UserProfile muserprofile = new UserProfile(updatename, firebaseAuth.getUid());
                    databaseReference.setValue(muserprofile);

                    updateimagetostorage();

                    Toast.makeText(getApplicationContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                    mprogressbarofupdate.setVisibility(View.INVISIBLE);

                    Intent intent = new Intent(UpdateProfile.this, Chat.class);
                    startActivity(intent);
                    finish();

                }
                else {

                    mprogressbarofupdate.setVisibility(View.VISIBLE);
                    UserProfile muserprofile = new UserProfile(updatename, firebaseAuth.getUid());
                    databaseReference.setValue(muserprofile);

                    updatenameonfirestore();

                    Toast.makeText(getApplicationContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                    mprogressbarofupdate.setVisibility(View.INVISIBLE);

                    Intent intent = new Intent(UpdateProfile.this, Chat.class);
                    startActivity(intent);
                    finish();


                }
            }
        });

        mgetupdateuserimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        storageReference = firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageAccessTokenUri = uri.toString();
                Picasso.get().load(uri).into(mgetupdateuserimageview);
            }
        });


    }

    private void updatenameonfirestore() {

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String, Object> userdata = new HashMap<>();

        userdata.put("name", updatename);
        userdata.put("image", imageAccessTokenUri);
        userdata.put("uid", firebaseAuth.getUid());
        userdata.put("status", "Online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Profile updated successfully", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void updateimagetostorage() {

        StorageReference imageReference = storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Picture");
        //compress
        Bitmap bitmap = null;
        try{
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
        }
        catch (IOException e){

            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        //putting image to storage

        UploadTask uploadTask = imageReference.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageAccessTokenUri = uri.toString();
                        Toast.makeText(getApplicationContext(), "URI success", Toast.LENGTH_SHORT).show();
                        updatenameonfirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "URI failed", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getApplicationContext(), "Image updated successfully", Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Image not updated", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            imagepath = data.getData();
            mgetupdateuserimageview.setImageURI(imagepath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status", "Offline").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "User offline", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status", "Online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "User online", Toast.LENGTH_SHORT).show();
            }
        });
    }

}