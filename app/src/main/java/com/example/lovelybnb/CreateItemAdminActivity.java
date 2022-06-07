package com.example.lovelybnb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CreateItemAdminActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference itemRef;
    String itemIdForCreate;
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item_admin);

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        itemRef = firebaseDatabase.getReference("Items");

        getNewItemKeyForCreate();


    }

    private void getNewItemKeyForCreate() {
        itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<String>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    arrayList.add(dataSnapshot.getKey());
                }
                //get last itemId in item and create id for new item
                String itemidString = arrayList.get(arrayList.size()-1);
                int itemidInt = Integer.parseInt(itemidString) +1;
                itemIdForCreate = Integer.toString(itemidInt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}