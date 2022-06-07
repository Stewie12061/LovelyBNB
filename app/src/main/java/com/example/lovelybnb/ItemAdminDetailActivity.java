package com.example.lovelybnb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.lovelybnb.Data.ItemDetail;
import com.example.lovelybnb.Data.PropertyItems;
import com.example.lovelybnb.Data.PropertyType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemAdminDetailActivity extends AppCompatActivity {
    String itemId, itemName, imageName, HostAvatar;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference itemDetailRef, sliderRef;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    TextView goback,itemNameDetail;
    ImageSlider imageSlider;
    Uri uri = null;


    EditText hostName, hostMail, hostPhone, address;
    TextView adCheckInTime,adCheckOutTime;
    CircleImageView adAvater;

    Button updateItemAdDetail, editImgSlider, itemSelectImg, itemUploadImg;
    private int PICK_IMG_REQUEST = 1705;
    ItemDetail itemDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_admin_detail);

        itemId = getIntent().getStringExtra("itemAdId");
        itemName = getIntent().getStringExtra("itemAdName");

        itemNameDetail = findViewById(R.id.itemNameDetail);
        itemNameDetail.setText(itemName);

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        itemDetailRef = firebaseDatabase.getReference("Item Detail");
        sliderRef = firebaseDatabase.getReference("Slider");

        imageSlider = findViewById(R.id.sliderAd);
        final List<SlideModel> slideModelArrayList = new ArrayList<>();
        getDetailImgSlider(itemId, slideModelArrayList);

        hostName = findViewById(R.id.edtHostName);
        hostMail = findViewById(R.id.edtHostMail);
        hostPhone = findViewById(R.id.edtHostPhone);
        address = findViewById(R.id.edtAdminAddress);
        adCheckInTime = findViewById(R.id.AdCheckInTime);
        adCheckOutTime = findViewById(R.id.AdCheckOutTime);
        adAvater = findViewById(R.id.adAvatar);
        getdetailHost(itemId);

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateItemAdDetail = findViewById(R.id.updateItemAdDetail);
        updateItemAdDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemDetail();
            }
        });

        itemDetail = new ItemDetail();

        itemSelectImg = findViewById(R.id.itemSelectImg);
        itemSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImg();
            }
        });
        itemUploadImg = findViewById(R.id.itemUploadImg);
        itemUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri!=null){
                    ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(ItemAdminDetailActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.dialog_progress);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    // get the Firebase  storage reference
                    firebaseStorage = FirebaseStorage.getInstance();
                    storageReference = firebaseStorage.getReference();

                    imageName = UUID.randomUUID().toString();
                    StorageReference imageFolder = storageReference.child("Images/items/"+imageName);

                    //put img to storage
                    imageFolder.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ItemAdminDetailActivity.this,"Upload sussessed",Toast.LENGTH_SHORT).show();

                            //get uri img from storage
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    itemDetail = new ItemDetail(address.getText().toString(),adCheckInTime.getText().toString(),adCheckOutTime.getText().toString(), uri.toString(),hostMail.getText().toString(),hostName.getText().toString(),hostPhone.getText().toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ItemAdminDetailActivity.this,"Upload failed",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void chooseImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMG_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMG_REQUEST && resultCode == Activity.RESULT_OK
                && data!=null && data.getData() != null){
            uri = data.getData();
            if (null != uri) {
                // update the preview image in the layout
                adAvater.setImageURI(uri);
            }
        }
    }

    private void updateItemDetail() {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(ItemAdminDetailActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (uri==null){
            itemDetail = new ItemDetail(address.getText().toString(),adCheckInTime.getText().toString(),adCheckOutTime.getText().toString(), HostAvatar,hostMail.getText().toString(),hostName.getText().toString(),hostPhone.getText().toString());
            itemDetailRef.child(itemId).setValue(itemDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(ItemAdminDetailActivity.this,MainAdminActivity.class);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            itemDetailRef.child(itemId).setValue(itemDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(ItemAdminDetailActivity.this,MainAdminActivity.class);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }



    private void getDetailImgSlider(String itemId, List<SlideModel> slideModelArrayList) {

        sliderRef.child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    slideModelArrayList.add(new SlideModel(dataSnapshot.child("url").getValue().toString(), ScaleTypes.CENTER_CROP));
                    imageSlider.setImageList(slideModelArrayList, ScaleTypes.CENTER_CROP);
                    imageSlider.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemSelected(int i) {
                            Intent intent = new Intent(getApplicationContext(),ImageDetailActivity.class);
                            intent.putExtra("itemId",itemId);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_from_top,R.anim.slide_to_bottom);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getdetailHost(String itemId) {
        itemDetailRef.child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String HostName = snapshot.child("hostName").getValue().toString();
                String HostEmail = snapshot.child("hostEmail").getValue().toString();
                String HostPhone = snapshot.child("hostPhone").getValue().toString();
                HostAvatar = snapshot.child("hostAvatar").getValue().toString();
                String ItemAddress = snapshot.child("address").getValue().toString();
                String CheckIn = snapshot.child("checkIn").getValue().toString();
                String CheckOut = snapshot.child("checkOut").getValue().toString();

                hostName.setText(HostName);
                hostMail.setText(HostEmail);
                hostPhone.setText(HostPhone);
                address.setText(ItemAddress);
                adCheckInTime.setText(CheckIn);
                adCheckOutTime.setText(CheckOut);
                Picasso.get().load(HostAvatar).into(adAvater);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}