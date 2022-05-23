package com.example.lovelybnb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReceiptActivity extends AppCompatActivity {
    TextView guest, day, receiptName, receiptTimeCheckin, receiptTimecheckout, receiptPrice, goback, dayStay, peopleQuantity, receiptAddress, receiptPlace, receiptContact, receiptDaycheckin, receiptDaycheckout ;
    Button Cancel;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef,sliderRef;
    String itemId, currentUserId;
    ImageSlider imageSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        userRef = firebaseDatabase.getReference("Registered users");
        sliderRef = firebaseDatabase.getReference("Slider");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        guest = findViewById(R.id.guest);
        day = findViewById(R.id.day);
        receiptName = findViewById(R.id.receiptName);
        receiptPrice = findViewById(R.id.receiptPrice);
        receiptDaycheckout = findViewById(R.id.receiptCheckOutDay);
        receiptDaycheckin = findViewById(R.id.receiptCheckInDay);
        receiptTimeCheckin = findViewById(R.id.receiptCheckInTime);
        receiptTimecheckout = findViewById(R.id.receiptCheckOutTime);
        dayStay = findViewById(R.id.dayStay);
        peopleQuantity = findViewById(R.id.peopleQuantity);
        receiptAddress = findViewById(R.id.receiptAddress);
        receiptPlace = findViewById(R.id.receiptPlace);
        receiptContact = findViewById(R.id.receiptContact);

        itemId = getIntent().getStringExtra("itemId");

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Cancel = findViewById(R.id.cancelBooking);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.child(currentUserId).child("Trip").child(itemId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(),"Cancel reservation",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        int idTrip = R.id.mnuTrip;
                        String IDtrip = Integer.toString(idTrip);
                        intent.putExtra("Fragment",IDtrip);
                        startActivity(intent);
                    }
                });

            }
        });
        imageSlider = findViewById(R.id.imageSlider);
        final List<SlideModel> slideModelArrayList = new ArrayList<>();
        getDetailImgSlider(itemId,slideModelArrayList);
        getReceiptData();
    }

    private void getDetailImgSlider(String itemId, List<SlideModel> slideModelArrayList) {

        sliderRef.child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    slideModelArrayList.add(new SlideModel(dataSnapshot.child("url").getValue().toString(), ScaleTypes.CENTER_CROP));
                    imageSlider.setImageList(slideModelArrayList, ScaleTypes.CENTER_CROP);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getReceiptData() {
        userRef.child(currentUserId).child("Trip").child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String daystay = snapshot.child("dayStay").getValue().toString();
                String peoplequantity = snapshot.child("peopleQuantity").getValue().toString();
                String receiptaddress = snapshot.child("receiptAddress").getValue().toString();
                String receiptcontact = snapshot.child("receiptContact").getValue().toString();
                String receiptdaycheckin = snapshot.child("receiptDaycheckin").getValue().toString();
                String receiptdaycheckout = snapshot.child("receiptDaycheckout").getValue().toString();
                String receiptname = snapshot.child("receiptName").getValue().toString();
                String receiptplace = snapshot.child("receiptPlace").getValue().toString();
                String receiptprice = snapshot.child("receiptPrice").getValue().toString();
                String receipttimecheckin = snapshot.child("receiptTimeCheckin").getValue().toString();
                String receipttimecheckout = snapshot.child("receiptTimeCheckout").getValue().toString();

                dayStay.setText(daystay);
                peopleQuantity.setText(peoplequantity);
                receiptAddress.setText(receiptaddress);
                receiptContact.setText(receiptcontact);
                receiptDaycheckin.setText(receiptdaycheckin);
                receiptDaycheckout.setText(receiptdaycheckout);
                receiptName.setText(receiptname);
                receiptPlace.setText(receiptplace);
                receiptPrice.setText(receiptprice);
                receiptTimeCheckin.setText(receipttimecheckin);
                receiptTimecheckout.setText(receipttimecheckout);

                int dayint = Integer.parseInt(daystay);
                int guestint = Integer.parseInt(peoplequantity);
                if (dayint==1){
                    day.setText("DAY");
                }
                if (guestint==1){
                    guest.setText("GUEST");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}