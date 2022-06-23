package com.example.lovelybnb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lovelybnb.Adapter.MessageAdapter;
import com.example.lovelybnb.Data.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdminActivity extends AppCompatActivity {

    String customerId;

    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef, chatRef;
    String currentUserId;

    TextView adminNameMessage;
    CircleImageView adAvatar;

    TextView goback;

    EditText edtMessage;
    ImageButton btnSendMess;

    RecyclerView rvMessageAdmin;
    MessageAdapter messageAdapter;
    ArrayList<Chat> chatArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_admin);

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        userRef = firebaseDatabase.getReference("Registered users");
        chatRef = firebaseDatabase.getReference("Chats");

        user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        adminNameMessage = findViewById(R.id.adminNameMessage);
        adAvatar = findViewById(R.id.adAvatarMessage);

        customerId = getIntent().getStringExtra("idCustomer");

        edtMessage = findViewById(R.id.edtMessage);
        btnSendMess = findViewById(R.id.btnSendMess);
        btnSendMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = edtMessage.getText().toString();
                if (!mess.equals("")){
                    sendMessage(currentUserId,customerId,mess);
                }else {
                    Toast.makeText(getApplicationContext(),"You have to write something first",Toast.LENGTH_SHORT).show();
                }
                edtMessage.setText("");
            }
        });

        rvMessageAdmin = findViewById(R.id.rvMessageAdmin);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setStackFromEnd(true);
        rvMessageAdmin.setLayoutManager(linearLayoutManager);

        userRef.child(customerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminNameMessage.setText(snapshot.child("FullName").getValue().toString());
                getMessage(currentUserId,customerId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation,R.anim.zoom_out);
    }


    private void sendMessage(String sender, String receiver, String message){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);


        chatRef.push().setValue(hashMap);
    }

    private void getMessage(String currentUserId, String adId){
        chatArrayList = new ArrayList<>();
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatArrayList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(currentUserId) && chat.getSender().equals(adId) ||
                            chat.getReceiver().equals(adId) && chat.getSender().equals(currentUserId)){
                        chatArrayList.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageAdminActivity.this,chatArrayList);
                    rvMessageAdmin.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}