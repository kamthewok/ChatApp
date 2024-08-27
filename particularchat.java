package com.example.secchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class particularchat extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    EditText mtypemessage;
    ImageButton msendbutton;
    CardView msendmessagecardview;
    ImageView mimageviewofparticularuser;
    TextView mnameofparticularuser;
    Toolbar mtoolbarofparticularchat;

    private String typedmessage;
    Intent intent;
    String mrecivername, mreciveruid, msenderuid;

    String reciverroom, senderroom;
    ImageButton mbackofparticularchat;

    RecyclerView mmessagerecyclerview;
    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    MessagesAdapter messagesAdapter;
    ArrayList<Messages> messagesArrayList;






    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particularchat);

        mtypemessage = findViewById(R.id.typemessage);
        msendmessagecardview = findViewById(R.id.cardviewofsendmessage);
        msendbutton = findViewById(R.id.imageviewsendmessage);
        mtoolbarofparticularchat = findViewById(R.id.toolbarofparticularchat);
        mnameofparticularuser = findViewById(R.id.nameofparticularuser);
        mimageviewofparticularuser = findViewById(R.id.particularuserimageinimageview);
        mbackofparticularchat = findViewById(R.id.backofparticularchat);

        messagesArrayList = new ArrayList<>();
        mmessagerecyclerview = findViewById(R.id.recyclerviewofparticularchat);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessagerecyclerview.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(particularchat.this, messagesArrayList);
        mmessagerecyclerview.setAdapter(messagesAdapter);

        intent = getIntent();
        setSupportActionBar(mtoolbarofparticularchat);

        mtoolbarofparticularchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Toolbar clicked", Toast.LENGTH_SHORT).show();


            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");

        msenderuid = firebaseAuth.getUid();
        mreciveruid = getIntent().getStringExtra("reciveruid");
        mrecivername = getIntent().getStringExtra("name");

        senderroom = msenderuid+mreciveruid;
        reciverroom = mreciveruid+msenderuid;

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Chats").child(senderroom).child("messages");
        messagesAdapter = new MessagesAdapter(particularchat.this, messagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesArrayList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()) {

                    Messages messages = snapshot1.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        mbackofparticularchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(particularchat.this, Chat.class);
                startActivity(intent);
                finish();
            }
        });

        mnameofparticularuser.setText(mrecivername);

        String uri = intent.getStringExtra("imageuri");

        if(uri.isEmpty()){
            Toast.makeText(getApplicationContext(),"null recived", Toast.LENGTH_SHORT).show();
        }
        else{
            Picasso.get().load(uri).into(mimageviewofparticularuser);
        }


        msendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                typedmessage = mtypemessage.getText().toString();

                if(typedmessage.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Type message first", Toast.LENGTH_SHORT).show();
                }
                else{

                    Date date = new Date();
                    currenttime = simpleDateFormat.format(calendar.getTime());

                    Messages messages = new Messages(typedmessage,firebaseAuth.getUid(),date.getTime(),currenttime);

                    firebaseDatabase = FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("Chats")
                            .child(senderroom)
                            .child("messages")
                            .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    firebaseDatabase.getReference()
                                            .child("Chats")
                                            .child(reciverroom)
                                            .child("messages")
                                            .push()
                                            .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                }
                            });

                    mtypemessage.setText(null);

                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (messagesAdapter != null) {
            messagesAdapter.notifyDataSetChanged();
        }
    }

}